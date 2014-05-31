package cn.phoniex.ssg.engine;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLStreamHandler;

import cn.phoniex.ssg.R;
import cn.phoniex.ssg.domain.Updateinfo;

import android.content.Context;

public class UpdateInfoService {
	//��ȡ��Դ�ļ���ʱ����Ҫ������
	private Context context;

	public UpdateInfoService(Context context) {
		this.context = context;
	}
	
	//��ʽ�ķ�ʽ�׳��쳣 �������߷�����
	public Updateinfo getUpdateinfo() throws Exception
	{
		String  urlstr = context.getResources().getString(R.string.UpdateUrl);
		URL url = new URL(urlstr);
		HttpURLConnection connection =  (HttpURLConnection) url.openConnection();
		connection.setConnectTimeout(5000);
		connection.setRequestMethod("GET");
		InputStream is = connection.getInputStream();
		
		return UpdateInfoParser.getUpdateInfo(is);
	}
	
	
	
}
