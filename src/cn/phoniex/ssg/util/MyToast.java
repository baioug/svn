package cn.phoniex.ssg.util;

import cn.phoniex.ssg.R;
import cn.phoniex.ssg.R.drawable;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyToast {
	
	public static void showToast(Context context, int icoId , String content ,  int   duration)
	{
		Toast toast = new Toast(context);
		toast.setDuration(duration);
		toast.setGravity(Gravity.BOTTOM, 0, 25);
		
		LinearLayout ll =  new LinearLayout(context);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		ll.setGravity(Gravity.CENTER_VERTICAL);
		
		ImageView imView  = new ImageView(context);
		Resources res = context.getResources();
		Drawable drawable = res.getDrawable(icoId);
		Bitmap bitmap = BitmapFactory.decodeResource(res, icoId);
		DrawbleUtil.reSizeDrawableBitmap(context, bitmap);
		imView.setImageResource(icoId);
		 ll.addView(imView);
		 TextView tView = new TextView(context);
		 tView.setText(content);
		 tView.setTextColor(Color.WHITE);
		 tView.setBackgroundColor(Color.BLACK);
		 
		 ll.addView(tView);
		 
		 toast.setView(ll);
		 toast .show();
		
	}

}
