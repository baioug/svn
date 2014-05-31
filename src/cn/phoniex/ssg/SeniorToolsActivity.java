package cn.phoniex.ssg;

import java.io.File;

import cn.phoniex.ssg.R.id;
import cn.phoniex.ssg.receiver.SSGAdminReceiver;
import cn.phoniex.ssg.service.BackUpService;
import cn.phoniex.ssg.service.ShowAddrService;
import cn.phoniex.ssg.viewpager.QueryFragmentActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.admin.DevicePolicyManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SeniorToolsActivity extends Activity implements OnClickListener{

	
	protected static final int ERROR = 10;
	protected static final int SUCCESS = 11;
	

	private TextView tv_queryaddr;
	private ProgressDialog pd;
	
	private int bgstyle = 0;
	
	private TextView tv_style;
	private TextView tv_setloc;
	private Intent serviceintent;
	private CheckBox cb_showloc;
	private TextView tv_locSer;
	private TextView tv_comNo;
	private LinearLayout ll_backup;
	private LinearLayout ll_restore;
	private LinearLayout ll_lockapp;
	private LinearLayout ll_userAnaly;
	private CheckBox cb_devadmin;
	private TextView tv_devadmin;
	private  TextView tv_backup;
	private TextView tv_restore;
	private TextView tv_lockapp;
	private LinearLayout ll_wifi;
	private SharedPreferences sp;
	private DevicePolicyManager dPmanager;
	private ComponentName  mAdminname;
	private boolean bbind = false;
	private BackUpService bkservice;
	private int iOps = 0;
	private final int OPS_BCK = 0;
	private final int OPS_RES = 1;
	
	private  ServiceConnection conn = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			bbind = false;
			bkservice = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			
			bkservice = ((BackUpService.BackUpBinder) service).getService();
			bbind = true;
			if (iOps == OPS_BCK) {
				bkservice.BackupSms();
			}else if (iOps == OPS_RES){
				bkservice.RestoreSms();
			}
			//bind 成功之后执行备份代码
		}
	};
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case ERROR:
				Toast.makeText(getApplicationContext(), "下载数据库失败", 0).show();
				break;
			case SUCCESS:
				Toast.makeText(getApplicationContext(), "下载数据库成功", 0).show();
				break;
			}
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.seniortools);
		
		 dPmanager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		 mAdminname = new ComponentName(this, SSGAdminReceiver.class);
		 
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		tv_queryaddr = (TextView) this.findViewById(R.id.tv_stools_query);
		tv_style = (TextView) this.findViewById(R.id.tv_stools_setstyle);
		tv_setloc = (TextView) this.findViewById(R.id.tv_stools_setloc);
		tv_locSer = (TextView) this.findViewById(R.id.tv_stools_addrser);
		tv_comNo = (TextView) this.findViewById(R.id.tv_stools_common_num);
//		tv_backup = (TextView) this.findViewById(R.id.tv_stools_backup);
//		tv_restore = (TextView) this.findViewById(R.id.tv_stools_restore);
//		tv_lockapp = (TextView) this.findViewById(R.id.tv_stools_lockapp);
		ll_backup = (LinearLayout) findViewById(R.id.ll_stools_backup);
		ll_restore = (LinearLayout) findViewById(R.id.ll_stools_restore);
		ll_lockapp = (LinearLayout) findViewById(R.id.ll_stools_lockapp);
		cb_showloc = (CheckBox) findViewById(R.id.cb_stools_addrser);
		cb_devadmin = (CheckBox) findViewById(R.id.cb_stools_devadmin);
		tv_devadmin = (TextView) findViewById(R.id.tv_stools_devadmin);
		ll_userAnaly = (LinearLayout) findViewById(R.id.ll_stools_user);
		ll_wifi = (LinearLayout) findViewById(R.id.ll_stools_wifi);

		tv_queryaddr.setOnClickListener(this);
		tv_style.setOnClickListener(this);
		tv_setloc.setOnClickListener(this);
		tv_locSer.setOnClickListener(this);
		tv_comNo.setOnClickListener(this);
		ll_backup.setOnClickListener(this);
		ll_restore.setOnClickListener(this);
		ll_lockapp.setOnClickListener(this);
		ll_userAnaly.setOnClickListener(this);
		ll_wifi.setOnClickListener(this);


		serviceintent = new Intent(this,ShowAddrService.class);
		
		cb_devadmin.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if(isChecked){
					startService(serviceintent);
					tv_devadmin.setTextColor(Color.GREEN);
					tv_devadmin.setText("已开启设备管理员权限");
				}else{
					stopService(serviceintent);
					tv_devadmin.setTextColor(Color.RED);
					tv_devadmin.setText("未开启设备管理员权限");
				}
				
			}
		});
		cb_showloc.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					startService(serviceintent);
					tv_locSer.setTextColor(Color.GREEN);
					tv_locSer.setText("号码归属地服务已经开启");
				}else{
					stopService(serviceintent);
					tv_locSer.setTextColor(Color.RED);
					tv_locSer.setText("号码归属地服务未开启");
				}
				
			}
		});

		tv_queryaddr.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_stools_query:
			// 判断来电归属地数据库是否存在
			Intent intent = new Intent(SeniorToolsActivity.this, QueryFragmentActivity.class);
			startActivity(intent);
			/*
			if (isDBexist()) {

				//Intent intent = new Intent(this, QueryPhoAddrActivity.class);
				//Intent intent = new Intent(this, NumFragmentActivity.class);
				//startActivity(intent);
			}else{
				//提示用户下载数据库
				pd  = new ProgressDialog(this);
				pd.setMessage("正在下载数据库");
				pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				pd.show();
				//下载数据库
				new Thread(){
					@Override
					public void run() {
						String path = getResources().getString(R.string.phoaddrdb);
						String filepath = "/sdcard/address.db";
						try {
							//DownLoadFileTask.getFile(path, filepath, pd);
							pd.dismiss();
							Message msg = new Message();
							msg.what = SUCCESS;
							handler.sendMessage(msg);
						} catch (Exception e) {
							e.printStackTrace();
							pd.dismiss();
							Message msg = new Message();
							msg.what = ERROR;
							handler.sendMessage(msg);
						}
					}
				}.start();
				
			}
			*/
			break;
		case R.id.tv_stools_setstyle:
			 AlertDialog.Builder builder = new Builder(this);
			 builder.setTitle("选择显示风格");
			 String[] colors = new String[]{"半透明","欢乐橙","金属黑","天空蓝","嫩芽绿","轻松蓝","香槟褐"};
			 builder.setSingleChoiceItems (colors, 0, new  DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					bgstyle = which;

				}
			});

			 builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					return;
				}
			});
			
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					Editor editor = sp.edit();
					editor.putInt("locbgcolor", bgstyle);
					editor.commit();
				}
			});
			builder.create().show();
			break;
			
			
		case R.id.tv_stools_setloc:
			// 更改来电归属地的显示位置
			Intent intentsetloc = new Intent(this,DragLocActivity.class);
			startActivity(intentsetloc);
			
			
			break;
		case R.id.tv_stools_common_num:
			
			break;
		case R.id.ll_stools_backup:
			AlertDialog.Builder bkbuilder =  new Builder(SeniorToolsActivity.this);
			bkbuilder.setTitle("选择需要备份的选项");
			final String[]  arroBckOps = new String[]{"只短信","通讯录","通话记录"};
			
			final boolean[] arrBckChked = new boolean[3];
			for (int i = 0; i < arrBckChked.length; i++) {
				arrBckChked[i] = false;
			}
			bkbuilder.setMultiChoiceItems(arroBckOps, arrBckChked,  new OnMultiChoiceClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					arrBckChked[which] = isChecked;
				}
			});
			bkbuilder.setPositiveButton("备份", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					iOps = OPS_BCK;
					if (arrBckChked[0]) {
						// 备份短信
						Intent bkIntent  = new Intent(SeniorToolsActivity.this,BackUpService.class);
						startService(bkIntent);
						bindService(bkIntent, conn, Context.BIND_AUTO_CREATE);
						
					}
					if (arrBckChked[1]) {
						//备份通讯录
					}
					if (arrBckChked[2]) {
						//备份通话记录
					}
				}
			});
			bkbuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
				
				}
			});
			
			break;
			
		case R.id.ll_stools_restore:
			AlertDialog.Builder resbuilder =  new Builder(SeniorToolsActivity.this);
			resbuilder.setTitle("选择需要还原的选项");
			final String[]  arroResOps = new String[]{"还原短信","还原通讯录","还原通话记录"};
			
			final boolean[] arrResChked = new boolean[3];
			for (int i = 0; i < arrResChked.length; i++) {
				arrResChked[i] = false;
			}
			resbuilder.setMultiChoiceItems(arroResOps, arrResChked,  new OnMultiChoiceClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					arrResChked[which] = isChecked;
				}
			});
			resbuilder.setPositiveButton("还原", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					iOps = OPS_RES;
					if (arrResChked[0]) {
						// 备份短信
						Intent bkIntent  = new Intent(SeniorToolsActivity.this,BackUpService.class);
						startService(bkIntent);
						bindService(bkIntent, conn, Context.BIND_AUTO_CREATE);
						
					}
					if (arrResChked[1]) {
						//备份通讯录
					}
					if (arrResChked[2]) {
						//备份通话记录
					}
				}
			});
			resbuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
				
				}
			});
			
			break;
		case R.id.ll_stools_lockapp:
			Intent locappIntent = new Intent(SeniorToolsActivity.this,ApplockEnterActivity.class);
			startActivity(locappIntent);
			break;
		case R.id.ll_stools_user:
			Intent userIntent = new Intent(SeniorToolsActivity.this,UserAnalysisActivity.class);
			startActivity(userIntent);
			break;
		case R.id.ll_stools_wifi:
			Intent wifiIntent = new Intent(SeniorToolsActivity.this,AntiApSpoofingActivity.class);
			startActivity(wifiIntent);
			break;
			
		}
		

	}

	/**
	 * 判断数据库是否存在
	 * 
	 * @return
	 */
	public boolean isDBexist() {
		File file = new File("/sdcard/address.db");
		return file.exists();

	}
	
	public void activeDevAdmin(Context context)
	{
		if (dPmanager.isAdminActive(mAdminname)) {
			return;
		}
		else {

			Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminname);
			//描述信息
			intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "开启设备管理员，以备手机丢失之后进行远程操作");
			//startActivity(intent);
			startActivityForResult(intent, 0);
			//dPm.lockNow();
			//dPm.resetPassword("123", 0);
			//dPm.wipeData(0);
			
		}
		
	}
	
	
	public void unActivieDevAdmin(Context context)
	{
		
		if (dPmanager.isAdminActive(mAdminname)) {
			dPmanager.removeActiveAdmin(mAdminname);
		}
	}

	@Override
	protected void onDestroy() {

		// 因为我们是先startservice 然后再bind 上去的 
		// 所以unbind 不会执行 service 的onDestroy 服务
		//注意判断服务是否已经开启和bind了，否则不要unbind
		if (bbind) {
			if (conn != null && bkservice != null) {
				unbindService(conn);
				bbind = false;
			}
		}

		super.onDestroy();
	}
	
	
	
}
