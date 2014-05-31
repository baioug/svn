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
		//Ĭ�����ĸ�����������ֻ��һ��context��ʣ��  name, CursorFactory factory, version
		super(context, DBNAME, null, 1);//version can't be zero

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//��һ�δ������ʱ��ִ��
		db.execSQL("create table blacklist (_id integer primary key autoincrement , number varchar(20))");		
	}

	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// ������Դ���뱸��  ��δʹ�����ݿⱸ�ݹ���
		//�������ݿ�����ˣ�ִ����� ����ԭ���ı� �����±�
		Log.i(TAG, " DBOpenHelper onUpgrade");
		String temptable = "tmp_student";
		String tablename = "t_student";

		//�Ȱ�ԭʼ�ı����Ϊ old�� alter table t_student rename to t_oldstu
		db.execSQL("alter table "+tablename+" rename to"+temptable);
		db.execSQL("create table "+tablename+"(sid integer primary key, name varchar(20),age integer,sex varchar(5))");
		//Ȼ��ʹ�����������Ӿɱ��ж�ȡ���ݸ��µ��±������ڵ�ֵʹ��ָ��ֵ���
		//sqlite> insert into t_student (sid,name,age,sex) select sid,sname,23,'M'from t_oldstu;
		String sqlString  = "insert into "+ tablename +"(name,age,sex) select name,age,'man',from"+temptable;
		db.execSQL(sqlString);
		//db.execSQL("drop table "+tmp_student);//ɾ����
	}
	

	


}
