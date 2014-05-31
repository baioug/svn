package cn.phoniex.ssg.engine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.R.integer;
import android.app.ProgressDialog;

public class downloadFile {

	public static File getFile(String urlStr, String path, ProgressDialog pd) throws Exception
	{
		URL url = new URL(urlStr);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setConnectTimeout(5000);
		connection.setRequestMethod("GET");
		if (connection.getResponseCode() == 200) {
			int allsize = connection.getContentLength();//获取总长度
			pd.setMax(allsize);
			InputStream  is = connection.getInputStream();
			File file = new File(path);
			FileOutputStream Fos = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len = 0;
			int currentlen = 0;
			while ((len = is.read(buf)) != -1) {
				Fos.write(buf);
				currentlen+=len;
				pd.setProgress(currentlen);
			}
			Fos.flush();
			Fos.close();
			is.close();
			return file;
		}
		return null;
		
	}
}
