package cn.phoniex.ssg.util;

import cn.phoniex.ssg.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.support.v4.view.WindowCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class customToast {

	private final static WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
	private static WindowManager wManager;
	private static View view;
	private static  SharedPreferences sp;

	
	
	public static void showToast(Context context , String text)
	{
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		wManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	LayoutInflater inflate = (LayoutInflater)
	                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 view = inflate.inflate(R.layout.customtoast, null);
	TextView tv = (TextView) view.findViewById(R.id.tv_ctoast_name);
	ImageView imv = (ImageView) view.findViewById(R.id.imv_ctoast_ico);
	tv.setText(text);
	setUserDefineStyle(view);
	int uerdefinex = sp.getInt("lastx", 100);
	int userdefiney = sp.getInt("lasty", 100);
	
    final WindowManager.LayoutParams params = mParams;

    params.height = WindowManager.LayoutParams.WRAP_CONTENT;
    params.width = WindowManager.LayoutParams.WRAP_CONTENT;
    params.format = PixelFormat.TRANSLUCENT;
    params.type = WindowManager.LayoutParams.TYPE_TOAST;
    params.setTitle("Toast");
    params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
    params.gravity = Gravity.LEFT | Gravity.TOP;
    params.x = uerdefinex;
    params.y = userdefiney;
    wManager.addView(view, params);

    //报错 空指针 如下的设置方式是相对布局来控制控件位置的 我们的xml文件是linearlayout的不能转换
   // LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
   // layoutParams.leftMargin = uerdefinex;
   // layoutParams.topMargin = userdefiney;
	//view.setLayoutParams(params);
		
	}

	public static void dismiss() {
		if (wManager != null) {
			wManager.removeView(view);
		}
	}

	public static void setUserDefineStyle(View view)
	{
		String[] colors = new String[]{"半透明","欢乐橙","金属黑","天空蓝","嫩芽绿","轻松蓝","香槟褐"};
		int  reskey = sp.getInt("locbgcolor", 0);
		switch (reskey) {
		case 0:
			view.setBackgroundResource(R.drawable.transbg);
			break;
		case 1:
			view.setBackgroundResource(R.drawable.orange);
			break;
		case 2:
			view.setBackgroundResource(R.drawable.lblackbg);
			break;
		case 3:
			view.setBackgroundResource(R.drawable.skybluebg);
			break;
		case 4:
			view.setBackgroundResource(R.drawable.lgreenbg);
			break;
		case 5:
			view.setBackgroundResource(R.drawable.lbluebg);
			break;
		case 6:
			view.setBackgroundResource(R.drawable.brownbg);
			break;

		default:
			break;
		}

		
	}
	
}
