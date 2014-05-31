package cn.phoniex.ssg;
import java.util.List;

import cn.phoniex.ssg.domain.ContactInfo;
import cn.phoniex.ssg.engine.ContactInfoProvider;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class SelectContactActivity extends Activity implements Runnable {

	private ListView lv;
	private ListAdapter adapter;
	private List<ContactInfo> infos;
	private ProgressDialog pd;
	private ContactInfoProvider provider;
	private Handler mHandler =    new  Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			lv.setAdapter(adapter);
			pd.dismiss();
		}
		
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.select_contact);
		 provider = new ContactInfoProvider(this);

		
		lv = (ListView) findViewById(R.id.lv_select_contact);

		adapter = new contactAdapter();

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				String number = infos.get(position).getPhone();
				Intent retintent = new Intent();
				retintent.putExtra("number", number);
				setResult(0,retintent);
				finish();
			}
		});

		pd = ProgressDialog.show(SelectContactActivity.this, "正在为你加载界面", "正在努力读取联系人");
		Thread t = new Thread(this);
		t.start();
		
	}
	
	public class contactAdapter extends BaseAdapter 
	{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return infos.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return infos.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ContactInfo info = infos.get(position);
			LinearLayout ll = new LinearLayout(SelectContactActivity.this);
			ll.setOrientation(LinearLayout.VERTICAL);
			TextView tv_name = new TextView(SelectContactActivity.this);
			TextView tv_phone = new TextView(SelectContactActivity.this);
			tv_name.setTextSize(22);
			tv_phone.setTextSize(24);
			tv_name.setText("姓名:"+info.getName());
			tv_phone.setText("号码:"+ info.getPhone());
			ll.addView(tv_name);
			ll.addView(tv_phone);
			
			return ll;
		}

		
		
	}

	@Override
	public void run() {
		
		infos = provider.getContactInfos();
		mHandler.sendEmptyMessage(0);
	}
	
}
