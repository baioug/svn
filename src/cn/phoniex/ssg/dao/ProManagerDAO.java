package cn.phoniex.ssg.dao;

import java.util.ArrayList;
import java.util.List;

import cn.phoniex.ssg.db.AppLockDBHelper;
import cn.phoniex.ssg.db.ProManagerDBHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ProManagerDAO {
	
	private String TAG = "ProManagerDAO";
	private Context context;
	private ProManagerDBHelper dbHelper;

	
	public ProManagerDAO(Context context) {
		
		this.context = context;
		dbHelper = new ProManagerDBHelper(context);
	}

	public List<String> getAll() {
		
		List<String> retList =  new ArrayList<String>();
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		if (db == null) {
			Log.e(TAG, "db == null");
		}

		if (db.isOpen()) {
		  Cursor cursor =	db.rawQuery("select pkgname from prolist", null);
			while (cursor.moveToNext()) {
				String packname = cursor.getString(0);
				retList.add(packname);
			}
			cursor.close();
			db.close();
		}
		return retList;
	}
	
	
	public boolean find(String pkgname) {
		boolean result = false;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery(
					"select pkgname from prolist where pkgname = ?",
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
			db.execSQL("insert into prolist (pkgname) values (?)", new Object[]{pkgname});
			db.close();
		}
	}

	public void delete(String pkgname){

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.execSQL("delete from prolist where pkgname=?", new Object[]{pkgname});
			db.close();
		}
	}


}
