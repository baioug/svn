package cn.phoniex.ssg.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class WiFiDBHelper extends SQLiteOpenHelper {

	public WiFiDBHelper(Context context) {
		super(context, "wifiinfo.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("create table wifiinfo (_id integer primary key autoincrement , ssid varchar(40) , password varchar(100) ,  mac varchar(40) ,linkspeed integer,  rssi integer)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
