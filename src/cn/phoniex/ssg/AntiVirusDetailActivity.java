package cn.phoniex.ssg;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.PrivateCredentialPermission;

import cn.phoniex.ssg.domain.AntiVirusInfo;


import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class AntiVirusDetailActivity extends Activity {

	private ListView lv_avd;
	private TextView tv_title;
	private List<AVDetailInfo> avDetailInfos;
	private AVDetailAdapter adapter;
	private ArrayList<AntiVirusInfo> arrayList;
	private TextView tv_content;
	private ImageView  imv_icon;
	private ProgressDialog  pd;
	private  Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			adapter = new AVDetailAdapter(avDetailInfos);
			lv_avd.setAdapter(adapter);
			pd.dismiss();
		}
		
	};
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.anti_virus_detail);
		lv_avd = (ListView) findViewById(R.id.lv_avd);
		tv_title = (TextView) findViewById(R.id.tv_avd_title);
		arrayList = (ArrayList<AntiVirusInfo>) getIntent().getSerializableExtra("key");
		int itype = getIntent().getIntExtra("type", -1);
		String spStr = "";
		if (itype < 8) {
			switch (itype) {
			case 0:
				spStr = getString(R.string.sp_call);
				break;
			case 1:
				spStr = getString(R.string.sp_sendsms);
				break;
			case 2:
				spStr = getString(R.string.sp_phone);
				break;
			case 3:
				spStr = getString(R.string.sp_readsms);
				break;
			case 4:
				spStr = getString(R.string.sp_location);
				break;
			case 5:
				spStr = getString(R.string.sp_sdcard);
				break;
			case 6:
				spStr = getString(R.string.sp_internet);
				break;
			case 7:
				spStr = getString(R.string.sp_camera);
				break;

			default:
				break;
			}
		}
		
		pd = ProgressDialog.show(AntiVirusDetailActivity.this, "请稍候...", "正在加载...",true,false);
		
		tv_title.setText("共有"+arrayList.size()+"项申请了"+spStr+"权限");
		
		lv_avd.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Uri delUri = Uri.parse("package:"+avDetailInfos.get(position).pkgName);
				Intent deliIntent = new Intent();
				deliIntent.setAction(Intent.ACTION_UNINSTALL_PACKAGE);
				deliIntent.setData(delUri);
				startActivityForResult(deliIntent, 0);
				
			}
			
		});
		
		new Thread(){

			@Override
			public void run() {
				initList(arrayList);
				handler.sendEmptyMessage(0);
				super.run();
			}
			
		}.start();
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 0) {
			new Thread(){

				@Override
				public void run() {
					initList(arrayList);
					handler.sendEmptyMessage(0);
					super.run();
				}
				
			}.start();
		}
	}

	public class AVDetailAdapter extends BaseAdapter{
		
		private List<AVDetailInfo> avdInfos;
		
		public AVDetailAdapter(List<AVDetailInfo> avdInfos) {
			super();
			this.avdInfos = avdInfos;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return avdInfos.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(AntiVirusDetailActivity.this, R.layout.anti_virus_detail_item, null);
			tv_content = (TextView) view.findViewById(R.id.tv_avditem);
			imv_icon = (ImageView) view.findViewById(R.id.imv_avditem_icon);
			tv_content.setText(avdInfos.get(position).appName);
			imv_icon.setImageDrawable(avdInfos.get(position).icon);
			
			return view;
		}
		
		
	}
	
	public void initList(ArrayList<AntiVirusInfo> arrayList){
		
		PackageInfo packageInfo;
		avDetailInfos = new ArrayList<AVDetailInfo>();
		for (AntiVirusInfo antiVirusInfo : arrayList) {
			try {
				AVDetailInfo info = new AVDetailInfo();
				packageInfo = getPackageManager().getPackageInfo(antiVirusInfo.pkgName, 0);
				ApplicationInfo applicationInfo =  packageInfo.applicationInfo;
				info.appName = applicationInfo.loadLabel(getPackageManager()).toString();
				info.pkgName = packageInfo.packageName;
				info.icon = applicationInfo.loadIcon(getPackageManager());
				avDetailInfos.add(info);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}

	}
	
	
	public class AVDetailInfo{
		public String pkgName;
		public String appName;
		public  Drawable icon;
		
	}
	
}
