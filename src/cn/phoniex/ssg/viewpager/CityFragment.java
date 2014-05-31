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


public class CityFragment extends BaseFragment {
	private View cityView;
	private ImageButton btnSearchCity;
	private EditText city_et;
	private ListView listView;
	private SimpleCursorAdapter mAdapter;
	private Cursor cursor;
	private String TABLE_COLUMN_NAME = "city";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		cityView = inflater.inflate(R.layout.tab_widget_city, container, false);
		findViewByIds();
		dao = new PhoAddrDAO(sqliteDB);
		city_et.addTextChangedListener(new MyTextWatcher());
		listView.setOnItemClickListener(new MyItemClickListener());
		btnSearchCity.setOnClickListener(this);
		return cityView;
	}

	private void findViewByIds() {
		city_et = (EditText) cityView.findViewById(R.id.numberlocationcitycode);
		btnSearchCity = (ImageButton) cityView.findViewById(R.id.btnSearchCityCode);
		listView = (ListView) cityView.findViewById(R.id.listViewCityPicker);
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		String city = city_et.getText().toString();
		Map<String, String> map = dao.queryCity(city);
		DialogUtils.showCityDialog(getActivity(), cityView, map, city);
	}

	private class MyTextWatcher implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@SuppressLint("NewApi")
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			cursor = dao.getPossibleCities(s.toString());
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
	private class MyItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			city_et.setText(cursor.getString(cursor.getColumnIndex(TABLE_COLUMN_NAME)));
		}
		
	}

}
