package cn.phoniex.ssg.viewpager;


import java.util.Map;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import cn.phoniex.ssg.R;



public class CountryFragment extends BaseFragment {
	private View countryView;
	private ImageButton btnSearchCountry;
	private EditText country_et;
	private ListView listView;
	private SimpleCursorAdapter mAdapter;
	private Cursor cursor;
	private String TABLE_COLUMN_NAME = "country";
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		countryView = inflater.inflate(R.layout.tab_widget_country, container, false);
		findViewByIds();
		dao = new PhoAddrDAO(sqliteDB);
		country_et.addTextChangedListener(new MyTextWatcher());
		listView.setOnItemClickListener(new MyItemClickListener());
		btnSearchCountry.setOnClickListener(this);
		return countryView;
	}

	private void findViewByIds() {
		country_et = (EditText) countryView.findViewById(R.id.numberlocationcountrycode);
		btnSearchCountry = (ImageButton) countryView.findViewById(R.id.btnSearchCountryCode);
		listView = (ListView) countryView.findViewById(R.id.listViewCountryPicker);
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String country = country_et.getText().toString();
		Map<String,String> map = dao.queryCountry(country);
		DialogUtils.showCountryDialog(getActivity(), countryView, map, country);
	}
	
	 class MyTextWatcher implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@SuppressLint("NewApi")
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			cursor = dao.getPossibleCountry(s.toString());
			String[] from = {TABLE_COLUMN_NAME};
			int[] to = {android.R.id.text1};
			mAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_1, 
					cursor, from , to,SimpleCursorAdapter.IGNORE_ITEM_VIEW_TYPE);
			listView.setAdapter(mAdapter);
		}

		@Override
		public void afterTextChanged(Editable s) {
			
		}
	}
	 class MyItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			country_et.setText(cursor.getString(cursor.getColumnIndex(TABLE_COLUMN_NAME)));
		}
		
	}
}
