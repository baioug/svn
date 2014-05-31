package cn.phoniex.ssg.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;
import cn.phoniex.ssg.AntiApSpoofingActivity.wifiDetailAdapter;
import cn.phoniex.ssg.db.WiFiDBHelper;
import cn.phoniex.ssg.domain.WiFiDetail;

public class WiFiDetailDAO {
	
	private WiFiDBHelper dbHelper ;
	private String TAG = "WiFiDetailDAO";
	private Context context;
	private List<WiFiDetail> wiFiDetails;
	
	public WiFiDetailDAO(Context context){
		this.context = context;
		dbHelper = new WiFiDBHelper(context);
		
	}
	
	public List<WiFiDetail> getALL(){
		wiFiDetails =  new ArrayList<WiFiDetail>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from wifiinfo", null);
			while (cursor.moveToNext()) {
				
				String ssid  = cursor.getString(cursor.getColumnIndex("ssid"));
				String password = cursor.getString(cursor.getColumnIndex("password"));
				String mac = cursor.getString(cursor.getColumnIndex("mac"));
				int linkspeed  = cursor.getInt(cursor.getColumnIndex("linkspeed"));
				int rssi  = cursor.getInt(cursor.getColumnIndex("rssi"));
				WiFiDetail detail = new WiFiDetail(ssid, mac, password, linkspeed, rssi);
				wiFiDetails.add(detail);
				
			}
			cursor.close();
			db.close();
			
		}
		
		return wiFiDetails;
	}
	
	//判定是否为伪装AP
	public boolean IsSpoofing(WiFiDetail detail){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		boolean bret = true;
		if (db.isOpen()) {// 判断SSID是否存在
			Cursor cursor = db.rawQuery("select * from wifiinfo where ssid = ? ",new String[]{detail.getSSID()});
			while(cursor.moveToNext()) {
				
				String mac = cursor.getString(cursor.getColumnIndex("mac"));
				//if (mac.equals(detail.getMAC()+"xxxxx"))//测试
				if (mac.equals(detail.getMAC())) {//如果mac 和ssid 一致 可能不是伪造AP
					return false;
				}
			}
			cursor.close();
			db.close();
			return true;
		}else {
			Toast.makeText(context, "WiFiDetail 打开数据库出错", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		
	}
	
	public void add(WiFiDetail detail){
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {// 判断是否存在在外部进行 添加逻辑中不进行判断
			
			Cursor cursor = db.rawQuery("select * from wifiinfo where ssid = ? ",new String[]{detail.getSSID()});
			while(cursor.moveToNext()) {
				String mac = cursor.getString(cursor.getColumnIndex("mac"));
				if (mac.equals(detail.getMAC())) {//mac 和ssid 
					 return;
					}
				}
			}
			//没找到匹配的
			db.execSQL("insert into wifiinfo (ssid,password,mac,linkspeed,rssi) values(?,?,?,?,?)",
					new Object[]{detail.getSSID(),detail.getPassword(),detail.getMAC(),detail.getLinkspeed(),detail.getRSSI()});
			
	}
	public WiFiDetail find(String ssid){
		WiFiDetail detail = null;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {// 判断是否存在在外部进行 添加逻辑中不进行判断
			
			Cursor cursor = db.rawQuery("select * from wifiinfo where ssid = ? ",new String[]{ssid});
			while(cursor.moveToNext()) {
				String mac = cursor.getString(cursor.getColumnIndex("mac"));
				String dbssid  = cursor.getString(cursor.getColumnIndex("ssid"));
				String password = cursor.getString(cursor.getColumnIndex("password"));
				int linkspeed  = cursor.getInt(cursor.getColumnIndex("linkspeed"));
				int rssi  = cursor.getInt(cursor.getColumnIndex("rssi"));
				detail = new WiFiDetail(dbssid, mac, password, linkspeed, rssi);
			}
		}
	return detail;
	}

}
