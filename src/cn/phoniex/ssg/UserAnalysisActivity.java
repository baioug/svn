package cn.phoniex.ssg;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.phoniex.ssg.dao.UserAnalysisDAO;
import cn.phoniex.ssg.domain.UserAnalysisInfo;
import cn.phoniex.ssg.service.UserAnalysisService;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class UserAnalysisActivity extends Activity {

	
	public ImageView imv;
	public TextView tv_name;
	public TextView tv_pagname;
	private TextView tv_cut;
	private UserAnalysisDAO dao;
	private List<String> pkgnameList;
	private UserAnalysisAdapter adapter;
	private List<UserAnalysisInfo> UserInfosList;
	private CheckBox cb_start;
	private TextView tv_start;
	private ListView lv_user;
	private LinearLayout ll_start;
	private SharedPreferences sp;
	private boolean bStart;
	private Intent service;
	private TimerTask task;
	private Timer  timer;
	
	@SuppressLint("HandlerLeak")
	private Handler userHandler =  new Handler(){

		@Override
		public void handleMessage(Message msg) {
			
			super.handleMessage(msg);
			if (msg.what == 0) {
				adapter = new UserAnalysisAdapter(UserInfosList);
				lv_user.setAdapter(adapter);
				//Toast.makeText(UserAnalysisActivity.this, "Show User Analysis Detail",Toast.LENGTH_SHORT).show();
				
			}
		}
		
	};
	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			setContentView(R.layout.user_analysis);
			sp = getSharedPreferences("config", Context.MODE_PRIVATE);
			tv_start = (TextView) findViewById(R.id.tv_user_start);
			cb_start = (CheckBox) findViewById(R.id.cb_user_start);
			lv_user = (ListView) findViewById(R.id.lv_user_appstatus);
			ll_start = (LinearLayout) findViewById(R.id.ll_user_start);
			bStart = sp.getBoolean("useranalysis", false);
			service = new Intent(UserAnalysisActivity.this,UserAnalysisService.class);
			cb_start.setChecked(bStart);
			
			if (bStart) {
				tv_start.setTextColor(Color.GREEN);
				startService(service);
				tv_start.setText("APP使用统计已开启");
			}else {
				tv_start.setTextColor(Color.RED);
				stopService(service);
				tv_start.setText("APP使用统计已关闭");
			}
			
			// 统一到CheckBox 的状态监听器中来修改sp配置文件和开启关闭服务
			// 在linearLayout和Checkbox的点击事件中只修改textview的显示和点击状态
			cb_start.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (cb_start.isChecked()) {
						
						tv_start.setTextColor(Color.GREEN);
						tv_start.setText("APP使用统计已开启");
					}else {
						
						tv_start.setTextColor(Color.RED);
						tv_start.setText("APP使用统计已关闭");
					}
				}
			});
			
			lv_user.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					if (position < pkgnameList.size()) {
						String pkgStr = pkgnameList.get(position);
						Intent  intent  = getPackageManager().getLaunchIntentForPackage(pkgStr);
						if (intent != null) {
							startActivity(intent);
						}else {
							Toast.makeText(UserAnalysisActivity.this, "找不到可启动的Activity", Toast.LENGTH_SHORT).show();
						}
						
					}
				}
				
			});
			
			ll_start.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					cb_start.setChecked(!cb_start.isChecked());
					//Intent service = new Intent(UserAnalysisActivity.this,UserAnalysisService.class);
					
					if (cb_start.isChecked()) {
						//startService(service);
						tv_start.setTextColor(Color.GREEN);
						tv_start.setText("APP使用统计已开启");
					}else {
						//stopService(service);
						tv_start.setTextColor(Color.RED);
						tv_start.setText("APP使用统计已关闭");
					}
				}
			});
			
			
			cb_start.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					Editor editor = sp.edit();
					if (cb_start.isChecked()) {
						startService(service);
						editor.putBoolean("useranalysis", true);
					}else {
						stopService(service);
						editor.putBoolean("useranalysis", false);
					}
					editor.commit();
				}
			});
			
			dao = new UserAnalysisDAO(this);
			//Time tm = new Time("GMT+8");
		//	tm.setToNow();
			//String dateStr = tm.year
		//	int ihour = (tm.hour+8)%24;//貌似之前设置的东八区没起作用还是晚八个小时
		//	SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");
			//String date = sdf.format(new Date());
		//	dao.add("cn.phoniex.ssg", date, tm.hour);
		//	dao.add("edu.nedu.uninstalldemo", "2014-05-16", 18);
			pkgnameList = dao.getByFilter(10);
			initUserAnalysisList(pkgnameList);
			timer = new Timer();
			task = new TimerTask() {
				
				@Override
				public void run() {
					try {
						Thread.sleep(601);
						
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					pkgnameList = dao.getByFilter(10);
					initUserAnalysisList(pkgnameList);
					userHandler.sendEmptyMessage(0);
					
				}
			};
			timer.schedule(task, 601*100, 10*1000);
			
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	
	public void initUserAnalysisList(List<String> pkgnamelist){
		UserInfosList = new ArrayList<UserAnalysisInfo>();
		PackageManager pm = getPackageManager();
		for (String name : pkgnamelist) {
			try {
				PackageInfo  info = pm.getPackageInfo(name, 0);
				String appName = info.applicationInfo.loadLabel(pm).toString();
				String pkgname = info.packageName;
				Drawable icon = info.applicationInfo.loadIcon(pm);
				UserAnalysisInfo userInfo  = new UserAnalysisInfo();
				userInfo.setAppname(appName);
				userInfo.setPackageName(pkgname);
				userInfo.setIcon(icon);
				userInfo.setIcut(dao.getcount(name));
				UserInfosList.add(userInfo);
				userInfo = null;
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		userHandler.sendEmptyMessage(0);
	}
	
	
	public class UserAnalysisAdapter extends BaseAdapter
	{
		private List<UserAnalysisInfo> userAnalysisInfos;
		
		public UserAnalysisAdapter(List<UserAnalysisInfo> AnalysisInfos) {
			this.userAnalysisInfos = AnalysisInfos;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return userAnalysisInfos.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return userAnalysisInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View view = null;
			// 复用之前的convertView 优化显示速度
			if (convertView == null) {
				view = View.inflate(UserAnalysisActivity.this, R.layout.user_analysis_item, null);
		
			}else {
				view = convertView;
			}
			//	view = View.inflate(AppManagerActivity.this, R.layout.lv_item, null);
			imv  = (ImageView) view.findViewById(R.id.imv_user_item_icon);
			tv_name = (TextView) view.findViewById(R.id.tv_user_item_appname);
			tv_pagname = (TextView) view.findViewById(R.id.tv_user_item_packageame);
			tv_cut   = (TextView) view.findViewById(R.id.tv_user_item_cut);
			tv_pagname.setText(userAnalysisInfos.get(position).getPackageName());
			imv.setImageDrawable(userAnalysisInfos.get(position).getIcon());
			tv_name.setText(userAnalysisInfos.get(position).getAppname());
			tv_cut.setText("频率:"+UserInfosList.get(position).getIcut());
			
			return  view;
		}
		
		
	}
	
	
}
