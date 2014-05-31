package cn.phoniex.ssg;

import cn.phoniex.ssg.util.MD5Encoder;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LostSetActivity extends Activity  implements OnClickListener{

	
	private float startX,endX;
	private float startY;
	private float endY;
	private ImageButton imb_left;
	private ImageButton imb_right;
	private ImageButton imb_content;
	private ImageButton imb_ok;
	private CheckBox cb_bind;
	private CheckBox cb_prostart;
	private EditText et_safeno;
	private SharedPreferences sp;
	
	private static final String TAG = "LostSetActivity";
	private boolean bfirst = false;
	private EditText et_pwd;
	private EditText et_pwd_confirm;
	private AlertDialog alertDlg;
	private Dialog dialog;
	private boolean bIn = false;
	
//	private TextView tv_bind;
//	private TextView tv_start;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.lostset);
		//setFinishOnTouchOutside(false);
		imb_content = (ImageButton) findViewById(R.id.imb_setpro_content);
		imb_left = (ImageButton) findViewById(R.id.imb_setpro_left);
		imb_right = (ImageButton) findViewById(R.id.imb_setpro_right);
		imb_ok = (ImageButton) findViewById(R.id.imb_setpro_ok);
		cb_bind = (CheckBox) findViewById(R.id.cb_bind);
		cb_prostart = (CheckBox) findViewById(R.id.cb_pro);
		et_safeno = (EditText) findViewById(R.id.et_safeno);
		//ll_lost = (LinearLayout) findViewById(R.id.ll_lostset);
		// onTouch是View的方法，不过在linearlayout上设置的貌似只能接受到 按下的事件 接收不到其他的，
		// 应该是和布局中的其他空间产生冲突了，改为设置Activity的onTouchEvent
		//ll_lost.setOnTouchListener(this);// 注册触屏监听器
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		// 获取启动的intent中 alreadyin 的值
		bIn = getIntent().getBooleanExtra("alreadyin", false);
		bfirst = sp.getBoolean("lostfirst", true);
		// 判定是否已经进入过设计界面了,如果该次进入程序的生存周期内还未进入过设置界面 那么弹出对话框
		if (!bIn) {
			if (bfirst) {
				//显示设置密码对话框
				showSetPassDlg();
				//setContentView(R.layout.set_phonelost_pass);
			}else {
				showEnterPassDlg();
				//setContentView(R.layout.lostprotect);
			}
		}
		
		imb_left.setOnClickListener(this);
		imb_right.setOnClickListener(this);
		imb_content.setOnClickListener(this);
		imb_ok.setOnClickListener(this);
		cb_bind.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			//需要改变CheckBox的状态，不一定要通过点击事件来实现，直接调用setChecked()方法就可以达到你的效果，
			//这样的话OnClickListener就监听不到了，但是OnCheckChangedListener可以监听
			@SuppressLint("ResourceAsColor")
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					//启动开机完成 广播接受者判定手机号
					Editor editor = sp.edit();
					editor.putBoolean("bindsim", true);
					editor.commit();
					//cb_bind.setTextColor(R.color.positivetext);
					//cb_bind.setBackgroundColor(R.color.positivetext);
					cb_bind.setTextColor(Color.GREEN);
					cb_bind.setText("已开启绑定SIM卡");
				}
				else{
					//取消注册广播接受者？
					Editor editor = sp.edit();
					editor.putBoolean("bindsim", false);
					editor.commit();
				//	cb_bind.setTextColor(R.color.negativetext);
					//cb_bind.setTextColor(R.color.negativetext);
					cb_bind.setTextColor(Color.RED);
					cb_bind.setText("未开启绑定SIM卡");
				}
			}
		});
		cb_prostart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@SuppressLint("ResourceAsColor")
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					//启动手机防盗功能
					Editor editor = sp.edit();
					editor.putBoolean("lostprotect", true);
					editor.commit();
					//cb_prostart.setTextColor(R.color.positivetext);
					cb_prostart.setTextColor(Color.GREEN);
					cb_prostart.setText("已开启保护");
				}
				else{
					//设置手机防盗标志为false
					Editor editor = sp.edit();
					editor.putBoolean("lostprotect", false);
					editor.commit();
					//cb_prostart.setTextColor(R.color.negativetext);
					cb_prostart.setTextColor(Color.RED);
					cb_prostart.setText("未开启保护");
				}
			}
		});
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 设置界面的id
		case R.id.imb_setpro_left:
			Intent intentleft = new Intent(this,LostSetHelpActivity.class);
			finish();
			startActivityForResult(intentleft, 0);
			break;

		case R.id.imb_setpro_right:
			Intent intentRight = new Intent(this,LostSetGuardActivity.class);
			finish();
			startActivityForResult(intentRight, 0);
			break;
		case R.id.imb_setpro_content:
			Intent intentcontent = new Intent(this,SelectContactActivity.class);
			startActivityForResult(intentcontent, 0);
			break;
		case R.id.imb_setpro_ok:
			String phoStr = et_safeno.getText().toString().trim();
			if (phoStr.length() != 11) {
				Toast.makeText(LostSetActivity.this, "安全号码要求长度为11位", Toast.LENGTH_SHORT).show();
			}else {
				Editor editor = sp.edit();
				editor.putString("safephone", phoStr);
				editor.commit();
				Toast.makeText(LostSetActivity.this, "设置完毕", Toast.LENGTH_SHORT).show();
				finish();
			}
			break;
			//密码对话框控件的ID
		case R.id.bt_enterpass_cancle:
			finish();//取消输入密码 直接结束设置Activity
			break;
		case R.id.bt_enterpass_ok:
			String passstrStr = et_pwd.getText().toString().trim();
			if (passstrStr.length()<1 || passstrStr.length()>20) {
				Toast.makeText(this, "密码长度非法", Toast.LENGTH_SHORT).show();
			}
			else {
				String enPass = MD5Encoder.encode(passstrStr);
				String realPass = sp.getString("lostpass", "");
				if (realPass.length()<1) {
					finish();
					break;
				}
				if (enPass.equalsIgnoreCase(realPass)) {
					Log.i(TAG,"加载手机防盗设置界面");
					bIn = true;
					alertDlg.dismiss();
				}
				else {
					Toast.makeText(this, "错误的密码，请确认输入无误", Toast.LENGTH_SHORT).show();
					finish();
				}
			}
			break;
		case R.id.bt_setpass_cancle:
			dialog.dismiss();
			finish();
			break;
		case R.id.bt_setpass_ok:
			String passStr1 =et_pwd.getText().toString().trim();
			String passStr2 =et_pwd_confirm.getText().toString().trim();
			if ("".equals(passStr1) || "".equals(passStr2) || passStr1.length() > 20 || passStr2.length() >20 ) {
				Toast.makeText(this, "密码长度非法", Toast.LENGTH_SHORT).show();
				finish();
				return;
			}else {
				
				if (passStr1.equals(passStr2)) {
					Editor editor = sp.edit();
					String enpass = MD5Encoder.encode(passStr1);
					editor.putString("lostpass", enpass);
					editor.putBoolean("lostfirst", false);
					editor.commit();
					Log.i(TAG,"加载手机防盗设置界面");
					bIn = true;
					dialog.dismiss();
					
				}else {
					Toast.makeText(getApplicationContext(), "两次密码不同", 0).show();
					finish();
					return;
				}
			}
			break;
			
			
		default:
			break;
		}
	}

	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()){
        case MotionEvent.ACTION_DOWN:
        	//Toast.makeText(this, "ACTION_DOWN", Toast.LENGTH_SHORT).show();
                startX = event.getX();
                startY = event.getY();
                break;
        case MotionEvent.ACTION_MOVE:
        //	Toast.makeText(this, "ACTION_MOVE", Toast.LENGTH_SHORT).show();
        	break;
        case MotionEvent.ACTION_UP:
        //	Toast.makeText(this, "ACTION_UP", Toast.LENGTH_SHORT).show();
                endX = event.getX();
                endY = event.getY();
                if(startX - endX > 150 && Math.abs(startY - endY) < 200){
                //	Toast.makeText(this, "左滑动手势", Toast.LENGTH_SHORT).show();
                	finish();
                    startActivityForResult(new Intent(this,LostSetGuardActivity.class), 0);
            	}
               else if (startX -endX <-100) {
        		Intent intentRight = new Intent(this,LostSetHelpActivity.class);
        	  //	Toast.makeText(this, "右滑动手势", Toast.LENGTH_SHORT).show();
        	  	finish();
        		startActivityForResult(intentRight,0);
            }
                break;
        }
		return super.onTouchEvent(event);
	}

//	@Override
//	public boolean onTouch(View arg0, MotionEvent event) {
//	 
//	            return super.onTouchEvent(event);
//
//	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 0) {
			if (data != null) {
				String number = data.getStringExtra("number");
				et_safeno.setText(number);
			}
		}
		//由于我们Activity的切换是直接用的startActivity 不存在返回的情况 所以该逻辑不行 注释掉
//		else if(resultCode == 1){
//			bIn = true;
//		}
	}


	//  Dialog 的style 配置中添加 <item name="android:windowCloseOnTouchOutside">false</item> 
	// 避免点击其他区域导致对话框被取消
	private void showSetPassDlg() {
		//AlertDialog.Builder builder = new Builder(this);
		dialog =  new Dialog(this, R.style.MyDialog);
		View view = View.inflate(this, R.layout.set_phonelost_pass, null);
		et_pwd = (EditText) view.findViewById(R.id.ed_setpass_1);
		et_pwd_confirm = (EditText) view.findViewById(R.id.ed_setpass_2);
		Button bt_set_ok = (Button) view.findViewById(R.id.bt_setpass_ok);
		Button bt_set_cancle =  (Button) view.findViewById(R.id.bt_setpass_cancle);
		bt_set_ok.setOnClickListener(this);
		bt_set_cancle.setOnClickListener(this);
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
	private void showEnterPassDlg() {
		
		AlertDialog.Builder builder = new Builder(this);
		View view = View.inflate(this, R.layout.lost_enter_pass, null);
		et_pwd = (EditText) view.findViewById(R.id.ed_enterpass);
		Button bt_set_ok = (Button) view.findViewById(R.id.bt_enterpass_ok);
		Button bt_set_cancle =  (Button) view.findViewById(R.id.bt_enterpass_cancle);
		bt_set_ok.setOnClickListener(this);
		bt_set_cancle.setOnClickListener(this);
		alertDlg = builder.setView(view).create();
		// 不允许在对话框外点击取消对话框 模态对话框？
		alertDlg.setCanceledOnTouchOutside(false);
		// 给对话框设置按键监听器，过滤后退按钮
		alertDlg.setOnKeyListener(new DialogInterface.OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				   if (keyCode == KeyEvent.KEYCODE_SEARCH)
				    {  return true;  }
				    else //默认返回 false
				    {  return false; }
				   }
			  });
		alertDlg.show();
		
		
	}

}
