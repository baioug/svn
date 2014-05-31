package cn.phoniex.ssg.util;

import cn.phoniex.ssg.R;
import android.content.Context;

import android.graphics.Canvas;

import android.graphics.Movie;

import android.util.AttributeSet;

import android.view.View;

// 报错 Fatal signal 11 (SIGSEGV) at 0x00000000 (code=1), 
public class MygifView extends View {

	private long movieStart;

	private Movie movie;

	// 此处必须重写该构造方法

	public   MygifView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		// 以文件流（InputStream）读取进gif图片资源
		movie = Movie.decodeStream(getResources().openRawResource(
				R.drawable.gif_shleld));

	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		
		long curTime = android.os.SystemClock.uptimeMillis();
		// 第一次播放
		if (movieStart == 0) {
			movieStart = curTime;
		}
		if (movie != null) {
			int duraction = movie.duration();
			int relTime = (int) ((curTime - movieStart) % duraction);
			movie.setTime(relTime);
			movie.draw(canvas, 0, 0);
			// 强制重绘
			invalidate();

		}
		super.onDraw(canvas);
	}
}
