package cn.phoniex.ssg;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.phoniex.ssg.adapter.MainActivityAdapter;
import cn.phoniex.ssg.dao.UserAnalysisDAO;
import cn.phoniex.ssg.receiver.SSGAdminReceiver;
import cn.phoniex.ssg.service.KingsGuardService;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.GridView;

public class MainActivity extends Activity implements OnItemClickListener , OnDrawerOpenListener,
OnDrawerCloseListener{

	private static final String TAG = "MainActivity";
	private GridView gv_main;
	private GridView gv_content;
	private SlidingDrawerAdapter sdAdapter;
	private MainActivityAdapter adapter;
	@SuppressWarnings("deprecation")
	private SlidingDrawer slidingDrawer;
	private ImageView iv_icon;
	private TextView  tv_name;
	private static String[] names = { "����", "����", "����", "��Ӱ", "��Ƭ","�Զ���"};
	
	private List<String> pkgnames;
	private List<Drawable> iconList;
	private UserAnalysisDAO dao;
	private List<String> slidingNames;
	
	private static int[] icons = { R.drawable.bluetooth, R.drawable.download,
		R.drawable.music, R.drawable.movie, R.drawable.pictures,R.drawable.user_define };
	private Handler slidingHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			gv_content.setAdapter(sdAdapter);
		}
		
	};
	
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		gv_main = (GridView) this.findViewById(R.id.gv_main);
		
		// ����SlidinDrawer�ؼ��򿪵�ʱ��GridView���أ�SlidingDrawer�رյ�ʱ��GridView��
		slidingDrawer = (SlidingDrawer) this.findViewById(R.id.sd_main);
		gv_content  = (GridView) findViewById(R.id.content);
		slidingDrawer.setOnDrawerOpenListener(this);
		slidingDrawer.setOnDrawerCloseListener(this);
		sdAdapter = new SlidingDrawerAdapter();
		Intent service = new Intent(MainActivity.this,KingsGuardService.class);
		startService(service);
		// ʹ��APP ʹ��������ݿ� ������GridView
	//	gv_content.setAdapter(sdAdapter);
		initSdList();
		gv_content.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.v(TAG, "Click gv");
					//ES�ļ��������֧��View Directory�� ����setComponent���ø�intet��ES��ֵ�Ǵ��
					// String syspath = Environment.getExternalStorageDirectory().toString();
					  // ���µķ�ʽ�ǵ��� blackmoon�ļ������ ��ʱ��ʹ����
					    //jump directly to the following file/folder
						//File uriFile = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES);
						
						//Uri theFileUri = Uri.fromFile(uriFile);
						// Blackmoon file manager ֧�� browseFile   ��Ҫ��װ blackmoon �ļ������� 6.6���ϰ汾
					  //http://www.blackmoonit.com/android/filebrowser/intents/#intent.browse
					 
					 Intent theIntent = new Intent(Intent.ACTION_VIEW);
					 Uri theFileUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().toString() + "/downloads/bluetooth/"));
					    theIntent.setDataAndType(theFileUri,"vnd.android.cursor.item/file");
					    theIntent.putExtra(Intent.EXTRA_TITLE,"A Custom Title"); //optional
					    theIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					    //�������FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS,�µ�Activity���������������Activity���б��б��档
					    //theIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS); //optional
					//  startActivity(theIntent);
	
			  
			  if (position < names.length) {
				  Intent intent = new Intent("org.openintents.action.VIEW_DIRECTORY");
					switch (position) {
					case 0:
						 intent.setData(Uri.fromFile(new File(Environment.getExternalStorageDirectory().toString() + "/downloads/bluetooth/")));
						break;
					case 1:
						intent.setData(Uri.fromFile(new File(Environment.getExternalStorageDirectory().toString() + "/UCDownloads/")));
						break;
					case 2:
						intent.setData(Uri.fromFile(new File(Environment.getExternalStorageDirectory().toString() + "/Music/")));
						break;
					case 3:
						intent.setData(Uri.fromFile(new File(Environment.getExternalStorageDirectory().toString() + "/Movies/")));
						break;
					case 4:
						intent.setData(Uri.fromFile(new File(Environment.getExternalStorageDirectory().toString() + "/DCIM/100MEDIA/")));
						break;
					case 5:
						intent.setData(Uri.fromFile(new File(Environment.getExternalStorageDirectory().toString() + "/Pictures/Screenshots/")));
						break;

					default:
						break;
					}
					
					 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					 startActivity(intent);
				}else {
					//���ֳ����ǲ�������ֱ��������
					Toast.makeText(MainActivity.this, "launcher app", Toast.LENGTH_SHORT).show();
					Intent startIntent =  getPackageManager().getLaunchIntentForPackage(pkgnames.get(position - names.length));
					startActivity(startIntent);
					
				}
				
			}
			
			
		});
		adapter = new MainActivityAdapter(this);
		gv_main.setAdapter(adapter);
		//Activity ����� implements OnItemClickListener�Ϳ���֧�� this��Ϊlistener
		gv_main.setOnItemClickListener(this);
		gv_main.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View view,
					int position, long id) {
				switch (position) {
				case 0:
					
					break;
					default :
						Toast.makeText(MainActivity.this, "û�ø���ѡ��",Toast.LENGTH_LONG).show();
				}
				return false;
			}
		});
		
		
	}

	/***
	 * ��gridview����Ŀ�������ʱ�� ��Ӧ�Ļص� parent :��girdview 
	 * view : ��ǰ���������Ŀ Linearlayout
	 * position : �����Ŀ��Ӧ��λ�� 
	 * id : ������к�
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
		switch (position) {
		case 0:
			Intent losprointent = new Intent(MainActivity.this,LostSetActivity.class);
			startActivity(losprointent);
			
			break;

		case 1:
			Intent blackIntent = new Intent(MainActivity.this,BlackListActivity.class);
			startActivity(blackIntent);
			
			break;
		case 2:
			Intent appmIntent = new Intent(MainActivity.this,AppManagerActivity.class);
			startActivity(appmIntent);
			
			break;
		
		case 3:
			Intent pmIntent =  new Intent(MainActivity.this, ProManagerActivity.class);
			startActivity(pmIntent);
			
			break;
		case 4:// internet traffic
			Intent itIntent = new Intent(MainActivity.this,InternetTrafficActiviy.class);
			startActivity(itIntent);
			
			break;
		case 5:
			Intent virus  = new Intent(MainActivity.this,AntiVirusActivity.class);
			startActivity(virus);
			break;
		case 6:
			Intent clearIntent = new Intent(MainActivity.this,ClearSDActivity.class);
			startActivity(clearIntent);
			break;
		case 7:
			Intent stIntent = new Intent(this, SeniorToolsActivity.class);
			startActivity(stIntent);
			break;
		case 8:
			Intent scIntent = new Intent(this, SettingCenterActivity.class);
			startActivity(scIntent);
			
			break;
		default:
			break;
		}
		
	}
	
	public void initUserAnalysisList(List<String> pkgnamelist){
		iconList = new ArrayList<Drawable>();
		PackageManager pm = getPackageManager();
		for (String name : pkgnamelist) {
			try {
				PackageInfo  info = pm.getPackageInfo(name, 0);
				Drawable icon = info.applicationInfo.loadIcon(pm);
				iconList.add(icon);
				icon = null;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void initSdList(){
		
		dao = new UserAnalysisDAO(MainActivity.this);
		pkgnames = dao.getByFilter(10);
		initUserAnalysisList(pkgnames);
		slidingHandler.sendEmptyMessage(0);
	}
	
	public class SlidingDrawerAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return names.length + iconList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View view = View.inflate(MainActivity.this, R.layout.main_item, null);
			iv_icon =  (ImageView) view.findViewById(R.id.iv_main_icon);
			tv_name =  (TextView) view.findViewById(R.id.tv_main_name);

			if (position < names.length) {
				iv_icon.setImageResource(icons[position]);
				tv_name.setText(names[position]);
			}else {
				BitmapDrawable bitmap = (BitmapDrawable) iconList.get(position- names.length);
				iv_icon.setImageBitmap(bitmap.getBitmap());
				tv_name.setText(pkgnames.get(position - names.length));
			}

			return view;
		}
		
		
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onDrawerClosed() {

		gv_main.setVisibility(View.VISIBLE);
	}

	@Override
	public void onDrawerOpened() {
		gv_main.setVisibility(View.GONE);
	}



}
