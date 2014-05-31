package cn.phoniex.ssg.service;

import java.util.ArrayList;
import java.util.List;

import cn.phoniex.ssg.AppLockActivity;
import cn.phoniex.ssg.AppLockPopupActivity;
import cn.phoniex.ssg.R;
import cn.phoniex.ssg.dao.AppLockDao;
import cn.phoniex.ssg.dao.ProManagerDAO;
import cn.phoniex.ssg.domain.Appinfos;
import cn.phoniex.ssg.service.CityGuardService.CgBinder;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

public class ProManagerService extends Service {

	private static final String TAG = "ProManagerService";
	private List<String> clearProList;
	private List<Appinfos> appinfoList;
	private ProManagerDAO dao;
	private PmBinder mBinder;
	private long timeleft = 5*60*1000;
	private long lasttime;
	private boolean bStart;
	private ActivityManager am;
	private SharedPreferences sp;
	private KeyguardManager km;
	private Intent pmIntent;
	private boolean  bByMe = false;
	
	
	@Override
	public IBinder onBind(Intent intent) {
		
		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");
		lasttime = System.currentTimeMillis();
		
		Uri uri;
		dao  = new ProManagerDAO(this);
	    clearProList  =new ArrayList<String>();
	    clearProList  = dao.getAll();
	    int icut = clearProList.size();
		
		appinfoList = new ArrayList<Appinfos>();
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		
		timeleft  = sp.getInt("timeleft", 5*60*1000);
		bStart = sp.getBoolean("pmstart", true);
		
		am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		km  = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		new Thread(){

			@Override
			public void run() {
				
				
				while (bStart) {
					try {
						// 锁屏清理
						if (km.inKeyguardRestrictedInputMode()) {

							if (bStart) {
								killAllBGPro(clearProList);
							}else {
								sleep(10*1000);
							}
							
						}else {
							sleep(60*1000);
						}
						//固定时间自动清理
						if (bStart) {
							if (System.currentTimeMillis() - lasttime > timeleft) {
								killAllBGPro(clearProList);
							}
						}
						
						super.run();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
		}.start();
		
	}


	protected void killAllBGPro(List<String> lists) {
		
		
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (int i = 0; i <lists.size() ; i++) {
			am.killBackgroundProcesses(lists.get(i));
		}
		//Toast.makeText(this,"清理后台进程"+"个",0).show();
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
			intent.setClass(this, ProManagerService.class);
			startService(intent);
		}

	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}
	
	public class PmBinder extends Binder
	{
		public void clearAll(List<String> lists)
		{
			killAllBGPro(lists);
		}
		
	}
	

}
