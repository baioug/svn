package cn.phoniex.ssg;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class SettingCenterActivity extends Activity {

	private int mAppWidgetId;
	/*
	 * 有重写 onCreate方法
	  我们在widget的配置文件中指定了Activity android:configure="cn.phoniex.ssg.SettingCenterActivity" 
	  这个Activity会在用户添加该Widget的时候自动启动这个Activity，一般这个Activity是用来配置Widget的属性的
	  Widget在启动这个Activity的时候，会在intent里面添加附加信息，我们可以获取这个信息来判定是不是从Widget启动
	  最后我们必须返回一个RESULT_OK的Intent并结束当前Activity，系统才会认为这个widget配置成功，并放置这个widget
	  在Manifest.xml中配置该Activity的 intent-filter的action为 APPWIDGET_CONFIGURE
	 <intent-filter>
        <action android:name="android.appwidget.action.APPWIDGET_CONFIGURE"/>
     </intent-filter>
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_center);

		
	}


	
}
