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
	
	//�ж��Ƿ�ΪαװAP
	public boolean IsSpoofing(WiFiDetail detail){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		boolean bret = true;
		if (db.isOpen()) {// �ж�SSID�Ƿ����
			Cursor cursor = db.rawQuery("select * from wifiinfo where ssid = ? ",new String[]{detail.getSSID()});
			while(cursor.moveToNext()) {
				
				String mac = cursor.getString(cursor.getColumnIndex("mac"));
				//if (mac.equals(detail.getMAC()+"xxxxx"))//����
				if (mac.equals(detail.getMAC())) {//���mac ��ssid һ�� ���ܲ���α��AP
					return false;
				}
			}
			cursor.close();
			db.close();
			return true;
		}else {
			Toast.makeText(context, "WiFiDetail �����ݿ����", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		
	}
	
	public void add(WiFiDetail detail){
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {// �ж��Ƿ�������ⲿ���� ����߼��в������ж�
			
			Cursor cursor = db.rawQuery("select * from wifiinfo where ssid = ? ",new String[]{detail.getSSID()});
			while(cursor.moveToNext()) {
				String mac = cursor.getString(cursor.getColumnIndex("mac"));
				if (mac.equals(detail.getMAC())) {//mac ��ssid 
					 return;
					}
				}
			}
			//û�ҵ�ƥ���
			db.execSQL("insert into wifiinfo (ssid,password,mac,linkspeed,rssi) values(?,?,?,?,?)",
					new Object[]{detail.getSSID(),detail.getPassword(),detail.getMAC(),detail.getLinkspeed(),detail.getRSSI()});
			
	}
	public WiFiDetail find(String ssid){
		WiFiDetail detail = null;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {// �ж��Ƿ�������ⲿ���� ����߼��в������ж�
			
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
