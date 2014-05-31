package cn.phoniex.ssg;

import cn.phoniex.ssg.engine.QueryPhoAddrService;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class QueryPhoAddrActivity extends Activity {

	
	private EditText et_Pho;
	private Button bt_OK;
	private TextView tv_Adder;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.query_phoaddr);
		bt_OK = (Button) findViewById(R.id.bt_query_addr);
		et_Pho = (EditText) findViewById(R.id.et_query_number);
		tv_Adder = (TextView) findViewById(R.id.tv_query_adder);
		
		bt_OK.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String Phostr = et_Pho.getText().toString();
				String  Filter = "";
				if (Phostr.trim().length() == 0) {
					
					Animation shakeAm = AnimationUtils.loadAnimation(QueryPhoAddrActivity.this, R.anim.shake);
					v.setAnimation(shakeAm);
				}else {
					String addr = new QueryPhoAddrService().showphoaddr(Phostr, QueryPhoAddrActivity.this);
					tv_Adder.setText("πÈ Ùµÿ:"+addr);
				}
				
			}
		});
		
	}

	
	
}
