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

	// 添加 android:permission="android.permission.BIND_REMOTEVIEWS" 权限
	// 160dp 120dp 放到 drawable-xxhdpi目录下很流畅 放到drawable目录下导致launcher很卡
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
		    private boolean brefreshed = false;//通知数据发生变化之后 getViewAt进行一次数据刷新
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
		        //虽然我们通知数据发生了改变，但是我们并未重新初始化pkgnames 和icons 所以导致显示错误
		        // service 好像一直存在 brefreshed 被赋值之后 不会在被初始化为false
		        if (position == 0) {
					pkgnames.clear();
					dao = new UserAnalysisDAO(mContext);
					pkgnames = dao.getByFilter(10);
					initUserAnalysisList(pkgnames);
				}
				// 获取 grid_view_item.xml 对应的RemoteViews
		      //这里导入的自然是item的布局文件 而不是widget的布局文件
				RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_grid_item);
//				if (icons.size() == 0) {
//					
//				}
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
				// 设置 第position位的“视图”的数据
				// icons初始化问题 可能并不一定是10  所以使用 icons大小而不是固定值
				if (position < icons.size()) {// 前十项设置为常用的APP
					// 把 Drawable转换为Bitmap
					BitmapDrawable bitmap = (BitmapDrawable) icons.get(position);
					rv.setImageViewBitmap(R.id.itemImage, bitmap.getBitmap());
				}else {
					map = (HashMap<String, Object>) data.get(position - icons.size());
					rv.setImageViewResource(R.id.itemImage, ((Integer)map.get(IMAGE_ITEM)).intValue());
				}
				// 设置 第position位的“视图”对应的响应事件
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
				// 初始化“集合视图”中的数据
				initGridViewData();
			}
			
			public int getCount() {
				// 返回“集合视图”中的数据的总数
				if (icons.size()<12) {//如果不够12项 后面使用照片填充
					return 12;
				}
				return icons.size();
				
			}
			
			public long getItemId(int position) {
				// 返回当前项在“集合视图”中的位置
				return position;
			}

			public RemoteViews getLoadingView() {
				return null;
			}
			
			public int getViewTypeCount() {
				// 只有一类 GridView
				return 1;
			}

			public boolean hasStableIds() {
				return true;
			}		
			//在 AppWidget的 onReceive中 调用提醒数据更新函数 来提示
			//在GridRemoteViewsFactory 中响应 更新
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

