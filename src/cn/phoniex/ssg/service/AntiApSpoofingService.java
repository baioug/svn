package cn.phoniex.ssg.service;

import cn.phoniex.ssg.AntiApSpoofingActivity;
import cn.phoniex.ssg.MainActivity;
import cn.phoniex.ssg.R;
import cn.phoniex.ssg.dao.WiFiDetailDAO;
import cn.phoniex.ssg.domain.WiFiDetail;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

public class AntiApSpoofingService extends Service {

	private WiFiDetailDAO dao;
	private String TAG = "AntiApSpoofingService";
	private Context context;
	private Handler handler  =  new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Toast.makeText(AntiApSpoofingService.this, "本次连接的无线MAC地址和保存的不一致，\r\n请注意确认该无线是否为钓鱼AP", Toast.LENGTH_LONG).show();
			notifyUser();
			stopSelf();
			
		}
		
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.context = AntiApSpoofingService.this;
		//Toast.makeText(AntiApSpoofingService.this, "Service Toast Show!", Toast.LENGTH_LONG).show();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e(TAG , "onStartCommand");
	//	Toast.makeText(AntiApSpoofingService.this, "Service Toast Show!", Toast.LENGTH_LONG).show();
		
		new Thread(){

			@Override
			public void run() {
		    	dao = new WiFiDetailDAO(context);
		    	try {
					Thread.sleep(6*1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	WifiInfo info = ((WifiManager)getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
		    	if ((info != null)&&(info.getSSID().toString().length()>0)&&(info.getBSSID() != null)) {
		        	WiFiDetail detail = new WiFiDetail(info.getSSID(), info.getBSSID(), "", info.getLinkSpeed(), info.getRssi());
		    		if (dao.IsSpoofing(detail)) {
		    			handler.sendEmptyMessage(1);
		    			Log.e(TAG , "ap spoofing");
					}
		        	dao.add(detail);
				}
				super.run();
			}
			
		}.start();
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	public void notifyUser(){
		Notification notification;
		NotificationCompat.Builder  notifyBuilder = 
				new NotificationCompat.Builder(AntiApSpoofingService.this)
		.setContentText("如果您无法确定该热点是否可信，请不要在该网络下泄露敏感信息")
		.setContentTitle("该无线MAC已改变,请确定该热点非钓鱼AP!")
		.setSmallIcon(R.drawable.ap_spoofing);
		TaskStackBuilder  taskBuilder = TaskStackBuilder.create(AntiApSpoofingService.this);
		
		Intent intent  = new Intent(AntiApSpoofingService.this,AntiApSpoofingActivity.class);
		intent.putExtra("fromnotify", true);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		taskBuilder.addParentStack(AntiApSpoofingActivity.class);
		taskBuilder.addNextIntent(intent);
		PendingIntent pendingIntent = taskBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		
		notifyBuilder.setContentIntent(pendingIntent);
		notification = notifyBuilder.build();
//		notification.defaults |= Notification.DEFAULT_SOUND;//声音
//		notification.defaults |= Notification.DEFAULT_LIGHTS;//闪光
//		notification.defaults |= Notification.DEFAULT_VIBRATE;//震动
//		notification.defaults |= Notification.DEFAULT_ALL;
		NotificationManager nManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		
		nManager.notify(601, notification);
		
	}
	
}
