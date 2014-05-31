package cn.phoniex.ssg.service;

import java.util.List;

import cn.phoniex.ssg.MainActivity;
import cn.phoniex.ssg.MyApplication;
import cn.phoniex.ssg.R;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class KingsGuardService extends Service {

	private String USERANALYSIS = "UserAnalysisService";
	private String CITYGUARD = "CityGuardService";
	private String PROMANAGER = "ProManagerService";
	private SharedPreferences sp;
	private boolean  buser;
	private boolean bcity;
	private boolean bpm;
	private int notifyId = 66011;
	private MyApplication myApp;
	private Notification notification;
	
	
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}

	@Override
	public void onCreate() {
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		buser = sp.getBoolean("useranalysis", true);
		bpm = sp.getBoolean("pmstart", true);;
		bcity = sp.getBoolean("apploctstart", true);
		myApp = (MyApplication) getApplicationContext();
		if (myApp.getNotifyId() != 0) {
			notifyId = myApp.getNotifyId();
			notification = myApp.getNotification();
			startForeground(notifyId, notification);
		}else {
			
			SetServiceForeground();
		}
		new Thread(){

			@Override
			public void run() {
				while(true){
					if (bcity) {
						if (!isServiceRunning(KingsGuardService.this, CITYGUARD)) {
							Intent cityIntent = new Intent(KingsGuardService.this,CityGuardService.class);
							startService(cityIntent);
						}
					}
					if (bpm) {
						if (!isServiceRunning(KingsGuardService.this, PROMANAGER)) {
							Intent pmIntent = new Intent(KingsGuardService.this,ProManagerService.class);
							startService(pmIntent);
						}
					}
					if (buser) {
						if (!isServiceRunning(KingsGuardService.this, USERANALYSIS)) {
							Intent userIntent = new Intent(KingsGuardService.this,UserAnalysisService.class);
							startService(userIntent);
						}
					}
					super.run();
				}

			}
			
			
		}.start();
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		
		super.onDestroy();
	}
	
	public boolean isServiceRunning(Context context , String ServerClassName){
		
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runList = am.getRunningServices(50);
		if (runList.size() == 0) {
			return false;
		}else {
			for (RunningServiceInfo info : runList) {
				
				if(info.service.getClassName().equals(ServerClassName) == true){
					
					return true;
				}
			}
			
		}
		return false;
	}
	
	public void SetServiceForeground() {
		NotificationCompat.Builder  notifyBuilder = 
				new NotificationCompat.Builder(this)
		.setContentText("CityGuard Niubility")
		.setContentTitle("CityGuard")
		.setSmallIcon(R.drawable.icon_shleld);
		//不设置 Notify响应的 Activity 应该可以吧？
		TaskStackBuilder  taskBuilder = TaskStackBuilder.create(this);
		
		Intent intent  = new Intent(this,MainActivity.class);
		
		taskBuilder.addParentStack(MainActivity.class);
		taskBuilder.addNextIntent(intent);
		PendingIntent pendingIntent = taskBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		
		notifyBuilder.setContentIntent(pendingIntent);
		NotificationManager nManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		nManager.notify(notifyId, notifyBuilder.build());
		myApp.setNotification(notifyBuilder.build());
		myApp.setNotifyId(notifyId);
		startForeground(notifyId, notifyBuilder.build());
	}

}


















