package cn.phoniex.ssg.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import android.content.Context;

public class DateBaseImportFunc {

	
	private static final String ASSETS_NAME = "phonebook";
	private static String DB_PATH = android.os.Environment.getExternalStorageState();
	private static String dB_NAME = "contact.db";
	private static String ZIP_NAME = "contact.zip";
	
	public static void ImportDateBase(Context context)
	{
		
		
	}
	
	private void copyFile(Context context) throws IOException 
	{
		String FileName = DB_PATH+ZIP_NAME;
		File zipFile = new File(FileName);
		if (!zipFile.exists()) {
			zipFile.createNewFile();
		}
		InputStream is = context.getAssets().open(ASSETS_NAME);
		OutputStream outStream = new FileOutputStream(zipFile);
		byte[] buf = new byte[1024];
		int len = 0;
		while ((len =  is.read(buf)) > 0) {
			outStream.write(buf,0,len);
		}
		outStream.flush();
		is.close();
		outStream.close();
	}
	
	private void UnZipFile(File file, String outpath) throws ZipException, IOException
	{
		File path = new File(outpath);
		if (!path.exists()) {
			path.mkdir();
		}
		//ZipFile zipFile = new ZipFile(file);
		InputStream is = new FileInputStream(file);
		ZipInputStream zipInputStream = new ZipInputStream(is);
		File UnzipFile = new File(DB_PATH+dB_NAME);
		OutputStream outStream  = new FileOutputStream(UnzipFile); 
		
		
		byte[] buf = new byte[1024];
		int len = 0;
		while((len =  is.read(buf)) > 0) {
			outStream.write(buf,0,len);
		}
		outStream.flush();
		is.close();
		outStream.close();
		if (zipInputStream != null) {
			zipInputStream.close();
		}
	
	}
	
	/**
	 * 解压缩一个文件 代码自网络暂未使用
	 * 
	 */
	public static void Unzip(String zipFile, String targetDir) {
		int BUFFER = 4096; // 这里缓冲区我们使用4KB，
		String strEntry; // 保存每个zip的条目名称
		try {
			BufferedOutputStream dest = null; // 缓冲输出流
			FileInputStream fis = new FileInputStream(zipFile);
			ZipInputStream zis = new ZipInputStream(
					new BufferedInputStream(fis));
			ZipEntry entry; // 每个zip条目的实例
			while ((entry = zis.getNextEntry()) != null) {
				try {
					int count;
					byte data[] = new byte[BUFFER];
					strEntry = entry.getName();
					File entryFile = new File(targetDir + strEntry);
					File entryDir = new File(entryFile.getParent());
					if (!entryDir.exists()) {
						entryDir.mkdirs();
					}
					FileOutputStream fos = new FileOutputStream(entryFile);
					dest = new BufferedOutputStream(fos, BUFFER);
					while ((count = zis.read(data, 0, BUFFER)) != -1) {

						dest.write(data, 0, count);
					}
					dest.flush();
					dest.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			zis.close();
		} catch (Exception cwj) {
			cwj.printStackTrace();
		}
	}

	
}
