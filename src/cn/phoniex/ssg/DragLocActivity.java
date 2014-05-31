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
			
 /* setOnTouchListener ����ʹ�õ�ʱ�򷵻�ֵ��ҪΪtrue���������ܱ�֤�ƶ���ʱ���ܺ��ȡ��Ӧ�ļ�
            ��������һ�μ�������ÿ��ֻ��һ�����µ��¼���setOnTouchListener �� setOnClickListener ͬʱʹ��ʱ��
onTouch �ķ���ֵҪ��Ϊ false�������ȿ��Ա�֤�����ƶ�̧���¼����Ա����������ҵ���¼�Ҳ�ᱻ����
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
			            	//���»�ȡ��ǰ������,���³�ʼ�� ����һֱ���ݵ����Ļ�����λ��������ƫ�� �������imv�ؼ�˲����ʧ����Ļ��
			            	prolocX = (int) event.getRawX();
			            	prolocY = (int) event.getRawY();
			            	break;
			            case MotionEvent.ACTION_UP:
			            	//������ǿؼ������λ�� ��Ϊ���������÷�ʽ��ʹ��view.setLayoutParams(params);
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
	            return true;//���жϴ����¼����� ��� ����false �ͻ��ж��¼� ֻ�ܼ�⵽�����¼�
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
