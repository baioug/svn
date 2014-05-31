package cn.phoniex.ssg.service;


import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;
import android.os.Looper;
import android.util.Log;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import cn.phoniex.ssg.R;
import cn.phoniex.ssg.dao.UserAnalysisDAO;
import cn.phoniex.ssg.domain.UserAnalysisInfo;
import cn.phoniex.ssg.receiver.SSGWidget;


@SuppressLint("NewApi")
public class WidgetGridViewService  extends RemoteViewsService{

	// ��� android:permission="android.permission.BIND_REMOTEVIEWS" Ȩ��
	// 160dp 120dp �ŵ� drawable-xxhdpiĿ¼�º����� �ŵ�drawableĿ¼�µ���launcher�ܿ�
		private static final String TAG = "WidgetGridViewService";
		@Override
		public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
			Log.d(TAG, "WidgetGridViewService");
			return new GridRemoteViewsFactory(this, intent);
		}
		
		
		private class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

			private Context mContext;
			private int mAppWidgetId;		
			private List<String> pkgnames;
			private List<Drawable> icons;
			private UserAnalysisDAO dao;
		    private String IMAGE_ITEM = "imgage_item";
		    private boolean brefreshed = false;//֪ͨ���ݷ����仯֮�� getViewAt����һ������ˢ��
		    private ArrayList<HashMap<String, Object>> data;
		    
			
		    private int[] arrImages=new int[]{
		            R.drawable.gmv01, R.drawable.gmv02, R.drawable.gmv03, 
		            R.drawable.gmv04, R.drawable.gmv05, R.drawable.gmv06, 
		            R.drawable.gmv07, R.drawable.gmv08, R.drawable.gmv09,
		            R.drawable.gmv10, R.drawable.gmv11, R.drawable.gmv12
		            };
			
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
		    
			public GridRemoteViewsFactory(Context context, Intent intent) {
				mContext = context;
				dao = new UserAnalysisDAO(context);
				pkgnames = dao.getByFilter(10);
				initUserAnalysisList(pkgnames);
		        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
		                AppWidgetManager.INVALID_APPWIDGET_ID);
		        
		        Log.d(TAG, "GridRemoteViewsFactory mAppWidgetId:"+mAppWidgetId);
			}
			
			public RemoteViews getViewAt(int position) {
	            HashMap<String, Object> map; 
	           
		        Log.d(TAG, "GridRemoteViewsFactory getViewAt:"+position);
		        //��Ȼ����֪ͨ���ݷ����˸ı䣬�������ǲ�δ���³�ʼ��pkgnames ��icons ���Ե�����ʾ����
		        // service ����һֱ���� brefreshed ����ֵ֮�� �����ڱ���ʼ��Ϊfalse
		        if (position == 0) {
					pkgnames.clear();
					dao = new UserAnalysisDAO(mContext);
					pkgnames = dao.getByFilter(10);
					initUserAnalysisList(pkgnames);
				}
				// ��ȡ grid_view_item.xml ��Ӧ��RemoteViews
		      //���ﵼ�����Ȼ��item�Ĳ����ļ� ������widget�Ĳ����ļ�
				RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_grid_item);
//				if (icons.size() == 0) {
//					
//				}
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
				// ���� ��positionλ�ġ���ͼ��������
				// icons��ʼ������ ���ܲ���һ����10  ����ʹ�� icons��С�����ǹ̶�ֵ
				if (position < icons.size()) {// ǰʮ������Ϊ���õ�APP
					// �� Drawableת��ΪBitmap
					BitmapDrawable bitmap = (BitmapDrawable) icons.get(position);
					rv.setImageViewBitmap(R.id.itemImage, bitmap.getBitmap());
				}else {
					map = (HashMap<String, Object>) data.get(position - icons.size());
					rv.setImageViewResource(R.id.itemImage, ((Integer)map.get(IMAGE_ITEM)).intValue());
				}
				// ���� ��positionλ�ġ���ͼ����Ӧ����Ӧ�¼�
				Intent fillInIntent = new Intent();
			//	fillInIntent.setAction(SSGWidget.GRIDVIEW_ACTION);
				fillInIntent.putExtra(SSGWidget.SSGWIDGETEXTRA, position);
				rv.setOnClickFillInIntent(R.id.itemImage, fillInIntent);
				
				return rv;
			}		

		    private void initGridViewData() {
		    	data = new ArrayList<HashMap<String, Object>>();
		        
		        for (int i=0; i<12; i++) {
		            HashMap<String, Object> map = new HashMap<String, Object>(); 
		            map.put(IMAGE_ITEM, arrImages[i]);
		            data.add(map);
		        }
		    }
			
			public void onCreate() {
				Log.d(TAG, "onCreate");
				// ��ʼ����������ͼ���е�����
				initGridViewData();
			}
			
			public int getCount() {
				// ���ء�������ͼ���е����ݵ�����
				if (icons.size()<12) {//�������12�� ����ʹ����Ƭ���
					return 12;
				}
				return icons.size();
				
			}
			
			public long getItemId(int position) {
				// ���ص�ǰ���ڡ�������ͼ���е�λ��
				return position;
			}

			public RemoteViews getLoadingView() {
				return null;
			}
			
			public int getViewTypeCount() {
				// ֻ��һ�� GridView
				return 1;
			}

			public boolean hasStableIds() {
				return true;
			}		
			//�� AppWidget�� onReceive�� �����������ݸ��º��� ����ʾ
			//��GridRemoteViewsFactory ����Ӧ ����
			public void onDataSetChanged() {
//				pkgnames.clear();
//				pkgnames = dao.getByFilter(10);
//				initUserAnalysisList(pkgnames);
//				Looper.prepare();
//				Toast.makeText(WidgetGridViewService.this, "onDataSetChanged", Toast.LENGTH_SHORT).show();
//				Looper.loop();
			}
			
			public void onDestroy() {
				data.clear();
			}
		}
	}

