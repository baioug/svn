package cn.phoniex.ssg;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.phoniex.ssg.domain.InternetTrafficinfos;
import cn.phoniex.ssg.util.GetStrValue;

import android.R.integer;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification.Action;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class InternetTrafficActiviy extends Activity {

	private TextView tv_wifi;
	private TextView tv_mobile;
	private ListView  lv_detail;
	private Button  bt_clear;
	private Timer timer;
	private TimerTask timerTask;
	private List<InternetTrafficinfos> ITinfos;
	private List<InternetTrafficinfos> allInfos;
	private InternetTrafficAdapter adapter;
	private List<String> clearList;
	private CheckBox cb_item;
	private int iCb_pos;
	private boolean bcheck[];
	private ViewHolder  viewHolder;
	private List<Integer> list;
	private Handler handler =  new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 0) {
				adapter = new InternetTrafficAdapter(ITinfos);
				lv_detail.setAdapter(adapter);
				bcheck = new boolean[ITinfos.size()];
				for (int i = 0; i < ITinfos.size(); i++) {
					bcheck[i] = false;
				}
			}
			initTotalTraffic();
		}
		
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.internet_traffic);
		
		list = new ArrayList<Integer>();
		tv_mobile = (TextView) findViewById(R.id.tv_mobile_total);
		tv_wifi = (TextView) findViewById(R.id.tv_wifi_total);
		lv_detail = (ListView) findViewById(R.id.lv_it_detail);
		bt_clear = (Button) findViewById(R.id.bt_it_clear);

		clearList = new ArrayList<String>();
		allInfos = new ArrayList<InternetTrafficinfos>();
		initTotalTraffic();
		initAdapterList();
		// 设置 CheckBox的属性 是不可点击的 不获得焦点
		// android:focusable="false"
       // android:clickable="false" 
	// 屏蔽CheckBox响应点击事件，然后可以在OnItemClickListener 里面 处理每一项的CheckBox 事件
		lv_detail.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TranslateAnimation transAnim = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 0.0f,
						Animation.RELATIVE_TO_SELF, 0.5f,
						Animation.RELATIVE_TO_SELF, 0.0f,
						Animation.RELATIVE_TO_SELF, 0.0f);
				transAnim.setDuration(500);
				view.startAnimation(transAnim);
				CheckBox cb = (CheckBox) view.findViewById(R.id.cb_it_item);
				if (bcheck[position]) {
					cb.setChecked(false);
					//cb.setVisibility(View.GONE);
				}else {
					cb.setChecked(true);
					//cb.setVisibility(View.VISIBLE);
				}
				bcheck[position] = !bcheck[position];
				if (cb.isChecked()) {
					clearList.add(allInfos.get(position).getPackageName());
				}else {
					clearList.remove(allInfos.get(position).getPackageName());
				}
			}
			
		});
	
		
		
		bt_clear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (clearList != null) {
					if (clearList.size() >0) {
						KillAllBGPros(clearList);
					}
				}
				
			}
		});
		
	}
	
	protected void KillAllBGPros(List<String> list) {

	ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	for (int j = 0; j < list.size(); j++) {
		am.killBackgroundProcesses(list.get(j));	
	}
	 Toast.makeText(this, "共结束后台任务"+list.size()+"个", 0).show();
	
	}
	
	@Override
	protected void onStart() {
		timer = new Timer();
		timerTask = new TimerTask() {
			
			@Override
			public void run() {
				handler.sendEmptyMessage(1);
			}
		};
		timer.schedule(timerTask, 1000, 2000);
		super.onStart();
	}



	@Override
	protected void onStop() {
		timer.cancel();
		timer = null;
		timerTask = null;
		super.onStop();
	}



	private void initTotalTraffic(){
		//Rx接受的
		long mbRx = TrafficStats.getMobileRxBytes();
		long mbTx = TrafficStats.getMobileTxBytes();
		long mbTotal = mbRx + mbTx;
		
		long totalRx = TrafficStats.getTotalRxBytes();
		long totalTx = TrafficStats.getTotalTxBytes();
		long total = totalRx + totalTx;
		long wifiRx =  totalRx - mbRx;
		long wifiTx = totalTx - mbTx;
		
		long  wifiTotal = wifiRx + wifiTx;
		String text;
		if (tv_mobile != null) {
			tv_mobile.setText("(上传  "+GetStrValue.getValueOf(mbRx)+" 下载 "+GetStrValue.getValueOf(mbTx)+" 总量 "+GetStrValue.getValueOf(mbTotal)+")");
		}
		if (tv_wifi != null) {
			tv_wifi.setText("(上传  "+GetStrValue.getValueOf(wifiRx)+" 下载 "+GetStrValue.getValueOf(wifiTx)+" 总量 "+GetStrValue.getValueOf(wifiTotal)+")");
		}
		
	}
	
	public void  initAdapterList(){

		ITinfos = new ArrayList<InternetTrafficinfos>();

		
		new Thread(){

			@Override
			public void run() {
				super.run();
				List<ResolveInfo> resolveInfos;
				PackageManager pm = getPackageManager();
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_MAIN);//"android.intent.action.MAIN"
				intent.addCategory(Intent.CATEGORY_LAUNCHER);// "android.intent.category.LAUNCHER"
				resolveInfos = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
				for (ResolveInfo resolveInfo : resolveInfos) {
					
					InternetTrafficinfos info = new InternetTrafficinfos();
					info.setAppname(resolveInfo.activityInfo.applicationInfo.loadLabel(pm).toString());
					info.setIcon(resolveInfo.activityInfo.applicationInfo.loadIcon(pm));
					info.setPackageName(resolveInfo.activityInfo.packageName);
					if (IsSysApp(resolveInfo.activityInfo.applicationInfo)) {
						info.setSysApp(true);
					}else {
						info.setSysApp(false);
					}
					info.setUid(resolveInfo.activityInfo.applicationInfo.uid);
					ITinfos.add(info);
					info = null;
				}
				handler.sendEmptyMessage(0);
			}
			
			
		}.start();

		
	}
	
    public boolean IsSysApp(ApplicationInfo info) {
        if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            return true;
        } else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
            return true;
        }
        return false;
    }
    
	public class InternetTrafficAdapter extends BaseAdapter{

		private List<InternetTrafficinfos> userInfos;
		private List<InternetTrafficinfos> sysInfos;
		
		
		public InternetTrafficAdapter(List<InternetTrafficinfos> infosList) {
			userInfos = new ArrayList<InternetTrafficinfos>();
			sysInfos = new ArrayList<InternetTrafficinfos>();
			for (InternetTrafficinfos info : infosList) {
				if (info.isSysApp()) {
					sysInfos.add(info);
				}else {
					userInfos.add(info);
				}
			}
			for (InternetTrafficinfos info : userInfos) {
				allInfos.add(info);
			}
			for (InternetTrafficinfos info : sysInfos) {
				allInfos.add(info);
			}
			
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return allInfos.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return allInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View view;
			if (convertView != null) {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
				cb_item = viewHolder.checkItem;
			}else {
				view = View.inflate(InternetTrafficActiviy.this, R.layout.internet_traffic_item, null);
				cb_item = (CheckBox) view.findViewById(R.id.cb_it_item);
				viewHolder = new ViewHolder();
				viewHolder.checkItem = cb_item;
				view.setTag(viewHolder);
			}
			iCb_pos = position;
			ImageView imv = (ImageView) view.findViewById(R.id.imv_it_item_icon);
			TextView tv_name = (TextView) view.findViewById(R.id.tv_it_item_name);
			TextView tv_tx = (TextView) view.findViewById(R.id.tv_traffic_tx);
			TextView tv_rx = (TextView) view.findViewById(R.id.tv_it_item_rx);
			CheckBox  cb = (CheckBox) view.findViewById(R.id.cb_it_item);
			imv.setImageDrawable(allInfos.get(position).getIcon());
			tv_name.setText(allInfos.get(position).getAppname());
			int uid = allInfos.get(position).getUid();
			tv_rx.setText(GetStrValue.getValueOf(TrafficStats.getUidRxBytes(uid)));
			tv_tx.setText(GetStrValue.getValueOf(TrafficStats.getUidTxBytes(uid)));
			cb_item = (CheckBox) view.findViewById(R.id.cb_it_item);
			//cb_item.setTag(new Integer(position)); 
			//在这里position 是 在当前页面中的的位置，而不是在整个listview中的位置
			//所以不能使用  boolean数组来判定
			if (clearList.contains(allInfos.get(position).getPackageName())) {
				cb_item.setChecked(true);
			}else {
				cb_item.setChecked(false);
			}
			//在getView中不能 使用 position来注册监听器
			// 以为每一项的显示都会调用一次getview函数，最后显示完毕后，position就是当前页面显示的最后一项的序号+1;
			// 所以你点击其他的子项 操作的CheckBox 也是 那一项的
			return view;
		}
		
	}
	
	public class ViewHolder
	{
		private CheckBox checkItem;
	}

}
