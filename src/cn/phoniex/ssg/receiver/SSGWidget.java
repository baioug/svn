package cn.phoniex.ssg.receiver;

import java.util.List;

import cn.phoniex.ssg.dao.UserAnalysisDAO;
import cn.phoniex.ssg.service.UpdateWidgetService;
import cn.phoniex.ssg.service.UserAnalysisService;
import cn.phoniex.ssg.service.WidgetGridViewService;
import cn.phoniex.ssg.service.UpdateWidgetService.MyBinder;
import cn.phoniex.ssg.service.WidgetKillTaskService;


import cn.phoniex.ssg.AppManagerActivity;
import cn.phoniex.ssg.R;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

@SuppressLint("CommitPrefEdits")
public class SSGWidget extends AppWidgetProvider {

	private final static String TAG = "SSGWidget";
	//自定义两个action 我们会在Widget update的时候关联对应控件的点击事件 来发送如下的两个对应的action
	public static final String BT_ACTION = "cn.phoniex.ssg.receiver.BT_ACTION";
	public static final String GRIDVIEW_ACTION = "cn.phoniex.ssg.receiver.GRID_VIEW_ACTION";
	public static final String SSGWIDGETEXTRA = "ssgWidgetExtra";
	private MyBinder upBinder;
	private MyConn conn = null;
	private Intent service;
	private Intent useranalyIntent;
	private List<String> pkgnames;
	private UserAnalysisDAO dao;
	private SharedPreferences sp;
	private int appWidgetID;
	private int icurrhour =  -1;//用来判定是否需要更新了 每小时更新一次
	//public class AppWidgetProvider extends BroadcastReceiver  
	// AppWidgetProvider 类是继承自 BroadcasReceiver的 onReceive 就是处理 指定的action消息到达的代码
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "onReceive");
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		String action = intent.getAction();   
		appWidgetID = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 1);
        Log.d(TAG, " onReceive : "+intent.getAction());
        
		Time tm = new Time("GMT+8");
		tm.setToNow();
		if (icurrhour != tm.hour) {
			icurrhour = tm.hour;//重新赋值
	        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
	        ComponentName cmpName = new ComponentName(context,SSGWidget.class);
	        int[] appIds = mgr.getAppWidgetIds(cmpName);
	        mgr.notifyAppWidgetViewDataChanged(appIds,R.id.gv_widget);
	        RemoteViews rViews = new RemoteViews(context.getPackageName(), R.layout.ssg_appwidget);
	        mgr.updateAppWidget(appIds, rViews);
		}
        
        // 根据我们设置不同控件点击事件的不同的ACTION 不同来判定用户点击的操作
        if (action.equals(GRIDVIEW_ACTION)) {
        	// 接受“gridview”的点击事件的广播
        	// 获取到对应的 AppWidget Id(同一个AppWidget是可以被多次添加到Launcher上的根据id来区分)
//            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
//                AppWidgetManager.INVALID_APPWIDGET_ID);
            //我们在GridView关联的Service里面添加IntExtra来区分点击的pos
            int viewIndex = intent.getIntExtra(SSGWIDGETEXTRA, 0);
            dao = new UserAnalysisDAO(context);
            pkgnames = dao.getByFilter(10);
            
            PackageInfo pkginfo;
            if (pkgnames.size() == 0) {
            	useranalyIntent = new Intent(context,UserAnalysisService.class);
        		Editor editor = sp.edit();
        		editor.putBoolean("useranalysis", true);
        		context.startService(useranalyIntent);
        		Toast.makeText(context, "当前APP统计数据库为空，已为您开启统计服务", 0).show();
            	
			}else {
				
				try {
					
					Intent startIntent =  context.getPackageManager().getLaunchIntentForPackage(pkgnames.get(viewIndex));
					
					context.startActivity(startIntent);
			//某些应用 本身不存在可启动的Activity 这样的方式会导致程序崩溃		
//					pkginfo = context.getPackageManager().getPackageInfo(pkgnames.get(viewIndex), PackageManager.GET_ACTIVITIES);
//					if (pkginfo != null) {
//						//某些程序设置了自定义权限 不声明这种权限就会安全异常
//						//java.lang.SecurityException: Permission Denial
//						ActivityInfo actinfo = pkginfo.activities[0];
//						
//						if (actinfo != null) {
//							Intent currIntent = new Intent();
//							intent.setClassName(pkginfo.packageName, actinfo.name);
//							// 在非 Activity 中启动 Activity 需要 新的任务栈标志
//							// Calling startActivity() from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag.
//							currIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//							currIntent.putExtra("WIDGETID", appWidgetID);
//							context.startActivity(currIntent);
//						}else {
//							Toast.makeText(context, "当前程序不存在可启动的Activity", Toast.LENGTH_SHORT ).show();
//						}
//					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
          
            Toast.makeText(context, "Touched view " + viewIndex, Toast.LENGTH_SHORT).show();
        } else if (action.equals(BT_ACTION)) {
        	// 接受清理按钮的点击事件的广播  服务是在 Widget在enable的时候启动服务
            // 开启杀进程服务
            Intent serviceIntent = new Intent(context,WidgetKillTaskService.class);
            context.startService(serviceIntent);
        	//在BroadcastReceiver中不能bind服务
     //      conn = new MyConn();
       //     context.bindService(intent, conn,Context.BIND_AUTO_CREATE);
         //   upBinder.KillAllTASK(context);
          //  context.unbindService(conn);
            Toast.makeText(context, "KillAllTASK", Toast.LENGTH_SHORT).show();
            context.stopService(serviceIntent);
            
        }
        
		super.onReceive(context, intent);
	}
//  重复添加的时候不会在执行 onEnbled 只会执行 onUpdate
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		Log.d(TAG, "onUpdate");
		
	   	for (int appWidgetId:appWidgetIds) {
	    	// 获取AppWidget对应的RemoteViews 以便在后面使用RemoteView的方法控制点击按钮的时候发送对应的ACTION
	        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.ssg_appwidget);
	        
	        // 设置响应 “按钮(clear)” 的intent
	        Intent btIntent = new Intent().setAction(BT_ACTION);
	        PendingIntent btPendingIntent = PendingIntent.getBroadcast(context, 0, btIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	        rv.setOnClickPendingIntent(R.id.bt_widget_clear, btPendingIntent);	        
	        
	        // 设置 “GridView(gridview)” 的adapter。
	        // (01) intent: 对应启动 GridWidgetService(RemoteViewsService) 的intent  
	        // (02) setRemoteAdapter: 设置 gridview的适配器
	        //    通过setRemoteAdapter将gridview和GridWidgetService关联起来，
	        //    以达到通过 GridWidgetService 更新 gridview 的目的
	        
	        
	        // 设置响应 “GridView(gridview)” 的intent模板	        
	        // 说明：“集合控件(如GridView、ListView、StackView等)”中包含很多子元素，如GridView包含很多格子。
	        //     它们不能像普通的按钮一样通过 setOnClickPendingIntent 设置点击事件，必须先通过两步。
	        //        (01) 通过 setPendingIntentTemplate 设置 “intent模板”，这是比不可少的！
	        //        (02) 然后在处理该“集合控件”的RemoteViewsFactory类的getViewAt()接口中 通过 setOnClickFillInIntent 设置“集合控件的某一项的数据”
	        Intent serviceIntent = new Intent(context, WidgetGridViewService.class);        
	        rv.setRemoteAdapter(R.id.gv_widget, serviceIntent);
	        //这个 intent 是我们在onReceive里面接受到的intent
	        Intent gridIntent = new Intent();
	        gridIntent.setAction(GRIDVIEW_ACTION);
	        gridIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
	        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, gridIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	        // 设置intent模板
	        rv.setPendingIntentTemplate(R.id.gv_widget, pendingIntent);
	        // 调用集合管理器对集合进行更新
	        appWidgetManager.updateAppWidget(appWidgetId, rv);
    	}
		
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	// 没删除一个Widget 就会执行一次删除
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.d(TAG, "onDeleted");
		super.onDeleted(context, appWidgetIds);
	}
	
	// onEnabed widget第一次创建的时候 执行的方法 
	@Override
	public void onEnabled(Context context) {
		// 初始化widget数据的操作,开启以后后台 
		Log.d(TAG, "onEnabled");
		service = new Intent(context,UpdateWidgetService.class);
		context.startService(service);
		super.onEnabled(context);
	}

	@Override
	public void onDisabled(Context context) {
		// 当所有的widget都被删除的时候 执行 ondisable()；
		// 停止我们开启的服务
		// 删除垃圾文件 临时文件
		Log.d(TAG, "onDisabled");
		if (service != null) {
			context.stopService(service);
		}
		super.onDisabled(context);
	}

	
	private class MyConn implements ServiceConnection{

		public void onServiceConnected(ComponentName name, IBinder service) {
			upBinder = (MyBinder)service;
		}

		public void onServiceDisconnected(ComponentName name) {
			
		}
		
	}
	
	
}
