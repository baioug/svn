package cn.phoniex.ssg;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class DragLocActivity extends Activity {

	SharedPreferences sp;
	private ImageView imV;
	private TextView tV;
	protected int prolocY;
	protected int prolocX;
	protected int lastX;
	protected int lastY;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dragaddrview);
		imV = (ImageView) findViewById(R.id.imv_drag_addr);
		imV.setOnTouchListener(new OnTouchListener() {
			

			@SuppressLint("NewApi")
			@Override
			
 /* setOnTouchListener 单独使用的时候返回值需要为true，这样才能保证移动的时候能后获取相应的监
            听，而非一次监听（即每次只有一个按下的事件）setOnTouchListener 和 setOnClickListener 同时使用时，
onTouch 的返回值要设为 false，这样既可以保证按下移动抬起事件可以被监听，并且点击事件也会被监听
*/
			public boolean onTouch(View v, MotionEvent event) {
				
				switch (v.getId()) {
				
				case R.id.imv_drag_addr:
					  switch(event.getAction()){
			            case MotionEvent.ACTION_DOWN:
			            	prolocX = (int) event.getRawX();
			            	prolocY = (int) event.getRawY();
			                    break;
			            case MotionEvent.ACTION_MOVE:
			            	int x,y;
			            	x = (int) event.getRawX();
			            	y = (int) event.getRawY();
							int dx = (int) (x-prolocX);
							int dy = (int) (y-prolocY);
							int l= imV.getLeft();
							int t= imV.getTop();
							int r = imV.getRight();
							int b = imV.getBottom();
							System.out.println(x+"    "+y+"   "+dx+"   "+dy);
			            	imV.layout(l+dx, t+dy, r+dx, b+dy);
			            	//setLayout(imV, x, y);
			            	//重新获取当前的坐标,重新初始化 否则一直根据点击屏幕最初的位置来计算偏移 结果就是imv控件瞬间消失到屏幕外
			            	prolocX = (int) event.getRawX();
			            	prolocY = (int) event.getRawY();
			            	break;
			            case MotionEvent.ACTION_UP:
			            	//保存的是控件的相对位置 因为后面我设置方式是使用view.setLayoutParams(params);
			            	lastX =(int) event.getRawX();
			            	lastX = imV.getLeft();
			            	lastY = (int) event.getRawY();
			            	lastY = imV.getTop();
			            	Editor editor = sp.edit();
			            	editor.putInt("lastx", lastX);
			            	editor.putInt("lasty", lastY);
			            	editor.commit();
			                    break;
			            }
						break;
				}
	            return true;//不中断触摸事件返回 如果 返回false 就会中断事件 只能检测到按下事件
				}

		});
	}
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		int x = sp.getInt("lastx", 0);  
        int y = sp.getInt("lasty", 0);
        setLayout(imV, x, y);
	}


	public static void setLayout(View view,int x,int y) 
	{ 
		LayoutParams params = (LayoutParams)view.getLayoutParams();
		params.leftMargin = x;
		params.topMargin = y;
		view.setLayoutParams(params);
		
	//MarginLayoutParams margin=new MarginLayoutParams(view.getLayoutParams()); 
	//margin.setMargins(x,y, x+margin.width, y+margin.height); 
	//RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin); 
	//view.setLayoutParams(layoutParams); 
	} 
	
}
