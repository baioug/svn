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
		//ģ̬�Ի��򻯸�Activity
		//�������ڸô���֮����д��������������������� ���¸öԻ����˳�
		//���Ի�������ĵط��������á������ؼ���������
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
		//������ʾ��ʽ �ȼ��غ���ʾ �߼��ر���ʾ ֻ��ʾ��һ֡����ʾ
		gifView.setGifImageType(GifImageType.SYNC_DECODER);
		
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		realPwd = sp.getString("applockpwd", "");
		// ��intent������ȡ���񷢸����ǵİ���
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
					Toast.makeText(AppLockPopupActivity.this, "���볤��С��6", 0).show();
					return;
				}else if(pwdStr.length()>20) {
					Toast.makeText(AppLockPopupActivity.this,"���볤�ȴ���20",1).show();
					return;
				}else {

					if (SHA1Encoder.sha1Lower(pwdStr).equals(realPwd)) {
						//��ʱֹͣ����
						cgbinder.tmpStopProItem(pkgname);
						finish();
					}else {
						Toast.makeText(AppLockPopupActivity.this, "�����������������", 0).show();
					}
				}
			}
		});
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//�жϷ����Ƿ��Ѿ�unbind ����unbind����
		if (cgbinder != null) {
			unbindService(conn);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			//�ػ񷵻ذ����¼� ���η��ذ�ť
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
