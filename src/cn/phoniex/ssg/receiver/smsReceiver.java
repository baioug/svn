package cn.phoniex.ssg.receiver;

import java.util.List;

import cn.phoniex.ssg.engine.GPSinfoProvider;
import cn.phoniex.ssg.engine.TakePhotoService;
import cn.phoniex.ssg.util.MD5Encoder;
import cn.phoniex.ssg.R;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.ViewDebug.FlagToString;

public class smsReceiver extends BroadcastReceiver {

	
	private static final String TAG = "SMSReceiver";
	private SharedPreferences sp;
	private  boolean bIsyou = false;
	private String sender = null;
	private static String  wrongstr = "������������� ��ʽΪ:  #*mima*#������������";
	private DevicePolicyManager dpManager;//�����Ѿ��߱��豸����ԱȨ�� ����ͨ������������ �������� 
	@Override
	public void onReceive(Context context, Intent intent) {
		
		dpManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		
		Object[] pdus = (Object[]) intent.getExtras().get("pdus");
		for (Object pdu : pdus) {
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
			String content = sms.getMessageBody();
			Log.i(TAG, "��������" + content);
			 sender = sms.getOriginatingAddress();
			if ("#*fangdao*#".equals(content)) {
				//ֱ�ӵ��ö��Žӿڷ�����
				String  contentStr;
				contentStr = "��ȡ�Է���γ�� ָ��     #*weizhi*#  \n" +
						"�����豸ָ��  #*suoding*# \n" +
						"����Է���Ƭָ��   #*paizhao*#\n" +
						"����ֻ�����ָ��   #*qingchu*#\n" +
						"�ֻ�����ָ��     #*xiangling*#\n" +
						"��������       #*mima*#";
				SendSmscontent(contentStr, sender);
			}
			if (content.contains("#*mima*#")) {
				
			String mm = content.substring(8);
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
			String realmm = sp.getString("realpass", null);
			if (realmm.equalsIgnoreCase(MD5Encoder.encode(mm))) {
				bIsyou = true;
				}
			}
			if ("#*weizhi*#".equals(content)) {
				// ��ֹ�㲥
				if (!bIsyou) {

					SendSmscontent(wrongstr, sender);
					return;
				}
				abortBroadcast();
				GPSinfoProvider provider = GPSinfoProvider.getInstance(context);
				String location = provider.getLoction();
				SmsManager smsmanager = SmsManager.getDefault();
				if ("".equals(location)) {

				} else {
					smsmanager.sendTextMessage(sender, null, location, null,
							null);
				}
			}else if("#*locknow*#".equals(content)){
				if (!bIsyou) {

					SendSmscontent(wrongstr, sender);
					return;
				}
				DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
				manager.resetPassword("123", 0);
				manager.lockNow();
				abortBroadcast();
			}else if("#*wipedata*#".equals(content)){
				if (!bIsyou) {

					SendSmscontent(wrongstr, sender);
					return;
				}
				DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
				manager.wipeData(0);
				abortBroadcast();
			}else if("#*alarm*#".equals(content)){
				//ע����Դ���ֲ����Ǵ���������ĸ������Сд��ĸ
				if (!bIsyou) {

					SendSmscontent(wrongstr, sender);
					return;
				}
				MediaPlayer player = MediaPlayer.create(context, R.raw.nucleus);
				player.setVolume(1.0f, 1.0f);
				player.start();
				abortBroadcast();
			}else if("#*paizhao*#".equals(content)){
				//ע����Դ���ֲ����Ǵ���������ĸ������Сд��ĸ
				if (!bIsyou) {

					SendSmscontent(wrongstr, sender);
					return;
				}
				TakePhotoService.takePhoto(context);
				abortBroadcast();
			}
		}

	}
	private void SendSmscontent(String content, String sender) {
		SmsManager smsManager = SmsManager.getDefault();
//  �ָ���� ������Ź����Ļ�
		List<String> divideContents = smsManager.divideMessage(content);  
		for (String text : divideContents) {  
			smsManager.sendTextMessage(sender, null, text,null,null );  
		}
	}

	}

