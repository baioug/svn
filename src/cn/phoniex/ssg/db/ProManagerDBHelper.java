package cn.phoniex.ssg.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProManagerDBHelper extends SQLiteOpenHelper{


	public ProManagerDBHelper(Context context) {
		super(context, "prolist.db", null, 1);
	}

	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table prolist (_id integer primary key autoincrement , pkgname varchar(20))");	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

	
}
