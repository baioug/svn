package cn.phoniex.ssg.util;

import cn.phoniex.ssg.R;
import android.content.Context;

import android.graphics.Canvas;

import android.graphics.Movie;

import android.util.AttributeSet;

import android.view.View;

// ���� Fatal signal 11 (SIGSEGV) at 0x00000000 (code=1), 
public class MygifView extends View {

	private long movieStart;

	private Movie movie;

	// �˴�������д�ù��췽��

	public   MygifView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		// ���ļ�����InputStream����ȡ��gifͼƬ��Դ
		movie = Movie.decodeStream(getResources().openRawResource(
				R.drawable.gif_shleld));

	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		
		long curTime = android.os.SystemClock.uptimeMillis();
		// ��һ�β���
		if (movieStart == 0) {
			movieStart = curTime;
		}
		if (movie != null) {
			int duraction = movie.duration();
			int relTime = (int) ((curTime - movieStart) % duraction);
			movie.setTime(relTime);
			movie.draw(canvas, 0, 0);
			// ǿ���ػ�
			invalidate();

		}
		super.onDraw(canvas);
	}
}
