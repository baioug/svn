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
	//�Զ�������action ���ǻ���Widget update��ʱ�������Ӧ�ؼ��ĵ���¼� ���������µ�������Ӧ��action
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
	private int icurrhour =  -1;//�����ж��Ƿ���Ҫ������ ÿСʱ����һ��
	//public class AppWidgetProvider extends BroadcastReceiver  
	// AppWidgetProvider ���Ǽ̳��� BroadcasReceiver�� onReceive ���Ǵ��� ָ����action��Ϣ����Ĵ���
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
			icurrhour = tm.hour;//���¸�ֵ
	        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
	        ComponentName cmpName = new ComponentName(context,SSGWidget.class);
	        int[] appIds = mgr.getAppWidgetIds(cmpName);
	        mgr.notifyAppWidgetViewDataChanged(appIds,R.id.gv_widget);
	        RemoteViews rViews = new RemoteViews(context.getPackageName(), R.layout.ssg_appwidget);
	        mgr.updateAppWidget(appIds, rViews);
		}
        
        // �����������ò�ͬ�ؼ�����¼��Ĳ�ͬ��ACTION ��ͬ���ж��û�����Ĳ���
        if (action.equals(GRIDVIEW_ACTION)) {
        	// ���ܡ�gridview���ĵ���¼��Ĺ㲥
        	// ��ȡ����Ӧ�� AppWidget Id(ͬһ��AppWidget�ǿ��Ա������ӵ�Launcher�ϵĸ���id������)
//            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
//                AppWidgetManager.INVALID_APPWIDGET_ID);
            //������GridView������Service�������IntExtra�����ֵ����pos
            int viewIndex = intent.getIntExtra(SSGWIDGETEXTRA, 0);
            dao = new UserAnalysisDAO(context);
            pkgnames = dao.getByFilter(10);
            
            PackageInfo pkginfo;
            if (pkgnames.size() == 0) {
            	useranalyIntent = new Intent(context,UserAnalysisService.class);
        		Editor editor = sp.edit();
        		editor.putBoolean("useranalysis", true);
        		context.startService(useranalyIntent);
        		Toast.makeText(context, "��ǰAPPͳ�����ݿ�Ϊ�գ���Ϊ������ͳ�Ʒ���", 0).show();
            	
			}else {
				
				try {
					
					Intent startIntent =  context.getPackageManager().getLaunchIntentForPackage(pkgnames.get(viewIndex));
					
					context.startActivity(startIntent);
			//ĳЩӦ�� �������ڿ�������Activity �����ķ�ʽ�ᵼ�³������		
//					pkginfo = context.getPackageManager().getPackageInfo(pkgnames.get(viewIndex), PackageManager.GET_ACTIVITIES);
//					if (pkginfo != null) {
//						//ĳЩ�����������Զ���Ȩ�� ����������Ȩ�޾ͻᰲȫ�쳣
//						//java.lang.SecurityException: Permission Denial
//						ActivityInfo actinfo = pkginfo.activities[0];
//						
//						if (actinfo != null) {
//							Intent currIntent = new Intent();
//							intent.setClassName(pkginfo.packageName, actinfo.name);
//							// �ڷ� Activity ������ Activity ��Ҫ �µ�����ջ��־
//							// Calling startActivity() from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag.
//							currIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//							currIntent.putExtra("WIDGETID", appWidgetID);
//							context.startActivity(currIntent);
//						}else {
//							Toast.makeText(context, "��ǰ���򲻴��ڿ�������Activity", Toast.LENGTH_SHORT ).show();
//						}
//					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
          
            Toast.makeText(context, "Touched view " + viewIndex, Toast.LENGTH_SHORT).show();
        } else if (action.equals(BT_ACTION)) {
        	// ��������ť�ĵ���¼��Ĺ㲥  �������� Widget��enable��ʱ����������
            // ����ɱ���̷���
            Intent serviceIntent = new Intent(context,WidgetKillTaskService.class);
            context.startService(serviceIntent);
        	//��BroadcastReceiver�в���bind����
     //      conn = new MyConn();
       //     context.bindService(intent, conn,Context.BIND_AUTO_CREATE);
         //   upBinder.KillAllTASK(context);
          //  context.unbindService(conn);
            Toast.makeText(context, "KillAllTASK", Toast.LENGTH_SHORT).show();
            context.stopService(serviceIntent);
            
        }
        
		super.onReceive(context, intent);
	}
//  �ظ���ӵ�ʱ�򲻻���ִ�� onEnbled ֻ��ִ�� onUpdate
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		Log.d(TAG, "onUpdate");
		
	   	for (int appWidgetId:appWidgetIds) {
	    	// ��ȡAppWidget��Ӧ��RemoteViews �Ա��ں���ʹ��RemoteView�ķ������Ƶ����ť��ʱ���Ͷ�Ӧ��ACTION
	        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.ssg_appwidget);
	        
	        // ������Ӧ ����ť(clear)�� ��intent
	        Intent btIntent = new Intent().setAction(BT_ACTION);
	        PendingIntent btPendingIntent = PendingIntent.getBroadcast(context, 0, btIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	        rv.setOnClickPendingIntent(R.id.bt_widget_clear, btPendingIntent);	        
	        
	        // ���� ��GridView(gridview)�� ��adapter��
	        // (01) intent: ��Ӧ���� GridWidgetService(RemoteViewsService) ��intent  
	        // (02) setRemoteAdapter: ���� gridview��������
	        //    ͨ��setRemoteAdapter��gridview��GridWidgetService����������
	        //    �Դﵽͨ�� GridWidgetService ���� gridview ��Ŀ��
	        
	        
	        // ������Ӧ ��GridView(gridview)�� ��intentģ��	        
	        // ˵���������Ͽؼ�(��GridView��ListView��StackView��)���а����ܶ���Ԫ�أ���GridView�����ܶ���ӡ�
	        //     ���ǲ�������ͨ�İ�ťһ��ͨ�� setOnClickPendingIntent ���õ���¼���������ͨ��������
	        //        (01) ͨ�� setPendingIntentTemplate ���� ��intentģ�塱�����ǱȲ����ٵģ�
	        //        (02) Ȼ���ڴ���á����Ͽؼ�����RemoteViewsFactory���getViewAt()�ӿ��� ͨ�� setOnClickFillInIntent ���á����Ͽؼ���ĳһ������ݡ�
	        Intent serviceIntent = new Intent(context, WidgetGridViewService.class);        
	        rv.setRemoteAdapter(R.id.gv_widget, serviceIntent);
	        //��� intent ��������onReceive������ܵ���intent
	        Intent gridIntent = new Intent();
	        gridIntent.setAction(GRIDVIEW_ACTION);
	        gridIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
	        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, gridIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	        // ����intentģ��
	        rv.setPendingIntentTemplate(R.id.gv_widget, pendingIntent);
	        // ���ü��Ϲ������Լ��Ͻ��и���
	        appWidgetManager.updateAppWidget(appWidgetId, rv);
    	}
		
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	// ûɾ��һ��Widget �ͻ�ִ��һ��ɾ��
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.d(TAG, "onDeleted");
		super.onDeleted(context, appWidgetIds);
	}
	
	// onEnabed widget��һ�δ�����ʱ�� ִ�еķ��� 
	@Override
	public void onEnabled(Context context) {
		// ��ʼ��widget���ݵĲ���,�����Ժ��̨ 
		Log.d(TAG, "onEnabled");
		service = new Intent(context,UpdateWidgetService.class);
		context.startService(service);
		super.onEnabled(context);
	}

	@Override
	public void onDisabled(Context context) {
		// �����е�widget����ɾ����ʱ�� ִ�� ondisable()��
		// ֹͣ���ǿ����ķ���
		// ɾ�������ļ� ��ʱ�ļ�
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
