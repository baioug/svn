package cn.phoniex.ssg;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class LostSetHelpActivity extends Activity {

	private float startX,endX;
	private float startY;
	private float endY;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.losthelp);
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
                	Intent intentLeft = new Intent(this,LostSetActivity.class);
                	intentLeft.putExtra("alreadyin", true);
                    startActivityForResult(intentLeft, 0);
            	}
               else if (startX -endX <-100) {
        		Intent intentRight = new Intent(this,LostSetGuardActivity.class);
        	  //	Toast.makeText(this, "右滑动手势", Toast.LENGTH_SHORT).show();
        	  	finish();
        		startActivityForResult(intentRight,0);
            }
                break;
        }
		return super.onTouchEvent(event);
	}
}
