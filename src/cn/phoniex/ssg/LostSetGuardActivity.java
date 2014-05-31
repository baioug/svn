package cn.phoniex.ssg;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class LostSetGuardActivity extends Activity implements OnClickListener, OnTouchListener {

	private ImageButton imb_right =  null;
	private ImageButton imb_left = null;
	private LinearLayout ll = null;
	private float startX,endX;
	private float startY;
	private float endY;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.lostsetguard);
		imb_left = (ImageButton) findViewById(R.id.imb_setguard_left);
		imb_right = (ImageButton) findViewById(R.id.imb_setguard_right);
		imb_left.setOnClickListener(this);
		imb_right.setOnClickListener(this);
		ll = (LinearLayout) findViewById(R.id.ll_setguard);
		ll.setOnTouchListener(this);
		ll.setLongClickable(true);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imb_setguard_left:
			Intent intentleft = new Intent(this,LostSetActivity.class);
			//使用返回码的方式只能用于返回到启动者的Activity，我们的启动Activity已经被我finish了
			//所以即时是设置返回码 也不会执行 启动者 Activity中的 onActivityResult
			//setResult(1,intentleft);//避免返回到主界面的时候 重复弹出输入密码的输入框
			finish();
			//通过设置 intent 的extra传递信息
			intentleft.putExtra("alreadyin", true);
			startActivity(intentleft);
			break;

		case R.id.imb_setguard_right:
			Intent intentRight = new Intent(this,LostSetHelpActivity.class);
			finish();
			startActivity(intentRight);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
	            switch(event.getAction()){
	            case MotionEvent.ACTION_DOWN:
	            //	Toast.makeText(this, "ACTION_DOWN", Toast.LENGTH_SHORT).show();
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
	                    if(startX - endX > 150 && Math.abs(startY - endY) < 100){
	                    //	Toast.makeText(this, "左滑动手势", Toast.LENGTH_SHORT).show();
	                    	finish();
	                    	Intent  intentleft  = new Intent(this,LostSetHelpActivity.class);
	                    	intentleft.putExtra("alreadyin", true);
	                        startActivity(intentleft);	
	                	}
	                   else if (startX -endX <-100) {
	            		Intent intentRight = new Intent(this,LostSetActivity.class);
	            		intentRight.putExtra("alreadyin", true);
	            	  //	Toast.makeText(this, "右滑动手势", Toast.LENGTH_SHORT).show();
	            	  	finish();
	            		startActivity(intentRight);
	                }
	                    break;
	            }
	            return super.onTouchEvent(event);

	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, 
			float velocityY)
	{
		 if(e1.getX() > e2.getX()) {//向左滑动  
   
				Intent intentleft = new Intent(this,LostSetActivity.class);
				startActivity(intentleft);
	       }else if(e1.getX() < e2.getX()) {//向右滑动  

				Intent intentRight = new Intent(this,LostSetHelpActivity.class);
				startActivity(intentRight);
	       }else {     
	           return false;     
	       }     
	       return true;  
		
	}

	
}
