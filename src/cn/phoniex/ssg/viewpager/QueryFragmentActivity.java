package cn.phoniex.ssg.viewpager;

import java.util.ArrayList;

import cn.phoniex.ssg.R;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

public class QueryFragmentActivity extends FragmentActivity {
	private static final int DEFAULT_OFFSCREEN_PAGES = 1;

	private ViewPager mViewPager;
	private TabsAdapter mTabsAdapter;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//设置该属性之后 getActionBar 结果将为 null
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.viewpager_phonenum);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		//这是默认加载的页数 如果设为2那么就会在打开页面的时候预先加载第二页的内容
		//不过在这里我们的页面都很简单没必要预加载
		mViewPager.setOffscreenPageLimit(DEFAULT_OFFSCREEN_PAGES);

		final ActionBar bar = getActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		//bar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	
		mTabsAdapter = new TabsAdapter(this, mViewPager);
		mTabsAdapter.addTab(bar.newTab().setText(R.string.number_to_location),
				PhonoFragment.class, null);
		mTabsAdapter.addTab(bar.newTab().setText(R.string.location_to_number),
				CityFragment.class, null);
		mTabsAdapter.addTab(bar.newTab().setText(R.string.country_name),
				CountryFragment.class, null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}
	
	/**A adapter class,which extends FragmentPagerAdapter class and implements OnPageChangeListener 
	 * and ActionBar.TabListener interface.*/
	@SuppressLint("NewApi")
	public static class TabsAdapter extends FragmentPagerAdapter implements
			OnPageChangeListener, ActionBar.TabListener {
		private final Context mContext;
		private final ActionBar mActionBar;
		private final ViewPager mViewPager;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
		/**static inner class,stored tab informations.*/
		static final class TabInfo {
			private final Class<?> clss;
			private final Bundle args;
			private Fragment fragment;

			TabInfo(Class<?> _class, Bundle _args) {
				clss = _class;
				args = _args;
			}
		}

		public TabsAdapter(FragmentActivity activity, ViewPager pager) {
			super(activity.getSupportFragmentManager());
			mContext = activity;
			mActionBar = activity.getActionBar();
			mViewPager = pager;
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
		}
		
		/**Add tabs.*/
		public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
			TabInfo info = new TabInfo(clss, args);
			tab.setTag(info);
			tab.setTabListener(this);
			mTabs.add(info);
			mActionBar.addTab(tab);
			notifyDataSetChanged();
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onPageSelected(int position) {
			mActionBar.setSelectedNavigationItem(position);
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			// TODO Auto-generated method stub
		}

		@Override
		public Fragment getItem(int position) {
			// TODO Auto-generated method stub
			TabInfo info = mTabs.get(position);
			if (info.fragment == null)
				info.fragment = Fragment.instantiate(mContext,
						info.clss.getName(), info.args);
			return info.fragment;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mTabs.size();
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			Object tag = tab.getTag();
			for (int i = 0; i < mTabs.size(); i++) {
				if (mTabs.get(i) == tag) {
					mViewPager.setCurrentItem(i);
				}
			}
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
		}
	}
	

}
