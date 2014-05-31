package cn.phoniex.ssg.service;

import java.util.ArrayList;
import java.util.List;

import cn.phoniex.ssg.AppLockActivity;
import cn.phoniex.ssg.AppLockPopupActivity;
import cn.phoniex.ssg.MyApplication;
import cn.phoniex.ssg.dao.AppLockDao;
import cn.phoniex.ssg.domain.Appinfos;

import cn.phoniex.ssg.R;
import android.R.string;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

public class CityGuardService extends Service {

	private static final String TAG = "CityGuardService";
	private List<String> guardList;
	private List<Appinfos> appinfoList;
	private AppLockDao dao;
	private CgBinder mBinder;
	private long timeleft = 5*60*1000;
	private long lasttime;
	private int cgmode;
	private boolean bStart;
	private ActivityManager am;
	private SharedPreferences sp;
	private KeyguardManager km;
	private Intent cgIntent;
	private boolean  bByMe = false;
	private int notifyId = 96310;
	
	
	@Override
	public IBinder onBind(Intent intent) {
		
		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// 从 Application中获取全局保存的Notification Id
//		MyApplication myApp = (MyApplication) getApplicationContext();
//		if (myApp.getNotifyId() != 0) {
//			notifyId = myApp.getNotifyId();
//		}
		Log.d(TAG, "onCreate");
		// 为什么没有new binder 对象
		mBinder = new CgBinder();
		SetServiceForeground();
		lasttime = System.currentTimeMillis();
		// 注册一个关于数据库的内容观察者
		Uri uri;
		//notifyForDescendents 设计为true 监视派生的uri
		//getContentResolver().registerContentObserver(uri, true, new myObserver())
		dao = new AppLockDao(this);
		guardList = new ArrayList<String>();
		guardList = dao.getAllLockedApps();
		
		appinfoList = new ArrayList<Appinfos>();
		initAppinfosList(guardList);
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		cgmode  = sp.getInt("cgmode", 2);
		bStart = sp.getBoolean("apploctstart", true);
		 cgIntent = new Intent(this,AppLockPopupActivity.class);
		//服务中启动Actiity需要设置 新的任务栈标志
		cgIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		km  = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		new Thread(){

			@Override
			public void run() {
				
				
				while (bStart) {
					try {
						if (km.inKeyguardRestrictedInputMode()) {
							LockAllPkg();
							//如果是锁屏休息10秒
							sleep(10*1000);
						}
						//只获得当前运行的最近的一个任务
						List<RunningTaskInfo> taskInfos  = am.getRunningTasks(1);
						//analysisBehaver(taskInfos);
						//获取最近的任务
						RunningTaskInfo currentInfo = taskInfos.get(0);
						String  actStr = currentInfo.topActivity.toString();
						//Log.d(TAG, actStr);
						String currpkgname = currentInfo.topActivity.getPackageName().toString();
						if (guardList.contains(currpkgname)) {
							for (int i = 0; i < appinfoList.size(); i++) {
								if (appinfoList.get(i).getPackageName().trim().equals(currpkgname.trim())) {
									if (appinfoList.get(i).isbLock()) {
										if (appinfoList.get(i).getTimeleft() == 0) {
											cgIntent.putExtra("pkgname", currpkgname);
											startActivity(cgIntent);
										}
									}
								}
							}

						}else {
							sleep(5*1000);
						}
						//每次刷新app列表 里面的剩余时间
						reflashAppinfos(guardList);
						super.run();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
		}.start();
		
	}


	public   void SetServiceForeground() {
		NotificationCompat.Builder  notifyBuilder = 
				new NotificationCompat.Builder(this)
		.setContentText("CityGuard Niubility")
		.setContentTitle("CityGuard")
		.setSmallIcon(R.drawable.icon_shleld);
		//不设置 Notify响应的 Activity 应该可以吧？
		TaskStackBuilder  taskBuilder = TaskStackBuilder.create(this);
		
		Intent intent  = new Intent(this,AppLockActivity.class);
		
		taskBuilder.addParentStack(AppLockActivity.class);
		taskBuilder.addNextIntent(intent);
		PendingIntent pendingIntent = taskBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		
		notifyBuilder.setContentIntent(pendingIntent);
		NotificationManager nManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		nManager.notify(notifyId, notifyBuilder.build());
		
		startForeground(notifyId, notifyBuilder.build());
	}
	
	protected void analysisBehaver(List<RunningTaskInfo> taskInfos) {
		//保存到数据库中
	}

	protected void LockAllPkg() {

		for (int i = 0; i < guardList.size(); i++) {
			appinfoList.get(i).setTimeleft(0);
			appinfoList.get(i).setbLock(true);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// 判断是否为自己的结束请求
		if (bByMe) {
			bStart = false;
			super.onDestroy();
		}else {//非我的请求，重启服务
			Intent intent = new Intent();
			intent.setClass(this, CityGuardService.class);
			startService(intent);
		}

	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}
	
	public class CgBinder extends Binder
	{
		public void tmpStopProItem(String pkgname)
		{
			cgTempStopItem(pkgname);
		}
		public void StopProItem(String pkgname)
		{
			cgStopProItem(pkgname);
		}
		public void StartProItem(String pkgname)
		{
			cgStartProItem(pkgname);
		}
		
	}
	//设置存活时间 临时停止保护
	public void cgTempStopItem(String pkgname)
	{
		//如果包名列表里面存着该包名 去重置appinfolist里面的timeleft
		if (guardList.contains(pkgname)) {
			for (int i = 0; i < appinfoList.size(); i++) {
				if (appinfoList.get(i).getPackageName().equals(pkgname)) {
					appinfoList.get(i).setTimeleft(timeleft);
				}
			}
		}
		
	}
//
	public void cgStopProItem(String pkgname) {

		if (guardList.contains(pkgname)) {
			//从包名列表中移除
			guardList.remove(pkgname);
			//从Appinfos列表中移除
			for (int i = 0; i < appinfoList.size(); i++) {
				if (appinfoList.get(i).getPackageName().equals(pkgname)) {
					appinfoList.remove(i);
				}
			}

		}
		if (dao.find(pkgname)) {
			//暂时更改为 内容观察者方式 检测数据库改变 然后刷新列表
			//从数据库中移除
			dao.delete(pkgname);
		}
		
	}

	public void cgStartProItem(String pkgname) {
		//添加到包名列表
		if (pkgname != null) {
			
			guardList.add(pkgname);
			//添加到Appinfos列表
			Appinfos info = new Appinfos();
			info.setPackageName(pkgname);
			info.setbLock(true);
			info.setTimeleft(0);
			appinfoList.add(info);
			//添加到数据库中
			dao.add(pkgname);
		}

		
	}

	
	//重新刷新Appinfos列表中的剩余时间
	public void reflashAppinfos(List<String> pkgnames)
	{
		
		long offtime = System.currentTimeMillis() - lasttime;
		for (int j = 0; j < pkgnames.size(); j++) {
			
			if (offtime < 0) {
				//如果时间差错误重新刷新
				appinfoList.get(j).setTimeleft(timeleft);
			}else {
				//将列表中的剩余时间取出后减去时间长 如果结果为负数则设为0
				long curleft = appinfoList.get(j).getTimeleft();
				if ((curleft - offtime) < 0) {
					appinfoList.get(j).setTimeleft(0);
				}else {
					appinfoList.get(j).setTimeleft(curleft - offtime);
				}
			}
		}
		lasttime = System.currentTimeMillis();
	}
	
	// 初始化Appinfos 列表剩余时间
	public void initAppinfosList(List<String> pkgnames)
	{
		for (int j = 0; j < pkgnames.size(); j++) {
			Appinfos info = new Appinfos();
			info.setPackageName(pkgnames.get(j));
			// 初始化为0 理科锁定，而不是初始化为用户设置的时间
			info.setTimeleft(0);
			info.setbLock(true);
			appinfoList.add(info);
			info = null;
		}

	}

}
