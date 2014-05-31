package cn.phoniex.ssg.viewpager;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View.OnClickListener;



@SuppressLint("NewApi")
public abstract class BaseFragment extends Fragment implements OnClickListener {
	public SQLiteDatabase sqliteDB;
	public PhoAddrDAO dao;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initDB();
	}

	private void initDB() {
		AssetsDatabaseManager.initManager(getActivity().getApplicationContext());
		AssetsDatabaseManager mg = AssetsDatabaseManager.getAssetsDatabaseManager();
		sqliteDB = mg.getDatabase("number_location.zip");
		dao = new PhoAddrDAO(sqliteDB);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		AssetsDatabaseManager.closeAllDatabase();
	}
}
