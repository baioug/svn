package cn.phoniex.ssg;

import com.ant.liao.GifView;
import com.ant.liao.GifView.GifImageType;

import cn.phoniex.ssg.service.CityGuardService;
import cn.phoniex.ssg.service.CityGuardService.CgBinder;
import cn.phoniex.ssg.util.SHA1Encoder;
import android.R.drawable;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

public class AppLockPopupActivity extends Activity {

	private EditText et_pwd;
	private Button   bt_en;
	private ImageView imv_icon;
	private TextView tv_Appname;
	private SharedPreferences sp;
	private String realPwd;
	private GifView gifView;
	private CgBinder cgbinder;
	private String pkgname;
	private myServiceConnection conn;
	
	@SuppressLint("NewApi")
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.app_lock_pwd);
		//模态对话框化该Activity
		//不允许在该窗口之外进行触屏操作避免点击其他部分 导致该对话框退出
		//按对话框以外的地方不起作用。按返回键还起作用
		setFinishOnTouchOutside(false);
		Intent intent = new Intent(AppLockPopupActivity.this,CityGuardService.class);
		conn = new myServiceConnection();
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
		et_pwd = (EditText) findViewById(R.id.et_lappup_pwd);
		bt_en = (Button) findViewById(R.id.bt_lappup_en);
		imv_icon = (ImageView) findViewById(R.id.imv_lappup_icon);
		tv_Appname = (TextView) findViewById(R.id.tv_lappup_name);
		
		gifView = (GifView) findViewById(R.id.gifv_shleld);
		gifView.setGifImage(R.drawable.gif_shleld);
		//设置显示方式 先加载后显示 边加载边显示 只显示第一帧再显示
		gifView.setGifImageType(GifImageType.SYNC_DECODER);
		
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		realPwd = sp.getString("applockpwd", "");
		// 从intent里面提取服务发给我们的包名
		pkgname = getIntent().getStringExtra("pkgname");
		
		PackageInfo pkgInfo;
		ApplicationInfo appInfo;
		 PackageManager pManager = getPackageManager();
		try {
			pkgInfo =  pManager.getPackageInfo(pkgname, 0);
			appInfo = pkgInfo.applicationInfo;
			Drawable icon = appInfo.loadIcon(getPackageManager());
			String appName = appInfo.loadLabel(getPackageManager()).toString();
			imv_icon.setImageDrawable(icon);
			tv_Appname.setText(appName);
			
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bt_en.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String pwdStr = et_pwd.getText().toString().trim();
				if (pwdStr.length()<6) {
					Toast.makeText(AppLockPopupActivity.this, "密码长度小于6", 0).show();
					return;
				}else if(pwdStr.length()>20) {
					Toast.makeText(AppLockPopupActivity.this,"密码长度大于20",1).show();
					return;
				}else {

					if (SHA1Encoder.sha1Lower(pwdStr).equals(realPwd)) {
						//暂时停止保护
						cgbinder.tmpStopProItem(pkgname);
						finish();
					}else {
						Toast.makeText(AppLockPopupActivity.this, "密码错误请重新输入", 0).show();
					}
				}
			}
		});
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//判断服务是否已经unbind 进行unbind操作
		if (cgbinder != null) {
			unbindService(conn);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			//截获返回按键事件 屏蔽返回按钮
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

    public class myServiceConnection implements ServiceConnection
{

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		cgbinder = (CgBinder)service;
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		
	}
	
}
	
	
}
