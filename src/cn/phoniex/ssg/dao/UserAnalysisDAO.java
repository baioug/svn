package cn.phoniex.ssg.dao;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.Time;
import android.util.Log;
import cn.phoniex.ssg.db.UserAnalysisDBHelper;

public class UserAnalysisDAO {
	
	private UserAnalysisDBHelper dbHelper ;
	private Context context;
	private String TAG = "UserAnalysisDAO";
	
	public UserAnalysisDAO(Context context){
		this.context = context;
		dbHelper = new UserAnalysisDBHelper(context);
	}
	
	public List<String> getByFilter(int iNo){
		List<String> retList =  new ArrayList<String>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		Time tm = new Time("GMT+8");
		tm.setToNow();
		//String dateStr = tm.year
		int ihour = (tm.hour+8)%24;//ò��֮ǰ���õĶ�����û�����û�����˸�Сʱ
		
		if (db.isOpen()) {
			//select pkgname,icut from useranalysis  where  timehour = 10 order by icut desc limit 1
			Cursor cursor = db.rawQuery("select pkgname from useranalysis where timehour = "+ String.valueOf(ihour)+" order by icut desc limit "+String.valueOf(iNo), null);
			while (cursor.moveToNext()) {
				String pkgname = cursor.getString(0);
				retList.add(pkgname);
			}
			//�����ǰʱ���ڳ���APP�������� �� ����ʱ����� ��������б�
			if (retList.size() < iNo) {
				retList.clear();//���ԭʼ�б��������
				cursor = db.rawQuery("select pkgname from useranalysis order by icut desc limit "+String.valueOf(iNo), null);
				while (cursor.moveToNext()) {
					String pkgname = cursor.getString(0);
					if (!retList.contains(pkgname)) {//��ͬСʱ�Ļ��ظ���ʾ ���˵�
						retList.add(pkgname);
					}
				}
			}
			cursor.close();
			db.close();
		}
		return retList;
	}
	
	public void add(String pkgname, String date ,int ihour){
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (pkgname.contains("launcher")) {// ���˵� launcher ������
			
			return;
		}
		if (db.isOpen()) {
			//select icut from useranalysis where pkgname = 'hgf' and timeday = '2014-05-16' and timehour = 11
			Cursor cursor = db.rawQuery("select icut from useranalysis where pkgname = ?  and timeday = ? and timehour = ?",new String[]{pkgname,date,String.valueOf(ihour)});
			//Cursor cursor = db.rawQuery("select icut from useranalysis where pkgname = "+ pkgname +" and timeday = " + date +" and timehour = " +ihour, null);
			if (cursor.moveToNext()) {
				int icur = cursor.getInt(0);
				icur = icur + 1;
				db.execSQL("update useranalysis set icut = ? where  pkgname = ? and  timeday = ?  and timehour = ?", new Object[]{icur,pkgname,date,ihour});
				
			}else {
				db.execSQL("insert into useranalysis (pkgname,icut,timeday,timehour) values(?,?,?,?)",new Object[]{pkgname,1,date,ihour});
			}
		}
	}
	
	public int getcount(String pkgname){
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		if (db.isOpen()) {
			
			Cursor cursor = db.rawQuery("select icut from useranalysis where pkgname = ? ",new String[]{pkgname});
			if (cursor.moveToNext()) {
			return cursor.getInt(0);
			}
		}
		return 0;
	}

}
