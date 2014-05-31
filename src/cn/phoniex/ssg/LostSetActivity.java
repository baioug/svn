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
		// onTouch��View�ķ�����������linearlayout�����õ�ò��ֻ�ܽ��ܵ� ���µ��¼� ���ղ��������ģ�
		// Ӧ���ǺͲ����е������ռ������ͻ�ˣ���Ϊ����Activity��onTouchEvent
		//ll_lost.setOnTouchListener(this);// ע�ᴥ��������
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		// ��ȡ������intent�� alreadyin ��ֵ
		bIn = getIntent().getBooleanExtra("alreadyin", false);
		bfirst = sp.getBoolean("lostfirst", true);
		// �ж��Ƿ��Ѿ��������ƽ�����,����ôν����������������ڻ�δ��������ý��� ��ô�����Ի���
		if (!bIn) {
			if (bfirst) {
				//��ʾ��������Ի���
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
			//��Ҫ�ı�CheckBox��״̬����һ��Ҫͨ������¼���ʵ�֣�ֱ�ӵ���setChecked()�����Ϳ��Դﵽ���Ч����
			//�����Ļ�OnClickListener�ͼ��������ˣ�����OnCheckChangedListener���Լ���
			@SuppressLint("ResourceAsColor")
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					//����������� �㲥�������ж��ֻ���
					Editor editor = sp.edit();
					editor.putBoolean("bindsim", true);
					editor.commit();
					//cb_bind.setTextColor(R.color.positivetext);
					//cb_bind.setBackgroundColor(R.color.positivetext);
					cb_bind.setTextColor(Color.GREEN);
					cb_bind.setText("�ѿ�����SIM��");
				}
				else{
					//ȡ��ע��㲥�����ߣ�
					Editor editor = sp.edit();
					editor.putBoolean("bindsim", false);
					editor.commit();
				//	cb_bind.setTextColor(R.color.negativetext);
					//cb_bind.setTextColor(R.color.negativetext);
					cb_bind.setTextColor(Color.RED);
					cb_bind.setText("δ������SIM��");
				}
			}
		});
		cb_prostart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@SuppressLint("ResourceAsColor")
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					//�����ֻ���������
					Editor editor = sp.edit();
					editor.putBoolean("lostprotect", true);
					editor.commit();
					//cb_prostart.setTextColor(R.color.positivetext);
					cb_prostart.setTextColor(Color.GREEN);
					cb_prostart.setText("�ѿ�������");
				}
				else{
					//�����ֻ�������־Ϊfalse
					Editor editor = sp.edit();
					editor.putBoolean("lostprotect", false);
					editor.commit();
					//cb_prostart.setTextColor(R.color.negativetext);
					cb_prostart.setTextColor(Color.RED);
					cb_prostart.setText("δ��������");
				}
			}
		});
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// ���ý����id
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
				Toast.makeText(LostSetActivity.this, "��ȫ����Ҫ�󳤶�Ϊ11λ", Toast.LENGTH_SHORT).show();
			}else {
				Editor editor = sp.edit();
				editor.putString("safephone", phoStr);
				editor.commit();
				Toast.makeText(LostSetActivity.this, "�������", Toast.LENGTH_SHORT).show();
				finish();
			}
			break;
			//����Ի���ؼ���ID
		case R.id.bt_enterpass_cancle:
			finish();//ȡ���������� ֱ�ӽ�������Activity
			break;
		case R.id.bt_enterpass_ok:
			String passstrStr = et_pwd.getText().toString().trim();
			if (passstrStr.length()<1 || passstrStr.length()>20) {
				Toast.makeText(this, "���볤�ȷǷ�", Toast.LENGTH_SHORT).show();
			}
			else {
				String enPass = MD5Encoder.encode(passstrStr);
				String realPass = sp.getString("lostpass", "");
				if (realPass.length()<1) {
					finish();
					break;
				}
				if (enPass.equalsIgnoreCase(realPass)) {
					Log.i(TAG,"�����ֻ��������ý���");
					bIn = true;
					alertDlg.dismiss();
				}
				else {
					Toast.makeText(this, "��������룬��ȷ����������", Toast.LENGTH_SHORT).show();
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
				Toast.makeText(this, "���볤�ȷǷ�", Toast.LENGTH_SHORT).show();
				finish();
				return;
			}else {
				
				if (passStr1.equals(passStr2)) {
					Editor editor = sp.edit();
					String enpass = MD5Encoder.encode(passStr1);
					editor.putString("lostpass", enpass);
					editor.putBoolean("lostfirst", false);
					editor.commit();
					Log.i(TAG,"�����ֻ��������ý���");
					bIn = true;
					dialog.dismiss();
					
				}else {
					Toast.makeText(getApplicationContext(), "�������벻ͬ", 0).show();
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
                //	Toast.makeText(this, "�󻬶�����", Toast.LENGTH_SHORT).show();
                	finish();
                    startActivityForResult(new Intent(this,LostSetGuardActivity.class), 0);
            	}
               else if (startX -endX <-100) {
        		Intent intentRight = new Intent(this,LostSetHelpActivity.class);
        	  //	Toast.makeText(this, "�һ�������", Toast.LENGTH_SHORT).show();
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
		//��������Activity���л���ֱ���õ�startActivity �����ڷ��ص���� ���Ը��߼����� ע�͵�
//		else if(resultCode == 1){
//			bIn = true;
//		}
	}


	//  Dialog ��style ��������� <item name="android:windowCloseOnTouchOutside">false</item> 
	// ���������������¶Ի���ȡ��
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
		// �������ڶԻ�������ȡ���Ի��� ģ̬�Ի���
		alertDlg.setCanceledOnTouchOutside(false);
		// ���Ի������ð��������������˺��˰�ť
		alertDlg.setOnKeyListener(new DialogInterface.OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				   if (keyCode == KeyEvent.KEYCODE_SEARCH)
				    {  return true;  }
				    else //Ĭ�Ϸ��� false
				    {  return false; }
				   }
			  });
		alertDlg.show();
		
		
	}

}
