package cn.phoniex.ssg.adapter;


import cn.phoniex.ssg.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivityAdapter extends BaseAdapter {

	private static final String TAG = "MainActivityAdapter";
	private Context context;
	private static ImageView iv_icon;
	private static TextView tv_name;
	private SharedPreferences sp;
	private static String[] names = { "丢失防护", "通讯卫士", "软件管理", "任务管理", "流量查看",
		"手机杀毒", "系统清理", "高级工具", "帮助中心" };
	private static int[] icons = { R.drawable.widget09, R.drawable.widget02,
		R.drawable.widget01, R.drawable.widget07, R.drawable.widget05,
		R.drawable.widget04, R.drawable.widget06, R.drawable.widget03,
		R.drawable.widget08 };
	
	public MainActivityAdapter(Context context) {
		this.context = context;
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		
	}

	@Override
	public int getCount() {
		return names.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.i(TAG,"getview "+ position);
		View view = View.inflate(context, R.layout.main_item, null);
		iv_icon =  (ImageView) view.findViewById(R.id.iv_main_icon);
		tv_name =  (TextView) view.findViewById(R.id.tv_main_name);
		String pretendname = sp.getString("pretendname", null);
		if (position == 0 && pretendname != null) {
			tv_name.setText(pretendname);
			iv_icon.setImageResource(R.drawable.ic_launcher);
		}
		else {
			iv_icon.setImageResource(icons[position]);
			tv_name.setText(names[position]);

		}
		return view;
	}

}
