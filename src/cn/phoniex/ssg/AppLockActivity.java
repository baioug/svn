package cn.phoniex.ssg;

import java.util.ArrayList;
import java.util.List;

import cn.phoniex.ssg.dao.AppLockDao;
import cn.phoniex.ssg.domain.Appinfos;
import cn.phoniex.ssg.engine.AppInfosProvider;
import cn.phoniex.ssg.util.MD5Encoder;
import cn.phoniex.ssg.util.ApplockFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.BoringLayout.Metrics;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AppLockActivity extends FragmentActivity  {

	private static final String TAG = "AppLockActivity";
	private ViewPager  mPager;
	private int currIndex = 0;
	private int bottomLineWidth;
	private int offset = 0;
	private int position;
    private ImageView ivBottomLine;
	private TextView tv_lock;
	private TextView tv_set;
	private ArrayList<Fragment> fragments;



    
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.app_lock_main);
		
		mPager  = (ViewPager) findViewById(R.id.vPager_loclapp);
		
		  LayoutInflater mInflater = getLayoutInflater();
	        View activityView = mInflater.inflate(R.layout.app_lock, null);
		 fragments = new ArrayList<Fragment>();
		Fragment fragment = new Fragment();
		ApplockFragment myFragment = new ApplockFragment();
		//导入包android.support.v4.app.Fragment; 而不是 android.app.Fragment;
		fragment = myFragment.newInstance(AppLockActivity.this,0);
		fragments.add(fragment);
		fragment = myFragment.newInstance(AppLockActivity.this,1);
		fragments.add(fragment);
		Log.d(TAG, "fragments.size "+fragments.size());
		mPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments));
		mPager.setCurrentItem(0);
		 mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	     tv_lock = (TextView) findViewById(R.id.tv_la_tab_lock);
	     tv_set = (TextView) findViewById(R.id.tv_la_tab_set);

	     tv_lock.setOnClickListener(new MyOnClickListener(0));
	     tv_set.setOnClickListener(new MyOnClickListener(1));
		 initPosition();

	}
	

	private void initPosition()
	{
		ivBottomLine = (ImageView) findViewById(R.id.imv_la_bottom_line);
		bottomLineWidth = ivBottomLine.getLayoutParams().width;
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		offset = (int) ((width / 2.0 - bottomLineWidth) / 2);
		position = (int) (width/2.0);
		
	}
	
    
	public class MyOnClickListener implements OnClickListener
	{

		private int icur;
		
		public MyOnClickListener(int icur) {
			this.icur = icur;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(icur);
			
		}
		
	}
	
	  public class MyOnPageChangeListener implements OnPageChangeListener {

	        public void onPageSelected(int arg0) {
	            Animation animation = null;
	            switch (arg0) {
	            case 0:
	                if (currIndex == 1) {
	                    animation = new TranslateAnimation(position, 0, 0, 0);
	                } 
	                break;
	            case 1:
	                if (currIndex == 0) {
	                    animation = new TranslateAnimation(0, position, 0, 0);
	                } 
	                break;

	            }
	            currIndex = arg0;
	            animation.setFillAfter(true);
	            animation.setDuration(300);
	            ivBottomLine.startAnimation(animation);
	        }

	        public void onPageScrolled(int arg0, float arg1, int arg2) {
	        }

	        public void onPageScrollStateChanged(int arg0) {
	        }
	    }
	
	  
	public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
	    private ArrayList<Fragment> fragmentsList;

	    public MyFragmentPagerAdapter(FragmentManager fm) {
	        super(fm);
	    }

	    public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
	        super(fm);
	        this.fragmentsList = fragments;
	    }

	    @Override
	    public int getCount() {
	        return fragmentsList.size();
	    }

	    @Override
	    public Fragment getItem(int arg0) {
	        return fragmentsList.get(arg0);
	    }

	    @Override
	    public int getItemPosition(Object object) {
	        return super.getItemPosition(object);
	    }

	}


}
