package cn.phoniex.ssg.viewpager;

import java.util.HashMap;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PhoAddrDAO{
	private SQLiteDatabase db;
	
	public PhoAddrDAO(SQLiteDatabase db){
		this.db = db;
	}
	
	/**获取指定区号的省份和地区*/
	public Map<String,String> queryAeraCode(String number){
		return queryNumber("0", number);
	}

	/**获取指定号码的省份和地区名
	 * <code>select city_id from number_0 limit arg1,arg2.</code>
	 * arg1表示从第几行（行数从零开始）开始，arg2表示查询几行数据.*/
	public Map<String,String> queryNumber(String prefix, String center){
		if (center.isEmpty() || !isTableExists("number_" + prefix))
			return null;
		
		int num = Integer.parseInt(center) - 1;	
		/*<code>select city_id from number_0 limit arg1,arg2.</code>
		 * arg1表示从第几行（行数从零开始）开始，arg2表示查询几行数据.*/
		//limit 1225,1 的意义就是从1225个开始返回一个 该数据库未保存手机号字段和地区代码的对应关系
		// 而是直接以手机号表里面地区代码的位置id为手机中间字段的 比方我的手机号是 1861225xxxx
		// number_186表里面第1225项对应的值就是地区代码
		String sql1 = "select city_id from number_" + prefix + " limit " + num + ",1";//关键字之间空格
		//根据城市id查询到省id
		String sql2 = "select province_id from city where _id = (" + sql1 + ")";
		//根据城市id 省id查询对应的省市名称
		String sql = "select province,city from province,city where _id=("+sql1+")and id=("+sql2+")";
		
		return getCursorResult(sql);
	}

	/*模糊查询 匹配用户输入*/
	public Cursor getPossibleCities(String city){
		if (city.isEmpty())
			return null;
		/* 模糊查询 使用 like 关键字 后面的格式是 '%关键字%' %是通配符  
		 * select * from city where city like '石%'   只顺序匹配
		 * select * from  city where city like '%石%' 匹配中间有石字的选项﻿
		_id	city	province_id	flag
		123	石嘴山	8	2
		128	石嘴山	8	3
		298	石河子/塔城地区	14	2
		314	石河子	14	3
		371	石家庄	17	2
		382	石家庄	17	3
		459	黄石	21	2
		474	黄石	21	3
 */
		String sql = "select _id,city from city where city like '%" + city +"%' and flag = 2";
		return getCursor(sql);
	}
	
	public Cursor getPossibleCountry(String country){
		if (country.isEmpty())
			return null;
		String sql = "select _id,country,country_tw,country_en from country where country like '%" + country + "%'";
		return getCursor(sql);
	}
	
	/**查询城市区号*/
	public Map<String,String> queryCity(String city){
		if (city.isEmpty())
			return null;
		String sql1 = "select _id from city where city ='" + city + "'";
		String sql = "select rowid as rownumber from number_0 where city_id =(" + sql1 + ")";
		return getCursorResult(sql);
	}
	
	/**查询国家的代号 */
	public Map<String,String> queryCountry(String country){
		if (country.isEmpty())
			return null;
		String sql1 = "select _id from country where country  ='" + country + "'";
		String sql = "select rowid as rownumber from number_00 where country_id =(" + sql1 + ")";
		return getCursorResult(sql);
	}
	
	/**返回查询结果*/
	private Map<String, String> getCursorResult(String sql) {
		Cursor cursor = getCursor(sql);
		int col_len = cursor.getColumnCount();
		Map<String, String> map = new HashMap<String, String>();
		
		while (cursor.moveToNext()){
			for (int i = 0; i < col_len; i++){
				String columnName = cursor.getColumnName(i);
				String columnValue = cursor.getString(cursor.getColumnIndex(columnName));
				if (columnValue == null)
					columnValue = "";
				map.put(columnName, columnValue);
			}
		}
		return map;
	}

	private Cursor getCursor(String sql) {
		return  db.rawQuery(sql, null);
	}

	public boolean isTableExists(String tableName){
		boolean result = false;
		if (tableName == null)
			return false;
		Cursor cursor = null;
		try{
			String sql = "select count(*) as c from sqlite_master where type='table' and " +
					"name = '" + tableName.trim() +"' ";
			cursor = db.rawQuery(sql, null);
			if (cursor.moveToNext()){
				int count = cursor.getInt(0);
				if (count > 0)
					result = true;
			}
		}catch(Exception e){
			
		}
		return result;
	}

	public void closeDB(){
		if(db != null){
			db = null;
			db.close();
		}
	}
}