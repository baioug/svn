package cn.phoniex.ssg.receiver;

import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class BootCompleteReceiver extends BroadcastReceiver {

	
	private static final String TAG = "ssgbootrec";
	private SharedPreferences sp;
	private boolean bbindsim = false;
	private String safeno = null;
	@Override
	public void onReceive(Context context, Intent intent) {
		
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		boolean bon = sp.getBoolean("bindsim", false);
		if (bon) {
			String  imsiStr = sp.getString("ismi", null);
			//spδ����imsi��Ϣ ��һ����
			if (imsiStr == null) {
				
				TelephonyManager tmanager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
				imsiStr = tmanager.getSimSerialNumber();
				if (imsiStr != null) {
					Editor editor = sp.edit();
					editor.putString("imsi", imsiStr);
					editor.commit();
					}
					else {
						Log.i(TAG, "Sim is  unavailable");
					}
				}
			//�Ѿ�����imsiֵ �ж��Ƿ���ͬ
			else if(imsiStr.trim().length()>0)
			{
				TelephonyManager tmanager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
				String currimsi  = tmanager.getSimSerialNumber();
				if (currimsi != null) {
					 if (currimsi.equalsIgnoreCase(imsiStr)) {
						return;
					}
					}
					else {
						Log.i(TAG, "Sim is  changed!");
						SmsManager smsManager = SmsManager.getDefault();
						String content = "����ֻ�sim�Ѿ�����������ȷ���Ƿ�ʧ";
						safeno = sp.getString("safephone", null);
						List<String> divideContents = smsManager.divideMessage(content);  
						for (String text : divideContents) {  
							
							smsManager.sendTextMessage(safeno, null, text,null,null );  
						}
					}
				
			}
			}
			
		}


}
