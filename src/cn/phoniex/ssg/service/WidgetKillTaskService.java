package cn.phoniex.ssg.service;

import java.util.List;

import cn.phoniex.ssg.R;
import cn.phoniex.ssg.util.GetStrValue;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

public class WidgetKillTaskService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		killAllProcess(this);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	//��Ҫ���������Լ��ķ���
	public  void killAllProcess(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningapps = am.getRunningAppProcesses();
		Toast.makeText(context, "Running task No :"+runningapps.size(), Toast.LENGTH_SHORT).show();
		for (RunningAppProcessInfo info : runningapps) {
			String packname = info.processName;
			if (!info.processName.contains("cn.phoniex.ssg")) {
				am.killBackgroundProcesses(packname);
			}
	
		}
		
		//RemoteView����  ������ȡ��Ӧ�ؼ� �Ա���и��²���
		AppWidgetManager  widgetmanager;
		widgetmanager = AppWidgetManager.getInstance(WidgetKillTaskService.this); 
		RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.ssg_appwidget);
		ComponentName widgetName; //����� 
		widgetName = new ComponentName(getPackageName(), "cn.phoniex.ssg.receiver.SSGWidget");
		remoteViews.setTextColor(R.id.tv_widget_show, Color.BLUE);
		String textStr = "";
		textStr = "������:"+String.valueOf(getProcessCount(WidgetKillTaskService.this))+" �����ڴ�"+ getMemeorySize(WidgetKillTaskService.this);
		remoteViews.setTextViewText(R.id.tv_widget_show, textStr);
		widgetmanager.updateAppWidget(widgetName, remoteViews); 

	}
	
	
	public static int getProcessCount(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningapps = am.getRunningAppProcesses();
		return runningapps.size();

	}
	public static String getMemeorySize(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new ActivityManager.MemoryInfo();
		am.getMemoryInfo(outInfo);
		return GetStrValue.getStrByArg(outInfo.availMem, 0);

	}
	

}
