package cn.phoniex.ssg;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.spec.PSource;

import cn.phoniex.ssg.domain.AntiVirusInfo;
import cn.phoniex.ssg.domain.Appinfos;
import cn.phoniex.ssg.util.MD5Encoder;

import com.ant.liao.GifView;
import com.ant.liao.GifView.GifImageType;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionInfo;
import android.content.pm.Signature;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class AntiVirusActivity extends Activity {

	private Button bt_scan;
	private GifView gifView;
	private ImageView imv_notscan;
	private ProgressBar pb;
	private ListView lv_virus;
	private SQLiteDatabase db;
	private ScrollView sv_virus;
	private LinearLayout ll_virus;
	private int  icurrpos = 0;
	private List<AVListInfo> avlist;
	private AntiVirusAdapter adapter;
	private TextView tv_desc;
	private TextView tv_cut;
	private ImageView imv_icon;
	private File  dbFile;
    private int[] arrIcons=new int[]{
            R.drawable.spcall, R.drawable.spsendsms, R.drawable.spcontacts, 
            R.drawable.spreadsms, R.drawable.splocation, R.drawable.spsdcard, 
            R.drawable.spinternet, R.drawable.spcamera
            };
    
	private boolean scanning = false;
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			String str = (String) msg.obj;
			TextView tView = new TextView(AntiVirusActivity.this);
			tView.setText(str);
			// java.lang.IllegalStateException: ScrollView can host only one direct child 不能直接使用Scroll控件来添加更多的控件
			ll_virus.addView(tView);
			sv_virus.scrollBy(0, 40);
			if (msg.what == -1) {
				gifView.setVisibility(View.GONE);
				imv_notscan.setVisibility(View.VISIBLE);
				lv_virus.setVisibility(View.VISIBLE);
				
				tView.setTextColor(Color.RED);
				tView.setTextSize(21);
			}else if (msg.what == 1) {
				gifView.setVisibility(View.GONE);
				imv_notscan.setVisibility(View.VISIBLE);
				ll_virus.removeAllViews();
				bt_scan.setText("扫描手机");
				adapter = new AntiVirusAdapter(avlist);
				lv_virus.setVisibility(View.VISIBLE);
				lv_virus.setAdapter(adapter);
				String strResult = (String) msg.obj;
				TextView tvResult = new TextView(AntiVirusActivity.this);
				tvResult.setText(strResult);
				tvResult.setTextColor(Color.GREEN);
				tvResult.setTextSize(21);
				ll_virus.addView(tvResult);
			}
			
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.anti_virus);
		dbFile = new File("antivirus.db");
		if (!dbFile.exists()) {
			copyfile();
		}
		String dbpath = getFilesDir().getAbsolutePath()+ File.separator;
		db = SQLiteDatabase.openDatabase(dbpath + "antivirus.db", null,
				SQLiteDatabase.OPEN_READONLY);
		bt_scan = (Button) findViewById(R.id.bt_virus);
		gifView = (GifView) findViewById(R.id.gifv_virus);
		gifView.setGifImage(R.drawable.virus_scan);
		imv_notscan = (ImageView) findViewById(R.id.imv_virus);
		pb = (ProgressBar) findViewById(R.id.pb_virus);
		lv_virus = (ListView) findViewById(R.id.lv_virus);
		sv_virus = (ScrollView) findViewById(R.id.sv_virus);
		ll_virus = (LinearLayout) findViewById(R.id.ll_virus);
		
		avlist = new ArrayList<AVListInfo>();
		for (int i = 0; i < 8; i++) {//添加用来显示权限的八个项
			AVListInfo avInfo = new AVListInfo();
			avInfo.type = i;
			avInfo.icut = 0;
			avInfo.appname = "";
			List<AntiVirusInfo> avinfos = new ArrayList<AntiVirusInfo>();
			avInfo.avinfos = avinfos;
			avlist.add(avInfo);
			avInfo = null;
			
		}
		
		lv_virus.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				int ih = view.getHeight();
				Toast.makeText(AntiVirusActivity.this, "高度:"+ih, 0).show();
				Log.e("view heigh", "高度:"+ih);
				
				Intent intent = new Intent(AntiVirusActivity.this,AntiVirusDetailActivity.class);
				intent.putExtra("position", position);
				ArrayList<AntiVirusInfo> antiVirusInfos = new ArrayList<AntiVirusInfo>();
				for (int i = 0; i < avlist.get(position).avinfos.size(); i++) {
					antiVirusInfos.add(avlist.get(position).avinfos.get(i));
				}
				intent.putExtra("key",antiVirusInfos);
				intent.putExtra("type", position);
				startActivity(intent);
			}
			
		});
		bt_scan.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//设置显示方式 先加载后显示 边加载边显示 只显示第一帧再显示
				imv_notscan.setVisibility(View.GONE);
				gifView.setVisibility(View.VISIBLE);
				//在非扫描状态才能新建线程扫描，之前由于忽略这点，导致点击取消扫描的时候又重新创建了一个线程，导致打印了两次扫描取消的状态到界面上
				//开始是怀疑线程没有被结束，尝试多种方式无果，后来尝试打印线程id发现两次输出的id不同，原来是两个线程。
				if (!scanning) {
					scanning = true;
					bt_scan.setText("取消扫描");
					new Thread() {
						public void run() {
							List<PackageInfo> infos = getPackageManager()
									.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES
													| PackageManager.GET_SIGNATURES |PackageManager.GET_PERMISSIONS);
							// 获取每一个应用程序的签名 获取到这个签名后 需要在数据库里面查询
							pb.setMax(infos.size());
							int total = 0;
							int virustotal = 0;
							if (icurrpos > 0) {
								total = icurrpos;
							}
							//记录 中断的节点 继续扫描
							for (int i = icurrpos; i < infos.size(); i++) 
							{
								PackageInfo info = infos.get(i);
								if (!scanning) {
									Message msg = Message.obtain();
									long lid = currentThread().getId();
									msg.obj = lid+"扫描取消 ,共扫描"+total+"项，发现" + virustotal + "个病毒";
									icurrpos = total;
									msg.what = -1;
									handler.sendMessage(msg);
									//currentThread().interrupt();
									return;
								}
								total++;
								try {
									sleep(20);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								Message msg = Message.obtain();
								msg.obj = "正在扫描" + info.packageName;
								handler.sendMessage(msg);
								Signature[] signs = info.signatures;
								if (info.requestedPermissions != null) {
									int ipos = -1;
									//info.permissions 不是申请的权限，而是该程序定义的权限，所以我们只能获取到Android一个程序具备下面的权限
									for (String pminfo : info.requestedPermissions) {
										//Log.e(info.packageName, pminfo);
										if (pminfo.equals(STR_CALL)) {
											avlist.get(0).icut ++;
											ipos = 0;
										}else if (pminfo.equals(STR_CONTACTS)) {
											avlist.get(2).icut ++;
											ipos = 2;
										}else if (pminfo.equals(STR_INTERNET)) {
											avlist.get(6).icut ++;
											ipos = 6;
										}else if (pminfo.equals(STR_CAMERA)) {
											avlist.get(7).icut ++;
											ipos = 7;
										}else if (pminfo.equals(STR_LOCATION)) {
											avlist.get(4).icut ++;
											ipos = 4;
										}else if (pminfo.equals(STR_READSMS)) {
											avlist.get(3).icut ++;
											ipos = 3;
										}else if (pminfo.equals(STR_SDCARD)) {
											avlist.get(5).icut ++;
											ipos = 5;
										}else if (pminfo.equals(STR_SENDSMS)) {
											avlist.get(1).icut ++;
											ipos = 1;
										}
										//如果存在该权限 添加到对应的列表中
										if (ipos != -1) {
										    AntiVirusInfo tmpinfo =  getAntiVirusInfo(info.packageName);
										    avlist.get(ipos).avinfos.add(tmpinfo);
										    ipos = -1;//用完恢复状态
										}
									}
									
								}
								if (signs == null) {
									continue;
								}
								String str = signs[0].toCharsString();
								String md5 = MD5Encoder.encode(str);
								Cursor cursor = db.rawQuery(
										"select desc,type from datable where md5=?",
										new String[] { md5 });
								if (cursor.moveToFirst()) {
									String desc = cursor.getString(0);
									int type = cursor.getInt(1);
									AVListInfo tmpavlist = new AVListInfo();
									
									List<AntiVirusInfo> antiVirusInfos = new ArrayList<AntiVirusInfo>();
									AntiVirusInfo tmpavinfo =  getAntiVirusInfo(info.packageName);
									antiVirusInfos.add(tmpavinfo);
									
									tmpavlist.appname =tmpavinfo.appName;
									tmpavlist.type = type;
									tmpavlist.avinfos = antiVirusInfos;
									avlist.add(tmpavlist);
									
									msg = Message.obtain();
									msg.obj = info.packageName + ": " + desc;
									
									handler.sendMessage(msg);
									virustotal++;
								}
								cursor.close();
								pb.setProgress(total);
								if (info.packageName.equals("com.example.jni")) {
									Log.e("AntiVirus", md5);
									msg = Message.obtain();
									msg.obj = info.packageName + md5;
									handler.sendMessage(msg);
								}
							}
							
							if (scanning) {
								Message msg = Message.obtain();
								msg.obj = currentThread().getId()+"扫描完毕 ,共扫描"+total+"项，发现"+ virustotal + "个病毒";
								msg.what = 1;
								handler.sendMessage(msg);
								pb.setProgress(pb.getMax());
							}
						}

					}.start();
				}else {
					scanning = false;
					bt_scan.setText("扫描手机");
				}
				
				gifView.setEnabled(true);
				gifView.setGifImageType(GifImageType.SYNC_DECODER);
				
			}
		});

	}
	
	public  void copyfile() {
		
		AssetManager  am = getAssets();
		InputStream is;

		try {
			is = am.open("antivirus.db");
			OutputStream ops = openFileOutput("antivirus.db", MODE_PRIVATE);
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

	public  AntiVirusInfo getAntiVirusInfo(String packageName) {
		
		AntiVirusInfo temp = new AntiVirusInfo();
		PackageInfo packageInfo;
		try {
			packageInfo = getPackageManager().getPackageInfo(packageName, 0);
			ApplicationInfo applicationInfo =  packageInfo.applicationInfo;
			temp.appName = applicationInfo.loadLabel(getPackageManager()).toString();
			temp.pkgName = packageInfo.packageName;
			temp.type = -1;
		//	temp.icon = applicationInfo.loadIcon(getPackageManager());
			return temp;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		
	};
	public int SP_CALL  = 0;
	public int SP_SENDSMS  = 1;
	public int SP_CONTACTS  = 2;
	public int SP_READSMS  = 3;
	public int SP_LOCATION  = 4;
	public int SP_SDCARD  = 5;
	public int SP_INTERNET  = 6;
	public int SP_CAMERA = 7;
	
	
	public String STR_CALL  = "android.permission.CALL_PHONE";
	public String STR_SENDSMS  = "android.permission.SEND_SMS";
	public String STR_CONTACTS  = "android.permission.READ_CONTACTS";
	public String STR_READSMS  = "android.permission.READ_SMS";
	public String STR_LOCATION  = "android.permission.ACCESS_FINE_LOCATION";
	public String STR_SDCARD  = "android.permission.WRITE_EXTERNAL_STORAGE";
	public String STR_INTERNET  = "android.permission.INTERNET";
	public String STR_CAMERA = "android.permission.CAMERA";
	
	public class AVListInfo{
		public String appname;
		public  int type;
		public int icut;
		List<AntiVirusInfo> avinfos;
	}
	
	
	public class AntiVirusAdapter extends BaseAdapter{

		private List<AVListInfo> avlist;
		
		public AntiVirusAdapter(List<AVListInfo> avlist) {
			this.avlist = avlist;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return avlist.size();
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
			View view = View.inflate(AntiVirusActivity.this, R.layout.anti_virus_item, null);
			tv_cut = (TextView) view.findViewById(R.id.tv_avitem_count);
			tv_desc = (TextView) view.findViewById(R.id.tv_avitem_desc);
			imv_icon = (ImageView) view.findViewById(R.id.imv_avitem_icon);
			String cutString = "";
			String descString = "";
			if (position < 8) {
				
				switch (position) {
				case 0:
					descString = getString(R.string.sp_call);
					break;
				case 1:
					descString = getString(R.string.sp_sendsms);
					break;
				case 2:
					descString = getString(R.string.sp_phone);
					break;
				case 3:
					descString = getString(R.string.sp_readsms);
					break;
				case 4:
					descString = getString(R.string.sp_location);
					break;
				case 5:
					descString = getString(R.string.sp_sdcard);
					break;
				case 6:
					descString = getString(R.string.sp_internet);
					break;
				case 7:
					descString = getString(R.string.sp_camera);
					break;
				default:
					break;
				}
				tv_cut.setText("共有"+avlist.get(position).icut+"申请该权限");
				tv_desc.setText(descString);
				imv_icon.setBackgroundResource(arrIcons[position]);
			}else {
				tv_desc.setText(avlist.get(position).appname);
				imv_icon.setBackgroundResource(R.drawable.anti_virus_found);
				switch (avlist.get(position).type) {
				case 4:
					tv_cut.setText("在未明示情况下窃取用户隐私");
					break;
				case 6:
					tv_cut.setText("恶意后台扣费,病毒木马程序");
					break;
				case 11:
					tv_cut.setText("测试程序");
					break;
				default:
					break;
				}
			}
			//int ih = view.getHeight();//此时获取到的高度为0 高度还未度量
			return view;
		}
	}
}
