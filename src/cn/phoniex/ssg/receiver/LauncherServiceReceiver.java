package cn.phoniex.ssg.receiver;

import cn.phoniex.ssg.service.KingsGuardService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class LauncherServiceReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent service = new Intent(context,KingsGuardService.class);
		context.startService(service);
	}

}
