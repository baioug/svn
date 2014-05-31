package cn.phoniex.ssg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.spec.PSource;

import cn.phoniex.ssg.util.GetStrValue;

import android.R.drawable;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ClearSDActivity extends Activity {


	private ClearSDAdapter  adapter;
	private ListView  lv;
	private List<ClearInfo> infos;
	private LinearLayout ll_loading;
	private TextView tv_title;
	private TextView tv_size;
	private TextView tv_name;
	private  CheckBox cb_item;
	private ImageView imv_clear;
	private SQLiteDatabase db;
	private File dbFile;
	private long dirsize = 0;
	private LinearLayout ll_all;
	private CheckBox  cb_all;
	private Button bt_clearsd;
	private long lchsize = 0;
	private  int icut = 0;
	private Handler  handler =  new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 0) {
				ll_loading.setVisibility(View.GONE);
				adapter = new ClearSDAdapter(infos);
				lv.setAdapter(adapter);
			}else if (msg.what == 1) {
				String  title = "共"+infos.size()+"项,"+"已选择"+icut+"项"+"共"+GetStrValue.getValueOf(lchsize);
				icut = 0;
				lchsize = 0;
				tv_title.setTextColor(Color.GREEN);
				tv_title.setText(title);
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.clearsd);
		tv_title = (TextView) findViewById(R.id.tv_clear_title);
		ll_loading  = (LinearLayout) findViewById(R.id.ll_clear_loading);
		lv = (ListView) findViewById(R.id.lv_clear);
		ll_all = (LinearLayout) findViewById(R.id.ll_clear_all);
		cb_all = (CheckBox) findViewById(R.id.cb_clear_all);
		bt_clearsd = (Button) findViewById(R.id.bt_clearsd);
		
		bt_clearsd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				new Thread(){

					@Override
					public void run() {
						List<Boolean> poslist = new ArrayList<Boolean>();
						for ( ClearInfo info : infos) {
							
							if (info.bchecked) {
								poslist.add(true);
								File  file = new File(info.path);
								deleteDir(file);
							}else {
								poslist.add(false);
							}
							//你不能在对一个List进行遍历的时候将其中的元素删除掉
							//infos.remove(info);
						}
						int i = 0;
						for (Boolean bchek : poslist) {
							if (bchek) {
								infos.remove(i);
							}
							i++;
						}
						handler.sendEmptyMessage(0);
						super.run();
					}
					
				}.start();
				
			}
		});
		
		ll_all.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (cb_all.isChecked()) {
					cb_all.setChecked(false);
				}else {
					cb_all.setChecked(true);
				}
			}
		});
		cb_all.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					for ( ClearInfo info : infos) {
						info.bchecked = true;
					}
				}else {
					for ( ClearInfo info : infos) {
						info.bchecked = false;
					}
				}
				reflashTitle();
				handler.sendEmptyMessage(0);
			}
		});
		
		
		ll_loading.setVisibility(View.VISIBLE);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				CheckBox cb_curr = (CheckBox) view.findViewById(R.id.cb_clear_item);
				cb_curr.setChecked(!cb_curr.isChecked());
				infos.get(position).bchecked = !infos.get(position).bchecked;
				
				new Thread(){

					@Override
					public void run() {
						reflashTitle();
						super.run();
					}

				}.start();
			}
			
		});
		
		dbFile = new File("clearsd.db");
		if (!dbFile.exists()) {
			copyfile();
		}
		
		new Thread(){

			@Override
			public void run() {
				
				initClearInfo();
				handler.sendEmptyMessage(0);
				super.run();
			}
			
		}.start();
		
		
	}

	public void reflashTitle() {
		for ( ClearInfo info : infos) {
			if (info.bchecked) {
				lchsize = lchsize + info.cacheSize;
				icut++;
			}
		}
		handler.sendEmptyMessage(1);
	}
	
	public  void copyfile() {
		
		InputStream  is = getClass().getClassLoader().getResourceAsStream("clearsd.db");
		try {
			OutputStream ops = openFileOutput("clearsd.db", MODE_PRIVATE);
			byte[] buf = new byte[1024];
			int len = 0;
			while ((len = is.read(buf)) != -1) {
				ops.write(buf, 0, len);
			}
			ops.flush();
			ops.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	protected void initClearInfo() {
		infos = new ArrayList<ClearInfo>();
		// 4.2 之后 使用如下的方式获取 绝对路径来打开
		String dbpath = getFilesDir().getAbsolutePath()+ File.separator;// 我们写到了 file目录下 所以不需要.replace("files", "databases") 
		db = SQLiteDatabase.openDatabase(dbpath+"clearsd.db", null, SQLiteDatabase.OPEN_READONLY);
		List<PackageInfo> packageInfos = getPackageManager().getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		
		for (PackageInfo packageInfo : packageInfos) {
			ClearInfo info = new ClearInfo();
			Cursor cursor = db.rawQuery("select filepath from softdetail where apkname = ?", new String[]{packageInfo.packageName});
			if(cursor.moveToNext()) {
				String path= cursor.getString(0);
				//添加sd根路径
				info.path = Environment.getExternalStorageDirectory().toString() + path;
				File dirFile = new File(Environment.getExternalStorageDirectory()+path);
				info.cacheSize = getDirSize(dirFile);
				dirsize = 0;
				info.bchecked = false;
				info.pkgName = packageInfo.packageName;
				Drawable icon = packageInfo.applicationInfo.loadIcon(getPackageManager());
				info.icon = icon;
				String appname = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
				info.appName = appname;
				infos.add(info);
			}
		}
	}
	
	public void deleteDir(File dirFile){
		
		if (dirFile.isDirectory()) {
			File[] files = dirFile.listFiles();
			// 空文件直接删除
			if (dirFile.listFiles().length == 0) {
				dirFile.delete();
			}else {// 递归调用
				for (File file : files) {
					deleteDir(file);
				}
			}
			
		}else {// 非文件夹 直接删除
			dirFile.delete();
		}
		
	}
	

	private long getDirSize(File dirFile) {
		File[] list = dirFile.listFiles();
		if (list != null) {
			for (File file : list) {
				if (file.isDirectory()) {
					getDirSize(file);
				}else {
					dirsize = dirsize + file.length();
				}
			}
		}
		
		return dirsize;
	}

	public class ClearInfo{
		public Drawable icon;
		String appName;
		String pkgName;
		long cacheSize;
		String path;
		boolean bchecked;
	}
	public class ClearSDAdapter extends BaseAdapter{

		private  List<ClearInfo> clearInfolist;
		
		public ClearSDAdapter(List<ClearInfo> clearInfolist) {
			super();
			this.clearInfolist = clearInfolist;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return clearInfolist.size();
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
			
			View view = View.inflate(ClearSDActivity.this, R.layout.clearsd_item, null);
			tv_name = (TextView) view.findViewById(R.id.tv_clear_appname);
			tv_size = (TextView) view.findViewById(R.id.tv_clear_size);
			cb_item = (CheckBox) view.findViewById(R.id.cb_clear_item);
			imv_clear = (ImageView) view.findViewById(R.id.imv_clear_icon);
			tv_name.setText(clearInfolist.get(position).appName);
			tv_size.setText(GetStrValue.getValueOf(clearInfolist.get(position).cacheSize));
			cb_item.setChecked(clearInfolist.get(position).bchecked);
			imv_clear.setImageDrawable(clearInfolist.get(position).icon);
			
			return view;
		}
		
	}
	
	
}
