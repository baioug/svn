package cn.phoniex.ssg.ui;

import java.io.File;

import cn.phoniex.ssg.MainActivity;
import cn.phoniex.ssg.R;
import cn.phoniex.ssg.domain.Updateinfo;
import cn.phoniex.ssg.engine.UpdateInfoService;
import cn.phoniex.ssg.engine.downloadFile;
import android.R.anim;
import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Type;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends Activity {

	private ProgressDialog pd;
	private TextView tv_sp;
	private LinearLayout ll_sp;
	private Updateinfo info = null;
	private String verStr;
	private Handler handler =  new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			if (isNeedUpdate(verStr)) {
				showUpdateDialog();
			}
		}
		
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		pd = new ProgressDialog(this);
		//这是水平显示经度
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setTitle("正在下载.....");
		tv_sp = (TextView) findViewById(R.id.tv_sp);
		ll_sp = (LinearLayout)findViewById(R.id.ll_sp);

	

		new Thread()
		{

			@Override
			public void run() {
				super.run();
				try {
					sleep(2000);
					handler.sendEmptyMessage(0);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}.start();
		// 获取系统版本信息 sdk 19 Release 4.4.2
		//TextView tvTextView = new TextView(this);
		//tvTextView.setText("SDK: "+android.os.Build.VERSION.SDK +" \r\n Release: "+ android.os.Build.VERSION.RELEASE);
		//tvTextView.setTextColor(Color.GREEN);
		//ll_sp.addView(tvTextView,0);
		 verStr = getVersion();
		tv_sp.setText("版本号:"+verStr);
		//设置动画
		AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
		aa.setDuration(2000);//显示时间
		ll_sp.startAnimation(aa);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
	}

	private void showUpdateDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("升级提醒");
		builder.setMessage(info.getDescriptionStr());
		builder.setCancelable(false);//不允许取消
		builder.setPositiveButton("确定更新",new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					downloadFileTask task = new downloadFileTask(info.getApkUrlString(),Environment.getExternalStorageDirectory().getPath()+"ssg"+info.getVerStr()+".apk");
				    pd.show();
					new Thread(task).start();
				}
				else {
					Toast.makeText(getApplicationContext(), "ExternalStorager unviliable!",Toast.LENGTH_SHORT).show();
				}
			}
		});
		builder.setNegativeButton("取消更新",new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				LoadMainActivity();
			}
		});
		
	}

	private boolean isNeedUpdate(String verStr) {
		UpdateInfoService updateInfoService = new UpdateInfoService(this);
		try {
			info = updateInfoService.getUpdateinfo();
			if (info!= null) {
				 String ver = info.getVerStr();
				 if (!ver.equals(verStr)) {
					 return true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();	
			LoadMainActivity();
		}
		LoadMainActivity();
			return false;
	}

	private void LoadMainActivity()
	{
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();//把当前Activity从任务栈中移除，避免后退到这个界面
	}

	private String getVersion() {
		PackageManager pm = getPackageManager();
		PackageInfo  info = null ;
		try {//"cn.phoniex.ssg"
			info = pm.getPackageInfo(getPackageName(),0 );
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	
	}

	class downloadFileTask implements Runnable
	{
		private String urlStr;
		private String path;
		
		
		public downloadFileTask(String urlStr, String path) {
			super();
			this.urlStr = urlStr;
			this.path = path;
		}


		@Override
		public void run() {

			try {
				 File file =   downloadFile.getFile(urlStr, path, pd);
				installApK(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	public void installApK(File file) {
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		//String type = getMIMEType(file);
		intent.setData(Uri.fromFile(file));
		intent.setType("application/vnd.android.package-archive");
		finish();
		startActivity(intent);
	}


	
}
