package cn.phoniex.ssg.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.phoniex.ssg.MainActivity;
import cn.phoniex.ssg.MyApplication;
import cn.phoniex.ssg.R;
import cn.phoniex.ssg.dao.UserAnalysisDAO;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.format.Time;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class UserAnalysisService extends Service {

	private static final String TAG = "UserAnalysisService";
	private boolean bStart;
	private ActivityManager am;
	private KeyguardManager km;
	private int notifyId = 66011;
	private Notification notification;
	private UserAnalysisDAO dao;
	private SharedPreferences sp;
	private MyApplication myApp;
	
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		bStart = true;
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean("useranalysis", true);
		editor.commit();
		dao = new UserAnalysisDAO(this);
		Log.d(TAG, "onCreate");
		// 从 Application中获取全局保存的Notification Id
		myApp = (MyApplication) getApplicationContext();
		if (myApp.getNotifyId() != 0) {
			notifyId = myApp.getNotifyId();
			notification = myApp.getNotification();
			startForeground(notifyId, notification);
		}else {
			
			SetServiceForeground();
		}
		am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		km  = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		new Thread(){

			@Override
			public void run() {
				
				while (bStart) {
					try {
						if (km.inKeyguardRestrictedInputMode()) {
							//如果是锁屏休息1秒
							sleep(100*1000);
							continue;
						}
						List<RunningTaskInfo> taskInfos  = am.getRunningTasks(1);
						//analysisBehaver(taskInfos);
						RunningTaskInfo currentInfo = taskInfos.get(0);
						Log.e(TAG, currentInfo.topActivity.toString());
						String currpkgname = currentInfo.topActivity.getPackageName().toString();
						UpateUserAnalysis(currpkgname);
						Thread.sleep(601*10);
						super.run();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
		}.start();
		
	}

	public void UpateUserAnalysis(String pkgname){
		
		Time tm = new Time("GMT+8");
		tm.setToNow();
		//String dateStr = tm.year
		int ihour = (tm.hour+8)%24;//貌似之前设置的东八区没起作用还是晚八个小时
		SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(new Date());
		dao.add(pkgname, date, ihour);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Editor editor = sp.edit();
		editor.putBoolean("useranalysis", false);
		editor.commit();
		super.onDestroy();

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
