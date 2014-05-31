package cn.phoniex.ssg.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BlackListDBHelper extends SQLiteOpenHelper{

	
	
	private static final String TAG = "BlackListDBHelper";
	private static final int  VERSION = 1;
	private static final String DBNAME = "blacklist.db";
	public BlackListDBHelper(Context context) {
		//默认是四个参数的我们只用一个context还剩下  name, CursorFactory factory, version
		super(context, DBNAME, null, 1);//version can't be zero

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//第一次创建表的时候执行
		db.execSQL("create table blacklist (_id integer primary key autoincrement , number varchar(20))");		
	}

	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// 仅仅是源代码备份  并未使用数据库备份功能
		//我们数据库更新了，执行这个 备份原来的表 创建新表
		Log.i(TAG, " DBOpenHelper onUpgrade");
		String temptable = "tmp_student";
		String tablename = "t_student";

		//先把原始的表改名为 old表 alter table t_student rename to t_oldstu
		db.execSQL("alter table "+tablename+" rename to"+temptable);
		db.execSQL("create table "+tablename+"(sid integer primary key, name varchar(20),age integer,sex varchar(5))");
		//然后使用下面的命令从旧表中读取内容更新到新表，不存在的值使用指定值填充
		//sqlite> insert into t_student (sid,name,age,sex) select sid,sname,23,'M'from t_oldstu;
		String sqlString  = "insert into "+ tablename +"(name,age,sex) select name,age,'man',from"+temptable;
		db.execSQL(sqlString);
		//db.execSQL("drop table "+tmp_student);//删除表
	}
	

	


}
