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
	 * ����д onCreate����
	  ������widget�������ļ���ָ����Activity android:configure="cn.phoniex.ssg.SettingCenterActivity" 
	  ���Activity�����û���Ӹ�Widget��ʱ���Զ��������Activity��һ�����Activity����������Widget�����Ե�
	  Widget���������Activity��ʱ�򣬻���intent������Ӹ�����Ϣ�����ǿ��Ի�ȡ�����Ϣ���ж��ǲ��Ǵ�Widget����
	  ������Ǳ��뷵��һ��RESULT_OK��Intent��������ǰActivity��ϵͳ�Ż���Ϊ���widget���óɹ������������widget
	  ��Manifest.xml�����ø�Activity�� intent-filter��actionΪ APPWIDGET_CONFIGURE
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
