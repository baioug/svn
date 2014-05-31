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
				//notifyFordescendents ����Ϊ true ��ôֻҪ�����������趨��Uri��ͷ������Uri�����ݱ仯���ᱻ��⵽
				//���������Ϊfalse ֻ�о�ȷƥ��Ĳſ��� ����������Uri
				/*
				 *  �������ǵ�ǰ��Ҫ�۲��UriΪcontent://com.qin.cb/student������������ݱ仯�� Uri Ϊ   
				 *  content://com.qin.cb/student/schoolchild ����notifyForDescendentsΪ false����ô��ContentObserver����������� 
				 *  ���ǵ�notifyForDescendents Ϊture���ܲ�׽��Uri�����ݿ�仯
				 * */
				// Ϊͨ����¼ע�����ǵ� ���ݹ۲��� �Ա���ʱɾ���绰����log
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
			//ȡ��ע��۲���
			getContentResolver().unregisterContentObserver(observer);
		}

		
	}
	
	@SuppressLint("NewApi")
	private void notifyHarass(String string) {
		Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG).show();
		NotificationManager notifyManager  = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// ����ĺ����Ѿ�ʧЧ��
		//long when  = System.currentTimeMillis();
		//@SuppressWarnings("deprecation")
		//Notification notify = new Notification(R.drawable.ic_launcher, "������һ���绰", when);
		//Notification notify = 	new Notification();
		NotificationCompat.Builder mBuilder =
		new NotificationCompat.Builder(this)
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentTitle("SSG Notify You")
		.setContentText("�յ���һ��ɧ�ŵ绰" + string);
		// �½�һ������ջ ��������Activity ���������ķ���
		// ����android.support.v4.app.�Ų��ᱨ�� ����android.app�����
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Ҫ������ Activity
		Intent intent = new Intent(this, BlackListActivity.class);
		// �� intent ���� extra ������� 
		intent.putExtra("number", string);
		//������ջ������Ӹ�����ջ (��������Ҳʹ�ú�������Activity)
		stackBuilder.addParentStack(BlackListActivity.class);
		//�������ջ���潫Ҫ������ intent
		stackBuilder.addNextIntent(intent);
		// ��ȡ pendingintent 
		PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(pendingIntent);
		//notifyManager.notify(); ��������ʹ�� ���� 
		//java.lang.IllegalMonitorStateException:object not locked by thread before notify()
		int mId = 66011;
		notifyManager.notify(mId, mBuilder.build());
		
		
		
	}
	
	public void FilterNo(String incomingNumber) {
		int filter = sp.getInt("filter", 0);
		switch (filter) {
		case NOFILTER:
			// �������κε绰
			break;
		case AUTOFILT:
			//�Զ�������֪�Ĺ�������Ⱥ���ĺ�����
			break;
		case BLACKLIST:
			//���˵��������е�
			if (dao.findNo(incomingNumber)) {
				TerminateCall();
				RemoveIncomingCallLog(incomingNumber);
			}
			break;
		case WRITELIST:
			//ֻ���ܰ������е�
			break;
		case REJECTALL:
			//�ܽ�����
			TerminateCall();
			RemoveIncomingCallLog(incomingNumber);
			break;

		default:
			break;
		}
		
		
	}
	
	// ɾ��ɧ�ŵ绰�ļ�¼
	private void RemoveIncomingCallLog(String incomingNumber) {
		ContentResolver resolver = getContentResolver();
		Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI, null, 
				"number = ?", new String[]{incomingNumber}, null);
		if (cursor.moveToNext()) {
			String id = cursor.getString(cursor.getColumnIndex("_id"));
			resolver.delete(CallLog.Calls.CONTENT_URI, "_id = ?", new String[]{id});
		}

		
	}
	// ʹ�÷�����ƻ�ȡTelephony��endcall����
	private void TerminateCall() {
		try {
			Method method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
			IBinder iBinder = (IBinder) method.invoke(null, new Object[]{TELEPHONY_SERVICE});
			ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
			iTelephony.endCall();
			/*
			TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			//�õ�TelephonyManager��class
			Class<TelephonyManager> telmangerClass  = TelephonyManager.class;
			Method telmethod = null;
			//��ȡ���еķ���(�������� ˽�� ���ֻ��ȡ���еľ�  getMethod)
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
