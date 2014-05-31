package cn.phoniex.ssg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Inflater;

import cn.phoniex.ssg.dao.BlackListDAO;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.style.BulletSpan;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.MenuInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class BlackListActivity extends Activity {

	private ListView lv;
	private EditText et;
	private Button button;
	private ImageButton imb;
	private List<String> numbers;
	private blacknoAdapter adapter;
	private BlackListDAO dao ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.blacklist);
		 dao = new BlackListDAO(BlackListActivity.this);
		button = (Button) findViewById(R.id.bt_add_bl_num);
		imb = (ImageButton) findViewById(R.id.imb_bl_contacts);
		et  = (EditText) findViewById(R.id.ed_blacklist);
		// ����edittext�������ʽ ��֪��ϵͳĬ�ϵ��ֻ��� ��ʽ����ôȷ���� ������ʹ�� ������ʽ
		//et.setInputType(InputType.TYPE_CLASS_PHONE); 
		lv = (ListView) findViewById(R.id.lv_blacklist);

		// ��listview ע�� �����Ĳ˵�
		registerForContextMenu(lv);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String number = et.getText().toString().trim();
				if (number.length()>0) {
					String match = "\\d{3,}";//����3λ����
					if (number.matches(match)) {
						if (dao != null) {
							dao.addNo(number);
							//֪ͨlistview��������
							//�����µ� adapter ˢ��������
							numbers.clear();
							numbers = null;
							numbers = dao.getallNo();
							adapter.notifyDataSetChanged();
						}
					}
					else {
						Toast.makeText(BlackListActivity.this,"�����ʽ�Ƿ�", 0).show();
					}
				}else {
					Toast.makeText(BlackListActivity.this, "���������", 0).show();
				}
			}
		});
		imb.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intentcontent = new Intent(BlackListActivity.this,SelectContactActivity.class);
				Toast.makeText(BlackListActivity.this, "����ϵ����ѡ��", 0).show();
				startActivityForResult(intentcontent, 0);
				
			}
		});

		numbers  =  dao.getallNo();
		numbers.add("18813501816");
		adapter = new blacknoAdapter();
		lv.setAdapter(adapter);
		
	}

	
	
	@Override
	protected void onStart() {
		
		super.onStart();
		Intent intent = getIntent();
		String number = intent.getStringExtra("number");
		if (number!= null) {
			et.setText(number);
		}
	}



	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.blacklistmenu, menu);
		
	}



	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		if (dao == null) {
			Toast.makeText(BlackListActivity.this, "���ݿ����", 0).show();
		}
		else {
			AlertDialog.Builder builder;
			switch (item.getItemId()) {
			case R.id.bl_menu_del:
				dao.delNo(numbers.get(info.position));
				numbers.clear();
				numbers = null;
				numbers = dao.getallNo();
				adapter.notifyDataSetChanged();
				break;
			case R.id.bl_menu_batch:
				builder = new Builder(BlackListActivity.this);
				AlertDialog dialog ;
				builder.setTitle("����ɾ������");
				builder.setIcon(R.drawable.black_delete);
				int size = numbers.size();
				// ArrayList ת��Ϊ����
				final String[] arrNos = (String[]) numbers.toArray(new  String[size]);
				//  ����ת ArrayList ����ʹ������ʾ��
				//numbers = Arrays.asList(arrNos);
				final boolean[] arrChked = new boolean[size];
				for (int i = 0; i < arrChked.length; i++) {
					arrChked[i] = false;
				}
				builder.setMultiChoiceItems(arrNos, arrChked, new OnMultiChoiceClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
						arrChked[which] = isChecked;
					}
				});
				builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ArrayList<String> chedNos = new ArrayList<String>();
						for (int i = 0; i < arrChked.length; i++) {
							if (arrChked[i]) {
								chedNos.add(arrNos[i]);
							}
						}
						if (chedNos.size() > 0) {
							dao.delNos(chedNos);
							numbers.clear();
							numbers = null;
							numbers = dao.getallNo();
							adapter.notifyDataSetChanged();
						}
						else {
							Toast.makeText(BlackListActivity.this, "δѡ���κ�ѡ��", 0).show();
						}
						
					}
				});
				builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				});
				builder.create().show();
				break;
			case R.id.bl_menu_up:
				final String oldNo  = numbers.get(info.position);
			    builder = new Builder(BlackListActivity.this);
				final EditText et_no = new EditText(BlackListActivity.this);
				builder.setView(et_no);
				builder.setPositiveButton("�޸�", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String newNo  = et_no.getText().toString().trim();
						String match = "\\d{3,}";//����3λ����
						if (newNo.matches(match)) {
							dao.updateNo(oldNo, newNo);
							numbers.clear();
							numbers = null;
							numbers = dao.getallNo();
							adapter.notifyDataSetChanged();
							
						}else {
		
							Toast.makeText(BlackListActivity.this, "��ʽ�Ƿ�", 0).show();
						}
						
					}
				});
				builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				});
				builder.create().show();
				break;
			case R.id.bl_menu_wp:
				dao.RemoveAll();
				numbers.clear();
				numbers.add("������Ϊ��");
				adapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
			
		}
	
		return super.onContextItemSelected(item);

		
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 0) {
			if (data != null) {
				String number = data.getStringExtra("number");
				et.setText(number);
			}
		}
	}



	public class blacknoAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return numbers.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return numbers.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(BlackListActivity.this, R.layout.blacklist_item, null);
			TextView tv = (TextView) view.findViewById(R.id.tv_bl_item);
			tv.setText(numbers.get(position));
			System.out.println(numbers.get(position));
			return view;
		}

	}
	
	
	
}
