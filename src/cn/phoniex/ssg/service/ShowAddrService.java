package cn.phoniex.ssg.service;

import java.lang.reflect.Method;
import java.util.Observable;
import java.util.Observer;

import com.android.internal.telephony.ITelephony;

import cn.phoniex.ssg.BlackListActivity;
import cn.phoniex.ssg.R;
import cn.phoniex.ssg.dao.BlackListDAO;
import cn.phoniex.ssg.engine.QueryPhoAddrService;
import cn.phoniex.ssg.util.customToast;
import android.R.integer;
import android.R.string;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.support.v4.app.TaskStackBuilder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.Telephony;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class ShowAddrService extends Service {

	private long ringtm;
	private long offtm;
	private SharedPreferences sp;
	private BlackListDAO dao;
	private final int NOFILTER = 0;
	private final int AUTOFILT = 1;
	private final int BLACKLIST = 2;
	private final int WRITELIST = 3;
	private final int REJECTALL = 4;
	private static final String TAG = "ShowAddrService";
	private CalllogObserver observer;
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		TelephonyManager telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		telManager.listen(new myTelListener(), PhoneStateListener.LISTEN_CALL_STATE);
		dao = new BlackListDAO(this);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
	}

	public class myTelListener extends PhoneStateListener
	{

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			Log.d(TAG, incomingNumber+"");
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				customToast.dismiss();
				offtm = System.currentTimeMillis();
				//Log.d(TAG, "offtime"+String.valueOf(offtm));
				Log.d(TAG, "offset time"+String.valueOf(offtm - ringtm));
				if ((offtm - ringtm)<5*1000) {
					notifyHarass(incomingNumber.toString());
				}
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				String retStr =  new QueryPhoAddrService().showphoaddr(incomingNumber,getApplicationContext());
				Toast.makeText(getApplicationContext(), retStr, 0).show();
				customToast.showToast(getApplicationContext(), retStr);
				ringtm = System.currentTimeMillis();
				Log.d(TAG, "ringtime" + String.valueOf(ringtm));
				FilterNo(incomingNumber);
				//notifyFordescendents 设置为 true 那么只要是我们我们设定的Uri开头的派生Uri的内容变化都会被检测到
				//而如果设置为false 只有精确匹配的才可以 不会监测派生Uri
				/*
				 *  假设我们当前需要观察的Uri为content://com.qin.cb/student，如果发生数据变化的 Uri 为   
				 *  content://com.qin.cb/student/schoolchild ，当notifyForDescendents为 false，那么该ContentObserver会监听不到， 
				 *  但是当notifyForDescendents 为ture，能捕捉该Uri的数据库变化
				 * */
				// 为通话记录注册我们的 内容观察者 以便延时删除电话打入log
				observer = new CalllogObserver(new Handler(), incomingNumber);
				getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, observer);
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				customToast.dismiss();
				break;
				
			default:
				break;
			}
		}

		
	}
	public class CalllogObserver extends ContentObserver {

		String incomingNo;
		public CalllogObserver(Handler handler,String incomingNo) {
			super(handler);
			this.incomingNo = incomingNo;
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			RemoveIncomingCallLog(incomingNo);
			//取消注册观察者
			getContentResolver().unregisterContentObserver(observer);
		}

		
	}
	
	@SuppressLint("NewApi")
	private void notifyHarass(String string) {
		Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG).show();
		NotificationManager notifyManager  = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// 下面的函数已经失效了
		//long when  = System.currentTimeMillis();
		//@SuppressWarnings("deprecation")
		//Notification notify = new Notification(R.drawable.ic_launcher, "发现响一声电话", when);
		//Notification notify = 	new Notification();
		NotificationCompat.Builder mBuilder =
		new NotificationCompat.Builder(this)
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentTitle("SSG Notify You")
		.setContentText("收到响一声骚扰电话" + string);
		// 新建一个任务栈 让启动的Activity 可以正常的返回
		// 导入android.support.v4.app.才不会报错 导入android.app会崩溃
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// 要启动的 Activity
		Intent intent = new Intent(this, BlackListActivity.class);
		// 在 intent 放置 extra 来电号码 
		intent.putExtra("number", string);
		//在任务栈里面添加父任务栈 (这里我们也使用黑名单的Activity)
		stackBuilder.addParentStack(BlackListActivity.class);
		//添加任务栈里面将要启动的 intent
		stackBuilder.addNextIntent(intent);
		// 获取 pendingintent 
		PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(pendingIntent);
		//notifyManager.notify(); 不能这样使用 报错 
		//java.lang.IllegalMonitorStateException:object not locked by thread before notify()
		int mId = 66011;
		notifyManager.notify(mId, mBuilder.build());
		
		
		
	}
	
	public void FilterNo(String incomingNumber) {
		int filter = sp.getInt("filter", 0);
		switch (filter) {
		case NOFILTER:
			// 不过滤任何电话
			break;
		case AUTOFILT:
			//自动根据已知的广告推销等号码的黑名单
			break;
		case BLACKLIST:
			//过滤掉黑名单中的
			if (dao.findNo(incomingNumber)) {
				TerminateCall();
				RemoveIncomingCallLog(incomingNumber);
			}
			break;
		case WRITELIST:
			//只接受白名单中的
			break;
		case REJECTALL:
			//拒接所有
			TerminateCall();
			RemoveIncomingCallLog(incomingNumber);
			break;

		default:
			break;
		}
		
		
	}
	
	// 删除骚扰电话的记录
	private void RemoveIncomingCallLog(String incomingNumber) {
		ContentResolver resolver = getContentResolver();
		Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI, null, 
				"number = ?", new String[]{incomingNumber}, null);
		if (cursor.moveToNext()) {
			String id = cursor.getString(cursor.getColumnIndex("_id"));
			resolver.delete(CallLog.Calls.CONTENT_URI, "_id = ?", new String[]{id});
		}

		
	}
	// 使用反射机制获取Telephony的endcall函数
	private void TerminateCall() {
		try {
			Method method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
			IBinder iBinder = (IBinder) method.invoke(null, new Object[]{TELEPHONY_SERVICE});
			ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
			iTelephony.endCall();
			/*
			TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			//得到TelephonyManager的class
			Class<TelephonyManager> telmangerClass  = TelephonyManager.class;
			Method telmethod = null;
			//获取所有的方法(包括共有 私有 如果只获取共有的就  getMethod)
			telmethod = telmangerClass.getDeclaredMethod("getITelephony", (Class[]) null );
			telmethod.setAccessible(true);//
			//
			ITelephony itelephony = (ITelephony) telmethod.invoke(telManager, (Object[]) null);
			itelephony.endCall();
			*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}


	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}
	
	

}
