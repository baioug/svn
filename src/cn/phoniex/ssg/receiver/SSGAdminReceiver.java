package cn.phoniex.ssg.receiver;

import android.annotation.SuppressLint;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class SSGAdminReceiver extends DeviceAdminReceiver {
	
	private  final String TAG = "SSGAdminReceiver";

	@Override
	public DevicePolicyManager getManager(Context context) {
		// TODO Auto-generated method stub
		Log.d(TAG, "getManager");
		return super.getManager(context);
	}

	@Override
	public ComponentName getWho(Context context) {

		Log.d(TAG, "getWho");
		return super.getWho(context);
	}

	@Override
	public void onEnabled(Context context, Intent intent) {

		Log.d(TAG, "onEnabled");
		Toast.makeText(context, "启用设备管理员权限", 0).show();
		super.onEnabled(context, intent);
	}

	@Override
	public CharSequence onDisableRequested(Context context, Intent intent) {
		Log.d(TAG, "onDisableRequested");
		return super.onDisableRequested(context, intent);
	}

	@Override
	public void onDisabled(Context context, Intent intent) {
		Log.d(TAG, "onDisabled");
		Toast.makeText(context, "禁用设备管理员", 0).show();
		super.onDisabled(context, intent);
	}

	@Override
	public void onPasswordChanged(Context context, Intent intent) {
		Log.d(TAG, "onPasswordChanged");
		super.onPasswordChanged(context, intent);
	}

	@Override
	public void onPasswordFailed(Context context, Intent intent) {
		Log.d(TAG, "onPasswordFailed");
		super.onPasswordFailed(context, intent);
	}

	@Override
	public void onPasswordSucceeded(Context context, Intent intent) {
		Log.d(TAG, "onPasswordSucceeded");
		super.onPasswordSucceeded(context, intent);
	}

	@SuppressLint("NewApi")
	@Override
	public void onPasswordExpiring(Context context, Intent intent) {
		Log.d(TAG, "onPasswordExpiring");
		super.onPasswordExpiring(context, intent);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "onReceive");
		super.onReceive(context, intent);
	}

	
}
