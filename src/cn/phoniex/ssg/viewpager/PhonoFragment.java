package cn.phoniex.ssg.viewpager;

import java.util.Map;

import cn.phoniex.ssg.R;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

public class PhonoFragment extends BaseFragment{
	private View numView;
	private ImageButton btnSearch;
	private EditText number_et;
	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		numView = inflater.inflate(R.layout.tab_widget_number, container, false);
		number_et = (EditText) numView.findViewById(R.id.numberlocation);
		btnSearch = (ImageButton) numView.findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(this);
		return numView;
	}


	@Override
	public void onClick(View v) {
		String phoneNumber = number_et.getText().toString();
		String prefix, center;
		Map<String,String> map = null;
		
		if (isZeroStarted(phoneNumber) && getNumLength(phoneNumber) > 2){
			prefix = getAreaCodePrefix(phoneNumber);
			map = dao.queryAeraCode(prefix);
			
		}else if (!isZeroStarted(phoneNumber) && getNumLength(phoneNumber) > 6){
			prefix = getMobilePrefix(phoneNumber);
			center = getCenterNumber(phoneNumber);
			map = dao.queryNumber(prefix, center);
		}
		
		DialogUtils.showNumberDialog(getActivity(), view, map, phoneNumber);
	}

	/**得到输入区号中的前三位数字或前四位数字去掉首位为零后的数字*/
	public String getAreaCodePrefix(String number){
		if (number.charAt(1) == '1' || number.charAt(1) == '2')
			return number.substring(1,3);
		return number.substring(1,4);
	}
	
	/**得到输入手机号码的前三位数字*/
	public String getMobilePrefix(String number){
		return number.substring(0,3);
	}
	
	/**得到输入号码的中间四位号码，用来判断手机号码归属地*/
	public String getCenterNumber(String number){
		return number.substring(3,7);
	}
	
	public boolean isZeroStarted(String number){
		if (number == null || number.isEmpty()){
			return false;
		}
		return number.charAt(0) == '0';
	}
	
	/**得到号码的长�?*/
	public int getNumLength(String number){
		if (number == null || number.isEmpty()  )
			return 0;
		return number.length();
	}
}

