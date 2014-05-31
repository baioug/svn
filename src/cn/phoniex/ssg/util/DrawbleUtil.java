package cn.phoniex.ssg.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class DrawbleUtil {

	public static Bitmap reSizeDrawableBitmap(Context context, Bitmap bitmap) {
		//Bitmap bitmap = drawable.getBitmap();
		//BitmapDrawable drawable
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		
		DisplayMetrics outMetrics =new DisplayMetrics();
		display.getMetrics(outMetrics);
		if (outMetrics.heightPixels <480 || outMetrics.widthPixels < 320) {
			return Bitmap.createScaledBitmap(bitmap, 32, 32, false);
		}else if(outMetrics.heightPixels >1024 || outMetrics.widthPixels > 700){
			return Bitmap.createScaledBitmap(bitmap, 64, 64, false);
		}
		else {
			return Bitmap.createScaledBitmap(bitmap, 48, 48, false);
		}
	}
	
	public static Bitmap drawableToBitamp(Drawable drawable)
    {
		Bitmap bitmap;
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        System.out.println("Drawable转Bitmap");
        Bitmap.Config config = 
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        bitmap = Bitmap.createBitmap(w,h,config);
        //注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);   
        drawable.setBounds(0, 0, w, h);   
        drawable.draw(canvas);
		return bitmap;
    }
}
