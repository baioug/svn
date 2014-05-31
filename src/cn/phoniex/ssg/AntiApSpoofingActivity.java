package cn.phoniex.ssg;


import java.util.List;

import cn.phoniex.ssg.dao.WiFiDetailDAO;
import cn.phoniex.ssg.domain.WiFiDetail;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AntiApSpoofingActivity extends Activity {

	private IntentFilter mWifiIntentFilter;
	private mWifiIntentReceiver mWifiIntentReceiver;
	private Handler       mHandler;
	private String strToast;
	private ListView lv_detail;
	private wifiDetailAdapter adapter;
	private List<WiFiDetail> details;
	private WiFiDetailDAO dao;
	private Dialog dialog;
	private TextView  tv_cmac;
	private TextView  tv_cspeed;
	private TextView  tv_crssi;
	
	private TextView  tv_title;
	
	private TextView  tv_nmac;
	private TextView  tv_nspeed;
	private TextView  tv_nrssi;

	
	@SuppressLint("HandlerLeak")
	private Handler  handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			lv_detail.setAdapter(adapter);
			
		}
		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.anti_ap_spoofing);
		dao = new WiFiDetailDAO(this);
		if (getIntent().getBooleanExtra("fromnotify", false)) {
			showDetailDialog();
		}
		lv_detail = (ListView) findViewById(R.id.lv_wifi_appstatus);
		new Thread(){

			@Override
			public void run() {
				details = dao.getALL();
				adapter = new wifiDetailAdapter(details);
				handler.sendEmptyMessage(0);
				super.run();
			}
			
			
		}.start();
		
		reFlashListView();
		
		WifiInfo wifiInfo = ((WifiManager)getSystemService(WIFI_SERVICE)).getConnectionInfo();
        /*
        info.getBSSID()；      获取BSSID地址。
        info.getSSID()；       获取SSID地址。  需要连接网络的ID
        info.getIpAddress()；  获取IP地址。4字节Int, XXX.XXX.XXX.XXX 每个XXX为一个字节
        info.getMacAddress()； 获取MAC地址。
        info.getNetworkId()；  获取网络ID。
        info.getLinkSpeed()；  获取连接速度，可以让用户获知这一信息。
        info.getRssi()；       获取RSSI，RSSI就是接受信号强度指示
         */
		 int Ip = wifiInfo.getIpAddress();
	        String strIp = "" + (Ip & 0xFF) + "." + ((Ip >> 8) & 0xFF) + "." + ((Ip >> 16) & 0xFF) + "." + ((Ip >> 24) & 0xFF);
	        
	       strToast = "BSSID : " + wifiInfo.getBSSID() + "\nSSID : " + wifiInfo.getSSID() + 
	        		"\nIpAddress : " + strIp + "\nMacAddress : " + wifiInfo.getMacAddress() +
	        		"\nNetworkId : " + wifiInfo.getNetworkId() + "\nLinkSpeed : " + wifiInfo.getLinkSpeed() + "Mbps" + 
	        		"\nRssi : " + wifiInfo.getRssi();
	        		wifiInfo.getIpAddress();	
	        		
	     Toast.makeText(AntiApSpoofingActivity.this, strToast, Toast.LENGTH_LONG).show();
		
	    mWifiIntentFilter = new IntentFilter();
	    mWifiIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
	                
	    mWifiIntentReceiver = new mWifiIntentReceiver();
	    registerReceiver(mWifiIntentReceiver, mWifiIntentFilter);
		mHandler = new Handler();
       mHandler.post(new TimerProcess());
		
		
	}
	

	private void showDetailDialog() {
		WifiInfo info = ((WifiManager)getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
		WiFiDetail detail = dao.find(info.getSSID());
		dialog =  new Dialog(this, R.style.MyDialog);
		View view = View.inflate(this, R.layout.show_notify_detail, null);
		
		tv_title = (TextView) view.findViewById(R.id.tv_snd_title);
		
		tv_cmac = (TextView) view.findViewById(R.id.tv_snd_cmac);
		tv_cspeed = (TextView) view.findViewById(R.id.tv_snd_cspeed);
		tv_crssi = (TextView) view.findViewById(R.id.tv_snd_crssi);
		
		tv_nmac = (TextView) view.findViewById(R.id.tv_snd_nmac);
		tv_nspeed = (TextView) view.findViewById(R.id.tv_snd_nspeed);
		tv_nrssi = (TextView) view.findViewById(R.id.tv_snd_nrssi);
		tv_title.setText(info.getSSID()+"\r\n可能为钓鱼热点，存在安全隐患");
		tv_cmac.setText("当前本机保存的MAC:"+detail.getMAC());
		tv_crssi.setText("信号强度:"+detail.getRSSI()+"dbm");
		tv_cspeed.setText("连接速度:"+detail.getLinkspeed()+"MBps");
		
		tv_nmac.setText("当前连接的无线MAC:"+info.getBSSID());
		tv_nrssi.setText("信号强度:"+info.getRssi()+"dbm");
		tv_nspeed.setText("连接速度:"+info.getLinkSpeed()+"MBps");
		
		dialog.setContentView(view);
		dialog.show();
	}


	public class wifiDetailAdapter extends BaseAdapter{

		private List<WiFiDetail> detaillist;
		
		
		public wifiDetailAdapter(List<WiFiDetail> detaillist) {
			super();
			this.detaillist = detaillist;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return detaillist.size();
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
			View view = View.inflate(AntiApSpoofingActivity.this, R.layout.wifi_detail_item, null);
			WiFiDetail detail = detaillist.get(position);
			TextView tv_ssid  = (TextView) view.findViewById(R.id.tv_wifi_ssid);
			TextView tv_rssi  = (TextView) view.findViewById(R.id.tv_wifi_rssi);
			TextView tv_pwd  = (TextView) view.findViewById(R.id.tv_wifi_pwd);
			TextView tv_mac  = (TextView) view.findViewById(R.id.tv_wifi_mac);
			TextView tv_speed  = (TextView) view.findViewById(R.id.tv_wifi_speed);
			tv_mac.setText("MAC:"+detail.getMAC());
			tv_pwd.setText("密码:"+detail.getPassword());
			tv_rssi.setText("信号强度:"+String.valueOf(detail.getRSSI()));
			tv_speed.setText("连接速度"+String.valueOf(detail.getLinkspeed())+"MBps");
			tv_ssid.setText(detail.getSSID());
			if (detail.getPassword().isEmpty()) {
				tv_pwd.setText("密码暂未获取");
			}
			if (detail.getRSSI() < -10) {
				tv_rssi.setTextColor(Color.GREEN);
				
			}else if (detail.getRSSI() < - 30) {
				tv_rssi.setTextColor(Color.YELLOW);
			}else {
				tv_rssi.setTextColor(Color.RED);
			}
			
			
			return view;
		}
		
	}
	
	
	private void reFlashListView() {

		details = dao.getALL();
		adapter = new wifiDetailAdapter(details);
		handler.sendEmptyMessage(0);

	}
	
	
	@Override
	protected void onDestroy() {
		if (mWifiIntentReceiver != null) {
			unregisterReceiver(mWifiIntentReceiver);
		}
		super.onDestroy();
	}

	
    private class TimerProcess implements Runnable{
		public void run() {
			reFlashListView();
            mHandler.postDelayed(this, 8*1000);
		}
    }
	
    
	 private class mWifiIntentReceiver extends BroadcastReceiver{

			public void onReceive(Context context, Intent intent) {

				WifiInfo info = ((WifiManager)getSystemService(WIFI_SERVICE)).getConnectionInfo();
				
		        int Ip = info.getIpAddress();
		        String strIp = "" + (Ip & 0xFF) + "." + ((Ip >> 8) & 0xFF) + "." + ((Ip >> 16) & 0xFF) + "." + ((Ip >> 24) & 0xFF);
		        
		       String  infoTosatStr = "BSSID : " + info.getBSSID() + "\nSSID : " + info.getSSID() + 
		        		"\nIpAddress : " + strIp + "\nMacAddress : " + info.getMacAddress() +
		        		"\nNetworkId : " + info.getNetworkId() + "\nLinkSpeed : " + info.getLinkSpeed() + "Mbps" + 
		        		"\nRssi : " + info.getRssi();
	          //  mIconWifi.setImageLevel(Math.abs(info.getRssi()));
	        
	            /*
		        WifiManager.WIFI_STATE_DISABLING   正在停止
	        	WifiManager.WIFI_STATE_DISABLED    已停止
	        	WifiManager.WIFI_STATE_ENABLING    正在打开
	        	WifiManager.WIFI_STATE_ENABLED     已开启
	        	WifiManager.WIFI_STATE_UNKNOWN     未知
	             */
	        	
	            switch (intent.getIntExtra("wifi_state", 0)) {
	            case WifiManager.WIFI_STATE_DISABLING:
	            	//Toast.makeText(AntiApSpoofingActivity.this, "WIFI STATUS : WIFI_STATE_DISABLING", Toast.LENGTH_SHORT).show();
	                break;
	            case WifiManager.WIFI_STATE_DISABLED:
	            	//Toast.makeText(AntiApSpoofingActivity.this, "WIFI STATUS : WIFI_STATE_DISABLED", Toast.LENGTH_SHORT).show();
	                break;
	            case WifiManager.WIFI_STATE_ENABLING:
	            	//Toast.makeText(AntiApSpoofingActivity.this, "WIFI STATUS : WIFI_STATE_ENABLING", Toast.LENGTH_SHORT).show();
	                break;
	            case WifiManager.WIFI_STATE_ENABLED:
	            	Toast.makeText(AntiApSpoofingActivity.this, "WIFI STATUS : WIFI_STATE_ENABLED", Toast.LENGTH_SHORT).show();
	            //	Toast.makeText(AntiApSpoofingActivity.this, infoTosatStr, Toast.LENGTH_LONG).show();
	                break;
	            case WifiManager.WIFI_STATE_UNKNOWN:
	            	Toast.makeText(AntiApSpoofingActivity.this, "WIFI STATUS : WIFI_STATE_UNKNOWN", Toast.LENGTH_SHORT).show();
	                break;
	                }
	            }
	    }
	
	

}
