package cn.phoniex.ssg.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.phoniex.ssg.R;
import cn.phoniex.ssg.dao.UserAnalysisDAO;
import cn.phoniex.ssg.receiver.SSGWidget;
import cn.phoniex.ssg.util.GetStrValue;

import android.R.integer;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.IBinder;
import android.widget.RemoteViews;

public class UpdateWidgetService extends Service {

	private MyBinder mBinder;
	private Timer timer;
	private TimerTask task;
	private Timer timerL;
	private TimerTask taskL;
	private List<String> pkgnames;
	private List<Drawable> icons;
	private UserAnalysisDAO dao;
	private int  appWidgetId;
	
	private AppWidgetManager  widgetmanager;
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		
		dao = new UserAnalysisDAO(this);
		pkgnames = dao.getByFilter(10);
		initUserAnalysisList(pkgnames);
		widgetmanager = AppWidgetManager.getInstance(UpdateWidgetService.this); 
		timer = new Timer();
		timerL = new Timer();
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		//appWidgetId = intent.getIntExtra("WIDGETID", 1);
		task = new TimerTask() {
			
			@Override
			public void run() {
				
				//RemoteView对象  用来获取对应控件 以便进行更新操作
				RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.ssg_appwidget);
				ComponentName widgetName; //组件名 
				widgetName = new ComponentName(getPackageName(), "cn.phoniex.ssg.receiver.SSGWidget");
				remoteViews.setTextColor(R.id.tv_widget_show, Color.BLUE);
				String textStr = "";
				textStr = "进程数:"+String.valueOf(getProcessCount(UpdateWidgetService.this))+" 可用内存"+ getMemeorySize(UpdateWidgetService.this);
				remoteViews.setTextViewText(R.id.tv_widget_show, textStr);
				widgetmanager.updateAppWidget(widgetName, remoteViews); 
				
			}
		};
		taskL = new TimerTask() {
			
			@Override
			public void run() {
				RemoteViews rViews = new RemoteViews(getPackageName(), R.layout.ssg_appwidget);
				ComponentName widgetName; //组件名 
				widgetName = new ComponentName(getPackageName(), "cn.phoniex.ssg.receiver.SSGWidget");
				// 尝试更新 Widget的GridView  根据当前的时间段来更新
				Intent udateIntent = new Intent(UpdateWidgetService.this,WidgetGridViewService.class);
				rViews.setRemoteAdapter(R.id.gv_widget, udateIntent);
				
				
		        AppWidgetManager mgr = AppWidgetManager.getInstance(UpdateWidgetService.this);
		        ComponentName cmpName = new ComponentName(UpdateWidgetService.this,SSGWidget.class);
		        int[] appIds = mgr.getAppWidgetIds(cmpName);
				widgetmanager.notifyAppWidgetViewDataChanged(appIds, R.id.gv_widget);
				widgetmanager.updateAppWidget(widgetName, rViews); 
				
		        mgr.notifyAppWidgetViewDataChanged(appIds,R.id.gv_widget);
		        mgr.updateAppWidget(appIds, rViews);
			}
		};
		
		//刷新时间为
		timer.schedule(task, 20*100, 60*100);
		timerL.schedule(taskL, 10*100, 601*10);
		
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}
	
	// 基础Binder 用来导出给其他类bind到服务来调用相关函数
	public class MyBinder extends Binder  {

		public void KillAllTASK(Context context) {

			killAllProcess(context);
		}


	}
	
	
	public void initUserAnalysisList(List<String> pkgnamelist){
		icons = new ArrayList<Drawable>();
		PackageManager pm = getPackageManager();
		for (String name : pkgnamelist) {
			try {
				PackageInfo  info = pm.getPackageInfo(name, 0);
				Drawable icon = info.applicationInfo.loadIcon(pm);
				icons.add(icon);
				icon = null;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void killAllProcess(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningapps = am.getRunningAppProcesses();
		for (RunningAppProcessInfo info : runningapps) {
			String packname = info.processName;
			if (!packname.contains("cn.phoniex.ssg")) {
				am.killBackgroundProcesses(packname);
			}
		}

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
