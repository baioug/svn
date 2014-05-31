package cn.phoniex.ssg.engine;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.zip.Inflater;

import android.R.string;

public class UploadFile {

	private static final String CHARSET = "utf-8";

	public static boolean UploadFileFuc(File file , String UpUrl)
	{
		String BOUNDARY  = UUID.randomUUID().toString();//随机生成一个唯一标识符作为边界标识
		String PREFIX = "--";
		String LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/from-data";
		
		try {
		
			URL url = new URL(UpUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setReadTimeout(100*1000);
			connection.setConnectTimeout(100*1000);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Charset","utf-8" );//设置编码
			connection.setRequestProperty("connection", "keep-alive");//保持链接
			connection.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="+ BOUNDARY);
			if (connection.getResponseCode() == 200) {
				
			}
			if (file == null) {
				
				return false;
			}
			OutputStream opsStream = connection.getOutputStream();
			DataOutputStream dosStream = new DataOutputStream(opsStream);
			StringBuffer sbBuffer =new StringBuffer();
			sbBuffer.append(PREFIX);
			sbBuffer.append(BOUNDARY);
			sbBuffer.append(LINE_END);
			sbBuffer.append("Content-Disposition: form-data; name=\"img\"; filename=\""
					+ file.getName() + "\"" + LINE_END);
			sbBuffer.append("Content-Type: application/octet-stream; charset="
					+ CHARSET + LINE_END);
			sbBuffer.append(LINE_END);
			dosStream.write(sbBuffer.toString().getBytes());
			InputStream is = new FileInputStream(file);
			byte[] buf = new byte[1024];
			int len = 0;
			while ((len = is.read(buf)) != -1) {
				dosStream.write(buf,0,len);
			}
			is.close();
			dosStream.write(LINE_END.getBytes());
			byte[] by_end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();
			dosStream.write(by_end_data);
			dosStream.flush();
			//获取响应代码 200 为成功
			if (connection.getResponseCode() == 200) {
				
				return true;
			}
//			else {
//				Thread.sleep(100*1000);
//				UploadFileFuc(file, UpUrl);
//			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return false;
		
		
	}
}
