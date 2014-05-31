package cn.phoniex.ssg;

import cn.phoniex.ssg.util.MD5Encoder;
import cn.phoniex.ssg.util.SHA1Encoder;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ApplockEnterActivity extends Activity implements OnClickListener{
	
	private boolean bFirst = true;
	private SharedPreferences sp;
	private EditText et_pwd;
	private EditText et_pwd_confirm;
	private Dialog dialog;
	private AlertDialog alertDlg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		bFirst = sp.getBoolean("applockfirst", true);
		if (bFirst) {
			//显示设置密码对话框
			showSetPassDlg();
		}else {
			showEnterPassDlg();
		}
	}


	private void showSetPassDlg() {
		//AlertDialog.Builder builder = new Builder(this);
		dialog =  new Dialog(this, R.style.MyDialog);
		View view = View.inflate(this, R.layout.app_lock_setpass_dialog, null);
		et_pwd = (EditText) view.findViewById(R.id.et_ladlg_setpass_1);
		et_pwd_confirm = (EditText) view.findViewById(R.id.et_ladlg_setpass_2);
		Button bt_set_ok = (Button) view.findViewById(R.id.bt_ladlg_setpass_ok);
		Button bt_set_cancle =  (Button) view.findViewById(R.id.bt_ladlg_setpass_cancle);
		bt_set_ok.setOnClickListener(this);
		bt_set_cancle.setOnClickListener(this);
		dialog.setContentView(view);
		dialog.show();
	}
	
	
	private void showEnterPassDlg() {
		
		AlertDialog.Builder builder = new Builder(this);
		View view = View.inflate(this, R.layout.app_lock_enter_dialog, null);
		et_pwd = (EditText) view.findViewById(R.id.et_ladlg_enterpass);
		Button bt_set_ok = (Button) view.findViewById(R.id.bt_ladlg_enterpass_ok);
		Button bt_set_cancle =  (Button) view.findViewById(R.id.bt_ladlg_enterpass_cancle);
		bt_set_ok.setOnClickListener(this);
		bt_set_cancle.setOnClickListener(this);
		alertDlg = builder.setView(view).create();
		alertDlg.show();
		
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_ladlg_enterpass_cancle:
			alertDlg.dismiss();
			break;
		case R.id.bt_ladlg_enterpass_ok:
			String passstrStr = et_pwd.getText().toString().trim();
			if (passstrStr.length()<1 || passstrStr.length()>20) {
				Toast.makeText(this, "密码长度非法", Toast.LENGTH_SHORT).show();
			}
			else {
				String enPass = SHA1Encoder.sha1Lower(passstrStr);
				String realPass = sp.getString("applockpwd", "");
				if (realPass.length()<1) {
					alertDlg.dismiss();
					break;
				}
				if (enPass.equalsIgnoreCase(realPass)) {
					Intent intent = new Intent(this,AppLockActivity.class);
					intent.putExtra("token", "myself");
					startActivity(intent);
				}
				else {
					Toast.makeText(this, "错误的密码，请确认输入无误", Toast.LENGTH_SHORT).show();
				}
			}
			alertDlg.dismiss();
			break;
		case R.id.bt_ladlg_setpass_cancle:
			dialog.dismiss();
			finish();
			break;
		case R.id.bt_ladlg_setpass_ok:
			String passStr1 =et_pwd.getText().toString().trim();
			String passStr2 =et_pwd_confirm.getText().toString().trim();
			if ("".equals(passStr1) || "".equals(passStr2) || passStr1.length() > 20 || passStr2.length() >20 ) {
				Toast.makeText(this, "密码长度非法", Toast.LENGTH_SHORT).show();
				return;
			}else {
				
				if (passStr1.equals(passStr2)) {
					Editor editor = sp.edit();
					String enpass = SHA1Encoder.sha1Lower(passStr1);
					editor.putString("applockpwd", enpass);
					editor.putBoolean("applockfirst", false);
					editor.commit();
					Intent intent = new Intent(this,AppLockActivity.class);
					intent.putExtra("token", "myself");
					startActivity(intent);
				}else {
					Toast.makeText(getApplicationContext(), "两次密码不同", 0).show();
					return;
				}
			}
			dialog.dismiss();
			finish();
			break;

		default:
			break;
		}
	}

}
