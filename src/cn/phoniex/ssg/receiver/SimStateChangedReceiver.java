package cn.phoniex.ssg.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SimStateChangedReceiver extends BroadcastReceiver {

	private final static String ACTION_S_S_C = "android.intent.action.SIM_STATE_CHANGED"; 
	private final static int SIM_VALID = 0; 
	private final static int SIM_INVALID = 1;  
	 private int simState = SIM_INVALID;  
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		if (intent.getAction().equals(ACTION_S_S_C)) {
			Log.i("SimState", "Sim State Changed");
		}
		TelephonyManager telmanager  = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
		int simstate = telmanager.getSimState();
		
		switch (simstate) {
		case TelephonyManager.SIM_STATE_READY:
			simstate = SIM_VALID;
			break;
		case TelephonyManager.SIM_STATE_ABSENT://δ�忨
			simstate = SIM_INVALID;
			break;
		case TelephonyManager.SIM_STATE_NETWORK_LOCKED://����
			simstate = SIM_INVALID;
			break;
		case TelephonyManager.SIM_STATE_PIN_REQUIRED://��Ҫ����pin
			simstate = SIM_INVALID;
			break;
		case TelephonyManager.SIM_STATE_PUK_REQUIRED://��Ҫpuk
			simstate = SIM_INVALID;
			break;
		case TelephonyManager.SIM_STATE_UNKNOWN://δ֪״̬
			simstate = SIM_VALID;
			break;
		default:
			break;
		}
		
	}
	public  int getSimstate()
	{
		return simState;
	}
	
	

}
