package cn.phoniex.ssg.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.impl.client.TunnelRefusedException;
import org.w3c.dom.ls.LSResourceResolver;

import cn.phoniex.ssg.db.BlackListDBHelper;
import android.R.string;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BlackListDAO {

	private Context context;
	private BlackListDBHelper blackdbhelper;
	private SQLiteDatabase blackdb;
	public BlackListDAO(Context context) {
		this.context = context;
		blackdbhelper = new BlackListDBHelper(context);
		
	}
	
	public boolean addNo(String number)
	{
		blackdb  = blackdbhelper.getWritableDatabase();
		boolean bRet = false;
		if (blackdb.isOpen()) {
			ContentValues values = new ContentValues();
			values.put("number", number);
			long lret = blackdb.insert("blacklist", "number", values);
			//blackdb.execSQL("insert into blacklist  (number) values (?)",new Object[]{number});
			if (lret != -1) {
				bRet = true;
			}else {
				System.err.println("addNo err");
			}
			blackdb.close();
		}
		//bRet = true;
		return bRet;
	}
	
	public boolean findNo(String number)
	{
		boolean bRet = false;
		blackdb  = blackdbhelper.getReadableDatabase();
		if (blackdb.isOpen()) {
			Cursor cursor =  blackdb.rawQuery("select  number from blacklist where number = ?", new String[]{number});
			if(cursor.moveToNext())
			{
				bRet =  true;
			}
			cursor.close();
			blackdb.close();
		}
		return bRet;
	}


	public boolean delNo(String number)
	{
		blackdb  = blackdbhelper.getWritableDatabase();
		boolean bRet = false;
		if (blackdb.isOpen()) {
			int iret = blackdb.delete("blacklist", "number = ?", new String[]{number});
			//删除一项 delete from blacklist where number = '18612255186'
			//blackdb.execSQL("delete from blacklist where number ="+number);
			// 删除多项 delete from blacklist where _id in (6,7,8,9,10,12,11,13)
			// 非主键也照样可以删除呢 delete from blacklist where number in (138355322555)
			if (iret != 0) {
				bRet = true;
			}
			blackdb.close();
		}
		
		return bRet;
	}
	/**
	 * 批量添加
	 * @param 需要插入的号码列表
	 * @return 返回失败的条目总数
	 */
	public int  addNos(ArrayList<String> numbers)
	{
		int iRet = 0;
		boolean bcircu = true;
		int icut = numbers.size();
		for (int i = 0; i < icut; i++) {
			bcircu = addNo(numbers.get(i));
			if (bcircu == false) {
				iRet++;
			}
		}
		return iRet;
		
	}
	
	public int delNos(ArrayList<String> numbers)
	{
		boolean bret = true;
		int iRet = 0;
		int icut = numbers.size();
		for (int i = 0; i < icut; i++) {
			bret = delNo(numbers.get(i));
			if (bret) {
				iRet++;
			}
			
		}
		return iRet;
	}
	public ArrayList<String> getallNo()
	{
		blackdb  = blackdbhelper.getReadableDatabase();
		String tmpstr;
		ArrayList<String> numbers = new ArrayList<String>();
		if (blackdb.isOpen()) {
			Cursor cursor = blackdb.rawQuery("select number from blacklist ", null);
			
			while (cursor.moveToNext()) {
				tmpstr = cursor.getString(cursor.getColumnIndex("number"));
				numbers.add(tmpstr);
				
			}
			cursor.close();
			blackdb.close();
		}
		return numbers;
	}
	public void DelMore(String... numbers )
	{
		if (numbers.length > 0) {
			StringBuffer sb = new StringBuffer();
			sb.append('(');
			for (int i = 0; i < numbers.length; i++) {
				sb.append('?').append(',');
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append(')');
			blackdb = blackdbhelper.getWritableDatabase();
			if (blackdb.isOpen()) {
				blackdb.execSQL("delete from blacklist where number in"+sb,(Object[])  numbers);
			}	
		}
	
	}
	
	
	public List<String> getFromTo(int start , int end )
	{
		int icut = end  - start;
		if (icut >1) {
			List<String> lstr = new ArrayList<String>();
			blackdb = blackdbhelper.getReadableDatabase();
			if (blackdb.isOpen()) {
				// limit 是用来限制从 第几个开始显示多少个
				//blackdb.execSQL("select number from blacklist limit ?,?", new Object[]{start,icut});
				Cursor cursor = blackdb.rawQuery("select number from blacklist limit ?", new String[]{String.valueOf(start),String.valueOf(icut)});
				while(cursor.moveToNext()) {
					String tmp =cursor.getString(cursor.getColumnIndex("number"));
					lstr.add(tmp);
					tmp = null;
				}
				if (lstr.size() != 0) {
					return lstr;
				}
			}
			blackdb.close();
		}

		return null;
		
	}
	
	public long getcount()
	{
		blackdb = blackdbhelper.getReadableDatabase();
		if (blackdb.isOpen()) {
			//select count(_id) from blacklist" 
			//blackdb.execSQL 这个函数是没有返回值的
			Cursor cursor = blackdb.rawQuery("select count(number) from blacklist ", null);
			if (cursor.moveToNext()) {
				return cursor.getLong(0);
			}
		}
		
		return 0;
		
	}
	
	public void RemoveAll()
	{
		blackdb = blackdbhelper.getWritableDatabase();
		if (blackdb.isOpen()) {
			blackdb.execSQL("delete from blacklist");
		}
		
	}
	
	public boolean updateNo(String oldone ,String newone)
	{
		blackdb  = blackdbhelper.getWritableDatabase();
		boolean bRet = false;
		ContentValues values = new ContentValues();
		values.put("number", newone);
		if (blackdb.isOpen()) {
			int iret =blackdb.update("blacklist", values, "where number =?", new String[]{oldone});
			// update blacklist set number = '1345' where number = '123'
			//blackdb.execSQL("upate blacklist set number = ? where number = ?",new Object[]{newone ,oldone});
			if (iret != 0) {
				bRet =true;
			}
			blackdb.close();
		}
		return bRet;
	}
	
}


