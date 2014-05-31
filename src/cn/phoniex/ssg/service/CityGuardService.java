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
		// �� Application�л�ȡȫ�ֱ����Notification Id
//		MyApplication myApp = (MyApplication) getApplicationContext();
//		if (myApp.getNotifyId() != 0) {
//			notifyId = myApp.getNotifyId();
//		}
		Log.d(TAG, "onCreate");
		// Ϊʲôû��new binder ����
		mBinder = new CgBinder();
		SetServiceForeground();
		lasttime = System.currentTimeMillis();
		// ע��һ���������ݿ�����ݹ۲���
		Uri uri;
		//notifyForDescendents ���Ϊtrue ����������uri
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
		//����������Actiity��Ҫ���� �µ�����ջ��־
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
							//�����������Ϣ10��
							sleep(10*1000);
						}
						//ֻ��õ�ǰ���е������һ������
						List<RunningTaskInfo> taskInfos  = am.getRunningTasks(1);
						//analysisBehaver(taskInfos);
						//��ȡ���������
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
						//ÿ��ˢ��app�б� �����ʣ��ʱ��
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
		//������ Notify��Ӧ�� Activity Ӧ�ÿ��԰ɣ�
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
		//���浽���ݿ���
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
		// �ж��Ƿ�Ϊ�Լ��Ľ�������
		if (bByMe) {
			bStart = false;
			super.onDestroy();
		}else {//���ҵ�������������
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
	//���ô��ʱ�� ��ʱֹͣ����
	public void cgTempStopItem(String pkgname)
	{
		//��������б�������Ÿð��� ȥ����appinfolist�����timeleft
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
			//�Ӱ����б����Ƴ�
			guardList.remove(pkgname);
			//��Appinfos�б����Ƴ�
			for (int i = 0; i < appinfoList.size(); i++) {
				if (appinfoList.get(i).getPackageName().equals(pkgname)) {
					appinfoList.remove(i);
				}
			}

		}
		if (dao.find(pkgname)) {
			//��ʱ����Ϊ ���ݹ۲��߷�ʽ ������ݿ�ı� Ȼ��ˢ���б�
			//�����ݿ����Ƴ�
			dao.delete(pkgname);
		}
		
	}

	public void cgStartProItem(String pkgname) {
		//��ӵ������б�
		if (pkgname != null) {
			
			guardList.add(pkgname);
			//��ӵ�Appinfos�б�
			Appinfos info = new Appinfos();
			info.setPackageName(pkgname);
			info.setbLock(true);
			info.setTimeleft(0);
			appinfoList.add(info);
			//��ӵ����ݿ���
			dao.add(pkgname);
		}

		
	}

	
	//����ˢ��Appinfos�б��е�ʣ��ʱ��
	public void reflashAppinfos(List<String> pkgnames)
	{
		
		long offtime = System.currentTimeMillis() - lasttime;
		for (int j = 0; j < pkgnames.size(); j++) {
			
			if (offtime < 0) {
				//���ʱ����������ˢ��
				appinfoList.get(j).setTimeleft(timeleft);
			}else {
				//���б��е�ʣ��ʱ��ȡ�����ȥʱ�䳤 ������Ϊ��������Ϊ0
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
	
	// ��ʼ��Appinfos �б�ʣ��ʱ��
	public void initAppinfosList(List<String> pkgnames)
	{
		for (int j = 0; j < pkgnames.size(); j++) {
			Appinfos info = new Appinfos();
			info.setPackageName(pkgnames.get(j));
			// ��ʼ��Ϊ0 ��������������ǳ�ʼ��Ϊ�û����õ�ʱ��
			info.setTimeleft(0);
			info.setbLock(true);
			appinfoList.add(info);
			info = null;
		}

	}

}
