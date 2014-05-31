package cn.phoniex.ssg.receiver;


import cn.phoniex.ssg.service.AntiApSpoofingService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;


public class WifiIntentReceiver extends BroadcastReceiver {
	
	private String TAG = "WifiIntentReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		
		int wifistate = -1;
		wifistate = intent.getIntExtra("wifi_state", 0);
        switch (wifistate) {
        case WifiManager.WIFI_STATE_DISABLING:
        	Log.v(TAG , "WIFI_STATE_DISABLING");
            break;
        case WifiManager.WIFI_STATE_DISABLED:
        	Log.v(TAG , "WIFI_STATE_DISABLED");
            break;
        case WifiManager.WIFI_STATE_ENABLING:
        	Log.v(TAG , "WIFI_STATE_ENABLING");
            break;
        case WifiManager.WIFI_STATE_ENABLED:
        	Intent service = new Intent(context, AntiApSpoofingService.class);
        	service.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	context.startService(service);
        	Log.v(TAG , "WIFI_STATE_ENABLED");
            break;
        case WifiManager.WIFI_STATE_UNKNOWN:
        	Log.v(TAG , "WIFI_STATE_UNKNOWN");
            break;
            }
	}
	

}
