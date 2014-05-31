package cn.phoniex.ssg.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.entity.SerializableEntity;

import cn.phoniex.ssg.R;
import cn.phoniex.ssg.dao.AppLockDao;
import cn.phoniex.ssg.dao.ProManagerDAO;
import cn.phoniex.ssg.domain.Appinfos;
import cn.phoniex.ssg.domain.ProcessInfo;
import cn.phoniex.ssg.engine.AppInfosProvider;
import cn.phoniex.ssg.service.CityGuardService;
import cn.phoniex.ssg.service.ProManagerService;
import cn.phoniex.ssg.service.CityGuardService.CgBinder;
import android.R.integer;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.os.Debug.MemoryInfo;
import android.app.AlertDialog;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ProManagerFragment extends Fragment {

	 private static final String TAG = "BaseFragment";
	    private int iarg = 0;//ȷ����ǰFragment�����ض�Ӧ��View
	    private ListView lv_pml = null;
	    private ListView lv_pms = null;
	    private TextView tv_start;
	    private TextView tv_offscreen;
	    private TextView tv_time;
	    private TextView tv_curr;
	    private TextView tv_title;
	    private TextView tv_showsys;
	    private CheckBox cb_start;
	    private CheckBox cb_offscreen;
	    private CheckBox cb_pml_chooseall;
	    private CheckBox cb_showsys;
	    private LinearLayout ll_chooseall;
		private LinearLayout ll_pml_loading;
		private LinearLayout ll_pms_loading;
		private Button bt_clear;
		
		private Timer timer;
		private TimerTask timerTask;
		
	    private int isyscut = 0;
	    private int iusercut = 0;
		protected SharedPreferences sp;	
		private List<ProcessInfo> proinfos;//�����û���Ϣ
		private List<ProcessInfo> userprolist;
		private List<ProcessInfo> sysprolist;
		private List<Appinfos>  appinfos;
		private List<Appinfos> sysapplist;
		private List<Appinfos> userapplist;
		private List<RunningAppProcessInfo> runningappinfos;
		private ProManagerListAdapter pml_adapter;
		private ProManagerSetAdapter  pms_adapter;

		private List<String> userchoList;
		private List<String> pmlist;
		private PackageManager pm;
		private ActivityManager am;
		private ProManagerDAO dao;
		private boolean[] bitems;
		private int ipos = 0;//�û�ѡ�������ģʽ ������ʾ toast��ʾ
		private long  usingMem;
		private Handler handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.what == 1) {
					upDateTitle();
				}
				if (lv_pml != null) {
					//�������֮���ٽ��и�ֵ
					//���ò����� ���ɵ��
					//lv_pml.setEnabled(false);
					pml_adapter = new ProManagerListAdapter(proinfos);
					lv_pml.setAdapter(pml_adapter);
				}
				ll_pml_loading.setVisibility(View.INVISIBLE);
			}
			
		};
		private Handler pms_handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (lv_pms != null) {
					//�������֮���ٽ��и�ֵ
					pms_adapter = new ProManagerSetAdapter(appinfos);
					lv_pms.setAdapter(pms_adapter);
				}
				ll_pms_loading.setVisibility(View.INVISIBLE);
			}
			
		};


	    public ProManagerFragment newInstance(Context context,int pos) {
	        ProManagerFragment newFragment = new ProManagerFragment();
			this.iarg = pos;
	        Bundle bundle = new Bundle();
	        //�����ﱣ��һЩ������ֵ
	        //�´ο�����onCreate���ȡ��
	        bundle.putInt("iarg", pos);
	        newFragment.setArguments(bundle);
	        return newFragment;

	    }

	    
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        Log.d(TAG, "BaseFragment-----onCreate");
	        Bundle args = getArguments();
	        iarg = args.getInt("iarg");
	    }

	    public void upDateTitle()
	    {
	    	runningappinfos = am.getRunningAppProcesses();
	    	int icut = runningappinfos.size();
	    	String  str = "������";
	    	String mem = "  ʣ��/����";
	    	android.app.ActivityManager.MemoryInfo  outInfo = new android.app.ActivityManager.MemoryInfo();
	    	am.getMemoryInfo(outInfo);
	    	long memsize = outInfo.availMem;
	    	tv_curr.setText(str+String.valueOf(icut)+mem+"("+GetStrValue.getValueOf(memsize)+"/"+GetStrValue.getStrByArg(usingMem, 1)+")");
	    	
	    }
	    // ÿ��10�� ˢ����ʾ
		public void onStartTimer() {
			timer = new Timer();
			timerTask = new TimerTask() {
				
				@Override
				public void run() {
					//�����������̣߳�������ֱ�ӷ���Ϣ�����ֱ�������﷢��Ϣ����handler�������ˢ�£���Ϊ�б���ܻ���
					//����ʼ�������Ǿ�ֱ�ӽ��з���������listvie������±���
					updateRunningAppinfos();
				}
			};
			timer.schedule(timerTask, 10000, 10000);
		}
	    
	    @SuppressLint("NewApi")
		@Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
	        Log.d(TAG, "BaseFragment-----onCreateView");
	        View view;
	        Log.d(TAG, ""+iarg);
	        sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
			if(iarg == 0){
				
			    String  str = "��ǰ���еĽ�����:";
				// ��ȡ��ǰ�������еĽ���
				 pm = getActivity().getPackageManager();
				 am = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
				 runningappinfos = new ArrayList<RunningAppProcessInfo>();
				runningappinfos = am.getRunningAppProcesses();
				int icut = runningappinfos.size();
				pmlist = new ArrayList<String>();
				// ��ʼ�� ��ǰ�������еĽ��̵İ������б�
				for (int i = 0; i < icut; i++) {
					pmlist.add(runningappinfos.get(i).processName);
				}
			    view  = inflater.inflate(R.layout.pro_manager_list, container, false);
			  //  tv_title = (TextView) view.findViewById(R.id.tv_pmm_title);
			    tv_curr = (TextView) view.findViewById(R.id.tv_pml_current);
			    lv_pml = (ListView) view.findViewById(R.id.lv_pml);
			    ll_pml_loading = (LinearLayout) view.findViewById(R.id.ll_pml_loading);
			    bt_clear  = (Button) view.findViewById(R.id.bt_pml_clear);
			    cb_pml_chooseall = (CheckBox) view.findViewById(R.id.cb_pml_chooseall);
			    ll_chooseall = (LinearLayout) view.findViewById(R.id.ll_pml_cho);
			    tv_curr.setText(str+String.valueOf(icut));
			    // ������ʱ�� ��ʱˢ���������
			    onStartTimer();
				proinfos = new ArrayList<ProcessInfo>();
				initRunningAppinfos();
				//��̨�ڲ���ˢ��,�����ǵ�icut��ֵ�Գ�ʼ��֮���û�����޸���ֵ����Ϊ���ǵĽ��������ܻ����ӵ��·���Խ��
			    bitems = new boolean[256];
			    // Ĭ��Ϊȫѡ״̬
			    for (int i = 0; i < bitems.length; i++) {
					bitems[i] = true;
				}
			    ll_chooseall.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {

						if (cb_pml_chooseall.isChecked()) {
							cb_pml_chooseall.setChecked(false);
						}else {
							cb_pml_chooseall.setChecked(true);
						}
					}
				});
			    
			    cb_pml_chooseall.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						View view;
						CheckBox cb_list;
						//lv_pml.notify();
						ll_pml_loading.setVisibility(View.VISIBLE);
						boolean bshowsys = sp.getBoolean("showsyspro", false);

						if (isChecked) {
							List<ProcessInfo> tmpproInfos = userprolist;
							if (bshowsys) {
								tmpproInfos.addAll(sysprolist);
							}
							int i = 0;
							pmlist.clear();
							for (ProcessInfo processInfo : tmpproInfos) {
								pmlist.add(processInfo.getPkgname());
								bitems[i] = true;
								i++;
							}
							Toast.makeText(getActivity(), ""+pmlist.size(), 0).show();
						}else {
							//���ȡ��ȫѡ ��������б�
								pmlist.clear();
								Toast.makeText(getActivity(), ""+pmlist.size(), 0).show();
						    for (int i = 0; i < bitems.length; i++) {
								bitems[i] = false;
							}
						}
						handler.sendEmptyMessage(0);
					}
				});
			    

				lv_pml.setOnItemClickListener(new OnItemClickListener() {

					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						TranslateAnimation transAnim = new TranslateAnimation(
								Animation.RELATIVE_TO_SELF, 0.0f,
								Animation.RELATIVE_TO_SELF, 0.5f,
								Animation.RELATIVE_TO_SELF, 0.0f,
								Animation.RELATIVE_TO_SELF, 0.0f);
						transAnim.setDuration(500);
						view.startAnimation(transAnim);
						CheckBox cb_pml = (CheckBox) view.findViewById(R.id.cb_pm_item);
						//������CheckBox ���������� Item����Ӧ�¼��д��� CheckBox����ʾ
						cb_pml.setChecked(!cb_pml.isChecked());
						// ������� �û�����б���
						iusercut = userprolist.size();
						isyscut = sysprolist.size();
						if (cb_pml.isChecked()) {
							if (position == 0) {
								
							}else if (position <= iusercut) {
								pmlist.add(userprolist.get(position - 1).getPkgname());
							}else if (position == iusercut + 1) {
								
							}else if (position <= iusercut + isyscut + 2){
								pmlist.add(sysprolist.get(position - iusercut -2).getPkgname());
							}
						}else {
							if (position == 0) {
								
							}else if (position <= iusercut) {
								pmlist.remove(userprolist.get(position - 1).getPkgname());
							}else if (position == iusercut + 1) {
								
							}else if (position <= iusercut + isyscut + 2){
								pmlist.remove(sysprolist.get(position - iusercut -2).getPkgname());
							}
							
						}
						
//						if (bitems[position]) {
//							bitems[position] = false;
//						}else {
//							bitems[position]= true;
//						}
						
					}
				});
				
				bt_clear.setOnClickListener( new OnClickListener() {
					
					@Override
					public void onClick(View v) {

						if (pmlist != null) {
							if (pmlist.size() == 0) {
								Toast.makeText(getActivity(), "δѡ���κ���", 0).show();
							}else {
								//����ѡ�еĽ���
								KillAllBGPros(pmlist);
							}
						}

					}
				});
			    
						
			}else{
				
				sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
				 view = inflater.inflate(R.layout.pro_manager_set, container, false);
				 tv_time = (TextView) view.findViewById(R.id.tv_pms_time);
				 tv_start = (TextView) view.findViewById(R.id.tv_pms_auto);
				 tv_offscreen  = (TextView) view.findViewById(R.id.tv_pms_offscreen);
				 cb_offscreen  = (CheckBox)view.findViewById(R.id.cb_pms_offscreen);
				 cb_start = (CheckBox) view.findViewById(R.id.cb_pms_auto);
				 tv_curr  = (TextView) view.findViewById(R.id.tv_pms_tips);
				 cb_showsys = (CheckBox) view.findViewById(R.id.cb_pms_showsys);
				 tv_showsys = (TextView) view.findViewById(R.id.tv_pms_showsys);
				 ll_pms_loading =  (LinearLayout) view.findViewById(R.id.ll_pms_loading);
				 lv_pms = (ListView) view.findViewById(R.id.lv_pms);
				 initAppinfos();
				 userchoList = new ArrayList<String>();
				 lv_pms.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						
						CheckBox cb_item = (CheckBox) view.findViewById(R.id.cb_pm_item);
						// ����������ʹ���ڴ��С��tv�ؼ�����ʾ����
						TextView tv_pkgname = (TextView) view.findViewById(R.id.tv_pmi_memory);
						String pkgname  = appinfos.get(position).getPackageName();
						dao  = new ProManagerDAO(getActivity());
						Log.e(TAG, pkgname);
						Log.e(TAG, tv_pkgname.getText().toString());
						if (cb_item.isChecked()) {
						//��ӵ����ݿ�
							userchoList.add(pkgname);
							dao.add(pkgname);
						}else {
							//ɾ��
							userchoList.remove(pkgname);
							dao.delete(pkgname);
						}
						 if (userapplist != null) {
							 int ichoose = userchoList.size();
							 tv_curr.setText("��ǰ��ѡ�� "+ichoose+"��");
						}
						
					}
					 
					 
				});
				 
				 cb_showsys.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

						if (isChecked) {
							Editor editor = sp.edit();
							editor.putBoolean("showsyspro", true);
							editor.commit();
							tv_showsys.setTextColor(Color.GREEN);
							tv_showsys.setText("��ʾϵͳ����");
						}
						else{
							Editor editor = sp.edit();
							editor.putBoolean("showsyspro", false);
							editor.commit();
							tv_showsys.setTextColor(Color.RED);
							tv_showsys.setText("����ʾϵͳ����");
						}
					}
				});

				 
				 tv_showsys.setOnClickListener(new OnClickListener() {
					 @Override
					 public void onClick(View v) {
						 Editor editor = sp.edit();
						 if (cb_showsys.isChecked()) {
							 cb_showsys.setChecked(false);
						editor.putBoolean("showsyspro", false);
						editor.commit();

					} else {
						cb_showsys.setChecked(true);
						editor.putBoolean("showsyspro", true);
						editor.commit();
						}
					}
				});
				 
				 tv_offscreen.setOnClickListener(new OnClickListener() {
					 @Override
					 public void onClick(View v) {
						 Editor editor = sp.edit();
						 
						 if (cb_offscreen.isChecked()) {
							 cb_offscreen.setChecked(false);
						editor.putBoolean("offscreenclear", false);
						editor.commit();

					} else {
						cb_offscreen.setChecked(true);
						editor.putBoolean("offscreenclear", true);
						editor.commit();
						}
					}
				});
				 
				 tv_start.setOnClickListener(new OnClickListener() {
					 @Override
					 public void onClick(View v) {
						 Editor editor = sp.edit();
						 
						 if (cb_start.isChecked()) {
						cb_start.setChecked(false);
						editor.putBoolean("pmautoclear", false);
						editor.commit();

					} else {
						cb_start.setChecked(true);
						editor.putBoolean("pmautoclear", true);
						editor.commit();
						}
					}
				});
				 
				 cb_offscreen.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						
						@Override
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							
							Intent intent = new Intent(getActivity(),ProManagerService.class);
							if (isChecked) {
								Editor editor = sp.edit();
								editor.putBoolean("offscreenclear", true);
								editor.commit();
								getActivity().startService(intent);
								tv_offscreen.setTextColor(Color.GREEN);
								tv_offscreen.setText("�ѿ��������������");
							}
							else{
								Editor editor = sp.edit();
								editor.putBoolean("offscreenclear", false);
								editor.commit();
								getActivity().stopService(intent);
								tv_offscreen.setTextColor(Color.RED);
								tv_offscreen.setText("δ���������������");
							}
						}
					});
				 
				 
				 cb_start.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						
						Intent intent = new Intent(getActivity(),ProManagerService.class);
						if (isChecked) {
							Log.d(TAG, "Start Service");
							Editor editor = sp.edit();
							editor.putBoolean("pmautoclear", true);
							editor.commit();
							getActivity().startService(intent);
							tv_start.setTextColor(Color.GREEN);
							tv_start.setText("�ѿ����Զ��������");
						}
						else{
							Editor editor = sp.edit();
							editor.putBoolean("pmautoclear", false);
							editor.commit();
							getActivity().stopService(intent);
							tv_start.setTextColor(Color.RED);
							tv_start.setText("δ�����Զ��������");
						}
					}
				});
				 
				 
				 tv_time.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						AlertDialog.Builder builder = new Builder(getActivity());
						// Ĭ����������5����
						int lastpos = sp.getInt("timeleft", 5*60*10000);
						//���õڶ�������  ��ʾ֮ǰ�������ʾģʽ����
						builder.setTitle("������ʱ���� �� ����Ϊ��λ");
						EditText et = new EditText(getActivity());
						et.setText("5");
						builder.setView(et);
						builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Toast.makeText(getActivity(), "��������Ϊ: ", 0).show();
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
				 

			}
			
	        return view;

	    }

		protected void KillAllBGPros(List<String> list) {

		ActivityManager am = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
		list.remove("cn.phoniex.ssg");
		for (int j = 0; j < list.size(); j++) {
			am.killBackgroundProcesses(list.get(j));	
		}
		 Toast.makeText(getActivity(), "��������̨����"+list.size()+"��", 0).show();
		
		}

/*
 *ͨ����ȡ"/proc/cupinfo"����ȡandroid�ֻ���CPU������ͨ����ȡ"/proc/stat"�ļ�������CPU��ʹ���ʣ����ﲻ��׸��
 * */
		private void initproinfos() {
			
			//ʹ����ʱlist�������list������֮����ֱ��ȫ����ӵ�prolist�б������ʱ��ռ��
			List<ProcessInfo> processInfos = new ArrayList<ProcessInfo>();
			for (RunningAppProcessInfo info : runningappinfos) {
				ProcessInfo  tmpinfo  = new ProcessInfo();
				int pid = info.pid;
				tmpinfo.setPid(info.pid);
				tmpinfo.setPkgname(info.processName);
				try {
					ApplicationInfo appinfo = pm.getApplicationInfo(info.processName, 0);
					tmpinfo.setIcon(appinfo.loadIcon(pm));
			        if ((appinfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
			      ||(appinfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
			        	tmpinfo.setbSysPro(true);
			        }else {
						tmpinfo.setbSysPro(false);
					}
					tmpinfo.setAppname(appinfo.loadLabel(pm).toString());
					MemoryInfo[] memoryInfos;
					memoryInfos = am.getProcessMemoryInfo(new int[]{pid});
					int memsize = memoryInfos[0].getTotalPrivateDirty();
					memsize = memoryInfos[0].dalvikPrivateDirty;
					tmpinfo.setmemSize(memsize);
					processInfos.add(tmpinfo);
					usingMem = usingMem +memsize;
					tmpinfo = null;
					
				} catch (NameNotFoundException e) {
					tmpinfo.setIcon(getActivity().getResources().getDrawable(R.drawable.ic_launcher));
					MemoryInfo[] memoryInfos = am.getProcessMemoryInfo(new int[]{pid});
					int memsize = memoryInfos[0].getTotalPrivateDirty();
					tmpinfo.setAppname(info.processName);
					//memsize = memoryInfos[0].dalvikPrivateDirty;
					tmpinfo.setmemSize(memsize);
					processInfos.add(tmpinfo);
					usingMem = usingMem +memsize;
					tmpinfo = null;
						//e.printStackTrace();
				}
				
			}
			proinfos.addAll(processInfos);
		}
		
		private void updateRunningAppinfos() {
			//������timer��������������������ǿ����л�����һ��fragment�����ˣ�
			//���ʱ��Ͳ�Ӧ���ٲ��������,�������õ�iarg�����жϵ�ǰ���ĸ�fragment
			if (iarg == 0) {
				// ll_pml_loading.setVisibility(View.VISIBLE);
				new Thread() {
					@Override
					public void run() {
						usingMem = 0;
						proinfos.clear();
						initproinfos();
						handler.sendEmptyMessage(1);
					}

				}.start();
			}

	}
		
		private void initRunningAppinfos() {
		ll_pml_loading.setVisibility(View.VISIBLE);
		new Thread() {
			@Override
			public void run() {
				usingMem = 0;
				proinfos.clear();
				initproinfos();
				handler.sendEmptyMessage(0);
			}

		}.start();

	}
		private void initAppinfos() {
		ll_pms_loading.setVisibility(View.VISIBLE);
		new Thread() {

			@Override
			public void run() {
				appinfos = new AppInfosProvider(getActivity().getApplicationContext()).getAllApps();
				pms_handler.sendEmptyMessage(0);
			}

		}.start();

	}

		public class ProManagerListAdapter extends BaseAdapter {
			
			public ProManagerListAdapter(List<ProcessInfo> listAppinfos) {

				userprolist = new ArrayList<ProcessInfo>();

				sysprolist = new ArrayList<ProcessInfo>();

				for (ProcessInfo info : proinfos) {
					if (info.isbSysPro()) {
						sysprolist.add(info);
					} else {
						userprolist.add(info);
					}
				}
			}

			@Override
			public int getCount() {
				sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
				boolean bsys = sp.getBoolean("showsyspro", false);
				if (bsys) {
					return proinfos.size() + 2;
				}else {
					return userprolist.size()+1;
				}
			}

			@Override
			public Object getItem(int position) {
				if (position == 0) {
					return position;//user title 
				}else if (position <= userprolist.size()) {
					return userprolist.get(position - 1);
				}else if (position == userprolist.size() +1) {
					return position;// sys title
				}else if (position <= proinfos.size()+2) {
					return sysprolist.get(position - userprolist.size() -2);
				}
				return position;
			}

			@Override
			public long getItemId(int position) {
				if (position == 0) {
					return -1;//user title 
				}else if (position <= userprolist.size()) {
					return position - 1;
				}else if (position == userprolist.size() +1) {
					return  -1;// sys title
				}else if (position <= proinfos.size()+2) {
					return position -2;
				}
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				
				View view = null;
				if (position == 0) {
					TextView tv = new TextView(getActivity());
					tv.setText("�û�����"+ userprolist.size() + "��");
					return tv;
					
				}else if (position == userprolist.size() + 1) {
					TextView tv = new TextView(getActivity());
					tv.setText("ϵͳ����"+sysprolist.size() + "��");
					return tv;
				}else  {
					
					if (convertView == null) {
						view = View.inflate(getActivity(),
								R.layout.pro_manager_item, null);
						//���convertView �� TextView ˵�����Ǹ��õ��� ����ؼ� ��������Ӧ���������� �����ļ�
					} else if (convertView instanceof TextView){
						view = View.inflate(getActivity(),
								R.layout.pro_manager_item, null);
					}else {
						view = convertView;
					}
				}

				ImageView iv = (ImageView) view.findViewById(R.id.imv_pmi_icon);
				TextView tv_appname = (TextView) view.findViewById(R.id.tv_pmi_appname);
				TextView tv_memory =  (TextView) view.findViewById(R.id.tv_pmi_memory);
				
				ProcessInfo info;
				if (position <= userprolist.size()) {
					 info = userprolist.get(position -1);
				}else {
					info = sysprolist.get(position - userprolist.size()- 2);
				}
				// ���ǻ�ȥ���ڴ��С��λ�� kb ���� modeΪ 1
				tv_memory.setText(GetStrValue.getStrByArg(info.getmemSize(),1));
				iv.setImageDrawable(info.getIcon());
				tv_appname.setText(info.getAppname());
				CheckBox cb_list = (CheckBox) view.findViewById(R.id.cb_pm_item);
				// ���س������ѡ��
				if (info.getPkgname().equals("cn.phoniex.ssg")) {
					cb_list.setVisibility(View.GONE);
				}
				if (bitems[position]) {
					cb_list.setChecked(true);
				}else {
					cb_list.setChecked(false);
				}
				return view;

			}

		}
		
		private class ProManagerSetAdapter extends BaseAdapter {
			
			public ProManagerSetAdapter(List<Appinfos> appinfosList) {

				userapplist = new ArrayList<Appinfos>();

				sysapplist = new ArrayList<Appinfos>();
				

				for (Appinfos info : appinfosList) {
					if (info.isSysApp()) {
						sysapplist.add(info);
					} else {
						userapplist.add(info);
					}
				}
			}

			@Override
			public int getCount() {
				return appinfos.size();
			}

			@Override
			public Object getItem(int position) {
				return appinfos.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				
				View view = null;
				if (convertView == null) {
					view = View.inflate(getActivity(),
							R.layout.pro_manager_item, null);
				} else {
					view = convertView;
				}
				Appinfos info = appinfos.get(position);
				ImageView iv = (ImageView) view.findViewById(R.id.imv_pmi_icon);
				TextView tv_appname = (TextView) view.findViewById(R.id.tv_pmi_appname);
				//�Ҹ����� item�Ĳ����ļ� ����id �� �ڴ��С��ʾ�Ŀؼ�id
				TextView tv_pkg_name =  (TextView) view.findViewById(R.id.tv_pmi_memory);
				CheckBox cb_item = (CheckBox) view.findViewById(R.id.cb_pm_item);
				
				dao  = new ProManagerDAO(getActivity());
				if (dao.find(info.getPackageName())) {
					cb_item.setChecked(true);
				}
				
				tv_pkg_name.setText(info.getPackageName());
				iv.setImageDrawable(info.getIcon());
				tv_appname.setText(info.getAppname());
				return view;

			}

		}


		@Override
	    public void onDestroy() {
	        super.onDestroy();
	        Log.d(TAG, "BaseFragment-----onDestroy");
	    }


}
