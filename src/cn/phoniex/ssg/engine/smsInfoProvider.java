package cn.phoniex.ssg.engine;

import java.util.ArrayList;
import java.util.List;

import cn.phoniex.ssg.domain.smsBackupInfo;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class smsInfoProvider {

	private Context context;

	public smsInfoProvider(Context context) {
		super();
		this.context = context;
	}


	public  List<smsBackupInfo> getAllSmsInfos()
	{
		List<smsBackupInfo> infos = new ArrayList<smsBackupInfo>();
		
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://sms/");
		Cursor cursor = resolver.query(uri, 
		new String[]{"_id","thread_id","address","date","date_send","read","status","type","body"}, 
				null, null, null);
		while (cursor.moveToNext()) {
			smsBackupInfo info = new smsBackupInfo();
			info.set_id(cursor.getString(0));
			info.setThread_id(cursor.getString(1));
			info.setAddress(cursor.getString(2));
			info.setDate(cursor.getString(3));
			info.setDate_send(cursor.getString(4));
			info.setRead(cursor.getString(5));
			info.setStatus(cursor.getString(6));
			info.setType(cursor.getString(7));
			info.setBody(cursor.getString(8));
			infos.add(info);
			info = null;
			
		}
		cursor.close();
		return infos;
		
		
	}
	
}
