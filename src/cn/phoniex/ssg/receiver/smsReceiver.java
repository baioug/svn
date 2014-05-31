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
	private static String  wrongstr = "请输入防盗密码 格式为:  #*mima*#加上你们密码";
	private DevicePolicyManager dpManager;//程序已经具备设备管理员权限 可以通过这个清除数据 重设密码 
	@Override
	public void onReceive(Context context, Intent intent) {
		
		dpManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		
		Object[] pdus = (Object[]) intent.getExtras().get("pdus");
		for (Object pdu : pdus) {
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
			String content = sms.getMessageBody();
			Log.i(TAG, "短信内容" + content);
			 sender = sms.getOriginatingAddress();
			if ("#*fangdao*#".equals(content)) {
				//直接调用短信接口发短信
				String  contentStr;
				contentStr = "获取对方经纬度 指令     #*weizhi*#  \n" +
						"锁定设备指令  #*suoding*# \n" +
						"拍摄对方照片指令   #*paizhao*#\n" +
						"清除手机数据指令   #*qingchu*#\n" +
						"手机响铃指令     #*xiangling*#\n" +
						"防盗密码       #*mima*#";
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
				// 终止广播
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
				//注意资源名字不能是纯数字首字母必须是小写字母
				if (!bIsyou) {

					SendSmscontent(wrongstr, sender);
					return;
				}
				MediaPlayer player = MediaPlayer.create(context, R.raw.nucleus);
				player.setVolume(1.0f, 1.0f);
				player.start();
				abortBroadcast();
			}else if("#*paizhao*#".equals(content)){
				//注意资源名字不能是纯数字首字母必须是小写字母
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
//  分割短信 如果短信过长的话
		List<String> divideContents = smsManager.divideMessage(content);  
		for (String text : divideContents) {  
			smsManager.sendTextMessage(sender, null, text,null,null );  
		}
	}

	}

