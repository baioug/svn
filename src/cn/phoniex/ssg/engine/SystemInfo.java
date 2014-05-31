package cn.phoniex.ssg.engine;

import android.app.Service;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class SystemInfo {

	public static DisplayMetrics  getdisplay(Context context)
	{
		DisplayMetrics metrics = null;
		WindowManager mWm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		mWm.getDefaultDisplay().getMetrics(metrics);
		return metrics;
	}
	public static void gettelstats(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
		int phostate = tm.getCallState();//tm.CALL_STATE_IDLE=0 tm.CALL_STATE_RINGING=1  ÏìÁå  tm.CALL_STATE_OFFHOOK=2  Õª»ú
				
	}

}
