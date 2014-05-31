package cn.phoniex.ssg.dao;

import java.util.ArrayList;
import java.util.List;

import cn.phoniex.ssg.db.AppLockDBHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AppLockDao {
	private String TAG = "AppLockDao";
	private Context context;
	private AppLockDBHelper dbHelper;

	public AppLockDao(Context context) {
		this.context = context;
		dbHelper = new AppLockDBHelper(context);
		if (dbHelper == null) {
			Log.e(TAG, "dbHelper == null");
		}else {
			Log.e(TAG, "not null");
		}
	}

	
	public boolean find(String pkgname) {
		boolean result = false;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery(
					"select pkgname from applock where pkgname=?",
					new String[] { pkgname });
			if (cursor.moveToNext()) {
				result = true;
			}
			cursor.close();
			db.close();
		}
		return result;
	}

	public void add(String pkgname){
		if(find(pkgname)){
			return ;
		}
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.execSQL("insert into applock (pkgname) values (?)", new Object[]{pkgname});
			db.close();
		}
	}

	public void delete(String pkgname){

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.execSQL("delete from applock where pkgname=?", new Object[]{pkgname});
			db.close();
		}
	}

	public List<String> getAllLockedApps(){
		
		// getReadableDatabase

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db == null) {
			Log.e(TAG, "db == null");
		}
		List<String> pkgnames = new ArrayList<String>();
		if (db.isOpen()) {
		  Cursor cursor =	db.rawQuery("select pkgname from applock", null);
			while (cursor.moveToNext()) {
				String packname = cursor.getString(0);
				pkgnames.add(packname);
			}
			cursor.close();
			db.close();
		}
		return pkgnames;
	}
}
