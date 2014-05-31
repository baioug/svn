package cn.phoniex.ssg.engine;

import java.util.Map;

import cn.phoniex.ssg.dao.PhoneaddrDAO;
import cn.phoniex.ssg.viewpager.AssetsDatabaseManager;
import cn.phoniex.ssg.viewpager.PhoAddrDAO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class QueryPhoAddrService {

	
	public  PhoAddrDAO dao;
	private  SQLiteDatabase sqLiteDb;
	
	public  String getPhoNoLoc(String phostr)
	{
		String phofilter,telfilter ;
	 sqLiteDb = PhoneaddrDAO.OpenPhoAddrDB("phoneaddr.db");
		switch (phostr.trim().length()) {
		case 11://手机号 或者 4+7 电话 3+8 电话
			phofilter = "[1][3458]\\d{9}";
			telfilter ="\\d{11}";
			if (phostr.matches(phofilter)) {
				System.out.println("you number"+phostr+" matched phone");
				if (sqLiteDb.isOpen()) {
					Cursor codecursor = sqLiteDb.rawQuery("select outkey from numinfo where mobileprefix=?", new String[]{phostr.substring(0, 7)});
					if (codecursor.moveToNext()) {
						String code = codecursor.getString(0);
						if (code.trim().length()!=0) {
							//Cursor locCursor = sqLiteDb.rawQuery("", )
						}
					}
				}
			}
			else if (phostr.matches(telfilter)) {
				System.out.println("you number"+phostr+" matched telpho");
			}
			break;
		case 3://区号
		case 4:
		case 7:
		case 8:
		case 10://区号+电话

			
			break;

		default:
			break;
		}
		
		
		return null;
		
	}
	
	
	public  String showphoaddr(String phostr,Context context)
	{
		String retStr = "Nothing Found";
		Map<String,String> map = null;
		AssetsDatabaseManager.initManager(context.getApplicationContext());
		AssetsDatabaseManager mg = AssetsDatabaseManager.getAssetsDatabaseManager();
		sqLiteDb = mg.getDatabase("number_location.zip");
		dao = new PhoAddrDAO(sqLiteDb);
		
		
		switch (phostr.trim().length()) {
		case 11://手机号 或者 4+7 电话 3+8 电话
			String phofilter = "[1][3458]\\d{9}";
			String telfilter ="[0]\\d{10}";
			if (phostr.matches(phofilter)) {
				System.out.println("you number"+phostr+" matched phone");
				if (sqLiteDb.isOpen()) {
					
					String prefix, center;

						prefix = getMobilePrefix(phostr);
						center = getCenterNumber(phostr);
						map = dao.queryNumber(prefix, center);
				}
			}
			else if (phostr.matches(telfilter)) {
				System.out.println("you number"+phostr+" matched telpho");
				
				String prefix = getAreaCodePrefix(phostr);
				map = dao.queryAeraCode(prefix);
			}
			break;
		case 10://区号+电话
			String telfilter2 ="[0]\\d{9}";
			if (phostr.matches(telfilter2)) {
				String prefix = getAreaCodePrefix(phostr);
				map = dao.queryAeraCode(prefix);
			}
			break;
			
			
		case 7:retStr = "local number!";
		break;
		case 8:retStr = "local number!";
			break;
		default:
			break;
		}
		if (map != null) {
			String province = map.get("province");
			String city = map.get("city");
			retStr = province +"   " + city;
		}
		
		return retStr;
		
	}
	
	
	public String getAreaCodePrefix(String number){
		if (number.charAt(1) == '1' || number.charAt(1) == '2')
			return number.substring(1,3);
		return number.substring(1,4);
	}
	
	public  String getMobilePrefix(String number){
		return number.substring(0,3);
	}
	
	public  String getCenterNumber(String number){
		return number.substring(3,7);
	}
}
