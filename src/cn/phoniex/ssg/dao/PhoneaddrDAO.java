package cn.phoniex.ssg.dao;

import android.database.sqlite.SQLiteDatabase;

public class PhoneaddrDAO {

	
	public static SQLiteDatabase OpenPhoAddrDB(String filepath)
	{
		return SQLiteDatabase.openDatabase(filepath, null, SQLiteDatabase.OPEN_READONLY);
		
	}
}
