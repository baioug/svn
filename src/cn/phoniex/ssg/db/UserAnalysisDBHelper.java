package cn.phoniex.ssg.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class UserAnalysisDBHelper extends SQLiteOpenHelper {

	public UserAnalysisDBHelper(Context context) {
		super(context, "useranalysis.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("create table useranalysis (_id integer primary key autoincrement , pkgname varchar(40) ,  icut integer,  timeday date, timehour hour)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
