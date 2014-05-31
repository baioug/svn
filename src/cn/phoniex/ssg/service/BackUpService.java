package cn.phoniex.ssg.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import cn.phoniex.ssg.BlackListActivity;
import cn.phoniex.ssg.R;
import cn.phoniex.ssg.SeniorToolsActivity;
import cn.phoniex.ssg.domain.smsBackupInfo;
import cn.phoniex.ssg.engine.smsInfoProvider;

import android.R.integer;
import android.R.xml;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Xml;
import android.widget.Toast;

public class BackUpService extends Service {

	
	private BackUpBinder binder;
	private int progress = 0;
	private boolean bcancel = false;
	private boolean bIsdone = false;
	private final int MSG_PROGRESS = 0;
	private final int MSG_CANCEL = 1;
	private final int NOTIFY_ID = 101;
	private NotificationManager manager;
	private NotificationCompat.Builder builder;
	private smsInfoProvider smsprovider;
	
	// ����Ŀ��
	private int isize = 0;
	
	private Handler handler =  new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == MSG_PROGRESS) {
				
				builder.setProgress(isize, msg.arg1, false);
				manager.notify(NOTIFY_ID, builder.build());
				
			}else if (msg.what == MSG_CANCEL) {
				//�����;��������ֹ�ˣ�Notify�û�
				//builder.setStyle(style);
				manager.cancel(NOTIFY_ID);
			}
		}
		
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		//����� bindservice�����ķ���unbind֮������ onDestroy
		return binder;
		//�������startservice Ȼ���� bindservice unbind֮����񲻻��˳�
	}


	 @Override
	public void onCreate() {
		super.onCreate();
		binder = new BackUpBinder();
		 smsprovider = new smsInfoProvider(this);
		BackupSms();
		
		
	}


	public void BackupSms() {
		showProgressNotify("���ڱ���...");
		new Thread()
		{
			@Override
			public void run() {
				
				List<smsBackupInfo> infos  = smsprovider.getAllSmsInfos();
				XmlSerializer xmlSerializer   =  Xml.newSerializer();
				File file = new File("sms_backup.xml");
				 isize = infos.size();
				 int icut = 0;

				try {
					FileOutputStream os = new FileOutputStream(file);
					xmlSerializer.setOutput(os, "utf-8");
					xmlSerializer.startDocument("utf-8", true);
					xmlSerializer.startTag(null, "allsmss");
					xmlSerializer.startTag(null, "count");
					xmlSerializer.text(String.valueOf(isize));
					xmlSerializer.endTag(null, "count");
					
					for (smsBackupInfo info : infos) {
						xmlSerializer.startTag(null, "sms");
						
						xmlSerializer.startTag(null, "id");
						xmlSerializer.text(info.get_id());
						xmlSerializer.endTag(null, "id");
						
						xmlSerializer.startTag(null, "thread_id");
						xmlSerializer.text(info.getThread_id());
						xmlSerializer.endTag(null, "thread_id");
						
						
						xmlSerializer.startTag(null, "address");
						xmlSerializer.text(info.getAddress());
						xmlSerializer.endTag(null, "address");
						
						xmlSerializer.startTag(null, "date");
						xmlSerializer.text(info.getDate());
						xmlSerializer.endTag(null, "date");
						
						xmlSerializer.startTag(null, "date_send");
						xmlSerializer.text(info.getDate_send());
						xmlSerializer.endTag(null, "date_send");
						
						xmlSerializer.startTag(null, "read");
						xmlSerializer.text(info.getRead());
						xmlSerializer.endTag(null, "read");
						
						xmlSerializer.startTag(null, "status");
						xmlSerializer.text(info.getStatus());
						xmlSerializer.endTag(null, "status");
						
						xmlSerializer.startTag(null, "type");
						xmlSerializer.text(info.getType());
						xmlSerializer.endTag(null, "type");
						
						xmlSerializer.startTag(null, "body");
						xmlSerializer.text(info.getBody());
						xmlSerializer.endTag(null, "body");
						
						xmlSerializer.endTag(null, "sms");
						icut ++ ;
						Message msg = handler.obtainMessage();
						msg.what = MSG_PROGRESS;
						msg.arg1 = icut;
						progress = icut;
						handler.sendMessage(msg);
					}
					
					xmlSerializer.endTag(null, "allsmss");
					xmlSerializer.endDocument();
					// ALT + Shift  ������������Ӣ�� 
					xmlSerializer.flush();
					os.flush();
					os.close();
					bIsdone =true;
					Looper.prepare();
					Toast.makeText(getApplicationContext(), "���ű������", 0).show();
					Looper.loop();
				} catch (Exception e) {
					bIsdone = false;
					Looper.prepare();
					Toast.makeText(getApplicationContext(), "���ű��ݳ����쳣!", 0).show();
					Looper.loop();
					e.printStackTrace();
				}

				super.run();
			}
			
		}.start();
	}

	public  void RestoreSms()
	{
		showProgressNotify("���ڻ�ԭ����");
		new Thread(){

			@Override
			public void run() {

				try {
					List<smsBackupInfo> infos  = new ArrayList<smsBackupInfo>();
					XmlPullParser xmlparser =  Xml.newPullParser();
					File file = new File("sms_backup.xml");
					FileInputStream fis = new FileInputStream(file);
					xmlparser.setInput(fis, "utf-8");
					int type = xmlparser.getEventType();
					smsBackupInfo info = null;
					ContentValues values = null;
					while (type != XmlPullParser.END_DOCUMENT) {
						
						switch (type) {
						case XmlPullParser.START_TAG:
							if ("count".equals(xmlparser.getName())) {
								String cut = xmlparser.nextText();
								 isize = Integer.parseInt(cut);
							}
							if ("sms".equals(xmlparser.getName())) {
								//һ����¼�Ŀ�ʼ new һ�� sms����
								info = new smsBackupInfo();
								values = new ContentValues();
							}
							else if ("address".equals(xmlparser.getName())) {
								info.setAddress(xmlparser.nextText());
								values.put("address", xmlparser.nextText());
							}
							else if ("body".equals(xmlparser.getName())) {
								info.setBody(xmlparser.nextText());
								values.put("body", xmlparser.nextText());
							}
							else if ("date".equals(xmlparser.getName())) {
								info.setDate(xmlparser.nextText());
								values.put("date", xmlparser.nextText());
							}
							else if ("id".equals(xmlparser.getName())) {
								info.set_id(xmlparser.nextText());
								values.put("_id", xmlparser.nextText());
							}
							else if ("thread_id".equals(xmlparser.getName())) {
								info.setThread_id(xmlparser.nextText());
								values.put("thread_id", xmlparser.nextText());
							}
							else if ("read".equals(xmlparser.getName())) {
								info.setRead(xmlparser.nextText());
								values.put("read", xmlparser.nextText());
								
							}
							else if ("status".equals(xmlparser.getName())) {
								info.setStatus(xmlparser.nextText());
								values.put("status", xmlparser.nextText());
							}
							else if ("type".equals(xmlparser.getName())) {
								info.setType(xmlparser.nextText());
								values.put("type", xmlparser.nextText());
							}
							else if ("date_send".equals(xmlparser.getName())) {
								info.setDate_send(xmlparser.nextText());
								values.put("date_send", xmlparser.nextText());
							}
							break;
						case XmlPullParser.END_TAG:
							if ("sms".equals(xmlparser.getName())) {
								//infos.add(info);
								//info = null;//�����ظ�ʹ��
								ContentResolver resolver = getContentResolver();
								resolver.insert(Uri.parse("content://sms/"), values);
								values = null;
								progress ++;
								Message msg = handler.obtainMessage();
								msg.what = MSG_PROGRESS;
								msg.arg1 = progress;
								handler.sendMessage(msg);
							}
							break;

						default:
							break;
						}
						
						type = xmlparser.next();
					}
				} catch (Exception e) {

				}
				super.run();
			}
			
			
		}.start();
		
	}


	@Override
	public void onDestroy() {

		bcancel = true;
		Message msg = handler.obtainMessage();
		msg.what = MSG_CANCEL;
		handler.sendMessage(msg);
		super.onDestroy();
	}



	@Override
	public boolean onUnbind(Intent intent) {
		
		return super.onUnbind(intent);
	}



	public class BackUpBinder extends Binder
	 {
		public int getprogress()
		{
			return progress;
		}
		
		public BackUpService getService()
		{
			//��Activity������ binder���� Ȼ����øú��� ��ȡ����Ķ���
			//���÷�����������÷����ж�Ӧ�Ĺ�������
			return BackUpService.this;
		}
		 
	 }
	
	public void showProgressNotify(String title)
	{
		manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		 builder = new NotificationCompat.Builder(this)
		.setSmallIcon(R.drawable.backup)
		.setContentTitle(title)
		.setProgress(isize, progress, false);;//���ý�������false��ʾ�ǽ�������true��ʾ�Ǹ������
		
		TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
		Intent intent = new Intent(this,SeniorToolsActivity.class);
		taskStackBuilder.addParentStack(SeniorToolsActivity.class);
		taskStackBuilder.addNextIntent(intent);
		
		intent.putExtra("FromNotify", true);
		
		PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(pendingIntent);
		
		manager.notify(NOTIFY_ID, builder.build());
		
		
	}
}
