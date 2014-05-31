package cn.phoniex.ssg.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.entity.SerializableEntity;

import cn.phoniex.ssg.R;
import cn.phoniex.ssg.dao.AppLockDao;
import cn.phoniex.ssg.domain.Appinfos;
import cn.phoniex.ssg.engine.AppInfosProvider;
import cn.phoniex.ssg.service.CityGuardService;
import cn.phoniex.ssg.service.CityGuardService.CgBinder;
import android.R.integer;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ApplockFragment extends Fragment {

	 private static final String TAG = "BaseFragment";
	    private int iarg = 0;//ȷ����ǰFragment�����ض�Ӧ��View
	    private ListView lv = null;
	    private TextView tv_start;
	    private TextView tv_strang;
	    private TextView tv_mode;
	    private CheckBox cb_start;
		protected SharedPreferences sp;	
		private List<Appinfos> appinfos;
		private List<Appinfos> reOrganList; //organization �Ż���֮֯����б�
//		private AppInfoProvider provider;
		private AppLockAdapter adapter;
		private AppLockDao dao;
		private LinearLayout ll_loading;
		private List<String> lockappinfos ;
		private CgBinder cgbinder;
		private String[] items =new  String[]{"��������","ʱ������","�˳�����","�û��޸�"};
		private int ipos = 0;//�û�ѡ�������ģʽ ������ʾ toast��ʾ
		private ServiceConnection conn;
		private Handler handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (lv != null) {
					//�������֮���ٽ��и�ֵ
					adapter = new AppLockAdapter(appinfos);
					lv.setAdapter(adapter);
				}
				ll_loading.setVisibility(View.INVISIBLE);
			}
			
		};

		// �Ż�app�б��ó����ĳ��õ���ǰ��
		//  ����Ϊ���õ��Ż���֯��ģʽ
		public void reOrganizationList(int imod)
		{
			//
		//	int  imod = sp.getInt("listapplockmode", 1);
			reOrganList = new ArrayList<Appinfos>();
			List<Appinfos>  tmplist = new ArrayList<Appinfos>();
			switch (imod) {
			case 0://Ĭ��ģʽ ��ϵͳӦ�ó�������
				
				for (int i = 0; i < appinfos.size(); i++) {
					if (appinfos.get(i).isSysApp()) {
						tmplist.add(appinfos.get(i));
					}else {
						reOrganList.add(appinfos.get(i));
					}
				}
				reOrganList.addAll(tmplist);
				
				break;
			case 1://


				
				break;
			case 2://������ʾ�û�������App
				
				for (int i = 0; i < appinfos.size(); i++) {
					if (lockappinfos.contains(appinfos.get(i).getPackageName())) {
						reOrganList.add(appinfos.get(i));
					}else {
						tmplist.add(appinfos.get(i));
					}
				}
				reOrganList.addAll(tmplist);
				
				break;

			default:
				break;
			}

			
		}


	    public ApplockFragment newInstance(Context context,int pos) {
	        ApplockFragment newFragment = new ApplockFragment();
			this.iarg = pos;
	        Bundle bundle = new Bundle();
	        //�����ﱣ��һЩ������ֵ
	        //�´ο�����onCreate���ȡ��
	       // bundle.putString("hello", s);
	        bundle.putInt("iarg", pos);
	        newFragment.setArguments(bundle);
	        return newFragment;

	    }
	    public ListView getlv()
	    {
	    	ListView listView = this.lv;
	    	return listView;
	    	
	    }

	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        Log.d(TAG, "BaseFragment-----onCreate");
	        Bundle args = getArguments();
	        iarg = args.getInt("iarg");
	    }

	    @SuppressLint("NewApi")
		@Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
	        Log.d(TAG, "BaseFragment-----onCreateView");
	        View view;
	        Log.d(TAG, ""+iarg);
			if(iarg == 0){
			     view = inflater.inflate(R.layout.app_lock, container, false);
			     if (view != null) {
						//DisplayMetrics outMetrics =  new DisplayMetrics();
						//view.getDisplay().getMetrics(outMetrics);
						lv = (ListView) view.findViewById(R.id.lv_lockapp);
					     ll_loading = (LinearLayout) view.findViewById(R.id.ll_la_loading);
						if (lv == null) {
							Log.d(TAG, "can not find listview ");}
						else {Log.d(TAG, "find it ");}
						//Ϊʲôcontext ����� null
						dao = new AppLockDao(getActivity().getApplicationContext());
						lockappinfos = dao.getAllLockedApps();
					     if (lv == null) {
					    	 Log.d(TAG, "can not find listview");
						}else {
							Intent intent =  new Intent();
							intent.setClass(getActivity(), CityGuardService.class);
							initAppinfos();

							lv.setOnItemClickListener(new OnItemClickListener() {

								public void onItemClick(AdapterView<?> parent, View view,
										int position, long id) {
									TranslateAnimation transAnim = new TranslateAnimation(
											Animation.RELATIVE_TO_SELF, 0.0f,
											Animation.RELATIVE_TO_SELF, 0.5f,
											Animation.RELATIVE_TO_SELF, 0.0f,
											Animation.RELATIVE_TO_SELF, 0.0f);
									transAnim.setDuration(500);
									view.startAnimation(transAnim);
									ImageView iv = (ImageView) view.findViewById(R.id.imv_la_status);
									
									// ���ݵ�ǰҪ��������İ���
									Appinfos info = (Appinfos) lv.getItemAtPosition(position);
									String pkgname = info.getPackageName();
									if(dao.find(pkgname)){
										// �Ƴ������Ŀ
										dao.delete(pkgname);
										//getContentResolver().delete(Uri.parse("content://cn.itcast.applockprovider/delete"), null, new String[]{packname});
										lockappinfos.remove(pkgname);
										
										if (cgbinder == null) {
											Toast.makeText(getActivity(), "����δ����", 0).show();
										}else {
											cgbinder.tmpStopProItem(pkgname);
										}
										iv.setImageResource(R.drawable.unlock);
										
									}else{
										dao.add(pkgname);
										lockappinfos.add(pkgname);
										//ContentValues values = new ContentValues();
										//values.put("packname", packname);
									//	getContentResolver().insert(Uri.parse("content://cn.itcast.applockprovider/insert"), values);
										if (cgbinder == null) {
											Toast.makeText(getActivity(), "����δ����", 0).show();
										}else {
											cgbinder.StartProItem(pkgname);
										}
										iv.setImageResource(R.drawable.lock);
										
									}
							
								}
							});
								
						}
			     }
						
			}else{
				
				sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
				 view = inflater.inflate(R.layout.app_lock_set, container, false);
				 tv_mode = (TextView) view.findViewById(R.id.tv_la_mode);
				 tv_start = (TextView) view.findViewById(R.id.tv_la_server);
				 tv_strang  = (TextView) view.findViewById(R.id.tv_la_stronge);
				 cb_start = (CheckBox) view.findViewById(R.id.cb_la_server);
				 if (sp.getBoolean("apploctstart", false)) {
					cb_start.setChecked(true);
					tv_start.setTextColor(Color.GREEN);
					tv_start.setText("�ѿ��������������");
				}
				 cb_start.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						Intent intent = new Intent(getActivity().getApplicationContext(),CityGuardService.class);
						conn = new myServiceConnection();
						if (isChecked) {
							Log.d(TAG, "Start Service");
							Editor editor = sp.edit();
							editor.putBoolean("apploctstart", true);
							editor.commit();
							getActivity().startService(intent);
							getActivity().bindService(intent, conn, Context.BIND_AUTO_CREATE);
							tv_start.setTextColor(Color.GREEN);
							tv_start.setText("�ѿ��������������");
						}
						else{
							Editor editor = sp.edit();
							editor.putBoolean("apploctstart", false);
							editor.commit();
							getActivity().stopService(intent);
							tv_start.setTextColor(Color.RED);
							tv_start.setText("δ���������������");
						}
					}
				});
				 tv_mode.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						//new Builder();��ʱ����ʹ�� getActivity().getApplicationContext()
						//ֻ��Ҫʹ�� getActivity�����ˣ����ʹ�����������������������Dialog�����
						// ��ʾview��ʱ�������ʹ�õ�ǰ��Activity�������ķ���֪���������ĸ�view�ϰ�
						AlertDialog.Builder builder = new Builder(getActivity());
						int lastpos = sp.getInt("applockmode", 0);
						//���õڶ�������  ��ʾ֮ǰ�������ʾģʽ����
						builder.setSingleChoiceItems(items, lastpos, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								
								Editor editor = sp.edit();
								editor.putInt("applockmode", which);
								editor.commit();
							}
						});
						builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								String choString  = items[ipos];
								Toast.makeText(getActivity(), "��������Ϊ: "+choString, 0).show();
							}
						});
						
						
						builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Toast.makeText(getActivity(), "ȡ���޸�", 0).show();
							}
						});
						builder.create().show();
						
					}
				});
				 // ͨ������CheckBox��״̬֪ͨCheckBox ״̬�ı��� 
				 //CheckBox�����״̬�ı� ������״̬��������
				 tv_start.setOnClickListener(new OnClickListener() {
						
					@Override
					public void onClick(View v) {
						Editor editor  = sp.edit();

						if (cb_start.isChecked()) {
							cb_start.setChecked(false);
							editor.putBoolean("apploctstart", false);
							editor.commit();
						
							
						}else {
							cb_start.setChecked(true);
							editor.putBoolean("apploctstart", true);
							editor.commit();
							
						}
						
					}
				});
				 tv_strang.setOnClickListener(new OnClickListener() {
						
					@Override
					public void onClick(View v) {
						
					}
				});
				 
			}
			
	        return view;

	    }

		private void initAppinfos() {
		ll_loading.setVisibility(View.VISIBLE);
		new Thread() {

			@Override
			public void run() {
				appinfos = new AppInfosProvider(getActivity().getApplicationContext()).getAllApps();
				handler.sendEmptyMessage(0);
			}

		}.start();

	}


private class AppLockAdapter extends BaseAdapter {

			private List<Appinfos> appinfos;
			
			public AppLockAdapter(List<Appinfos> listAppinfos) {
				this.appinfos = listAppinfos;
			}

			public int getCount() {

				return appinfos.size();
			}

			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return appinfos.get(position);
			}

			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = null;
				if (convertView == null) {
					view = View.inflate(getActivity().getApplicationContext(),
							R.layout.app_lock_item, null);
				} else {
					view = convertView;
				}
				//����view�����״̬
				Appinfos info = appinfos.get(position);
				ImageView iv = (ImageView) view.findViewById(R.id.imv_la_icon);
				TextView tv_appname = (TextView) view.findViewById(R.id.tv_la_appname);
				ImageView iv_lock_status = (ImageView) view.findViewById(R.id.imv_la_status);
				TextView tv_pkg_name =  (TextView) view.findViewById(R.id.tv_la_packname);
				tv_pkg_name.setText(info.getPackageName());
				//ʹ���б����洢�����İ��� ������ȥ���ݿ��в�ѯ ���Ч��
				if(lockappinfos.contains(info.getPackageName())){
					iv_lock_status.setImageResource(R.drawable.lock);
				}else{
					iv_lock_status.setImageResource(R.drawable.unlock);
				}
				iv.setImageDrawable(info.getIcon());
				tv_appname.setText(info.getAppname());
				return view;
			}
		}
	    // java ��֧�� ���ؼ̳� ���ǿ��Զ��ؼ̳нӿ� implements ��ʵ�ֽӿ�����ķ���
        // extends һ�㶼�Ǽ̳и��� �� ��չ����
	    public class myServiceConnection implements ServiceConnection
	    {

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				cgbinder = (CgBinder)service;
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				
			}
	    	
	    }

	    @Override
	    public void onDestroy() {
	        super.onDestroy();
	        Log.d(TAG, "BaseFragment-----onDestroy");
	    }
}
