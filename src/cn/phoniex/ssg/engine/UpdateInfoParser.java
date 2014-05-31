package cn.phoniex.ssg.engine;

import java.io.InputStream;
import java.text.BreakIterator;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.R.integer;
import android.util.Xml;

import cn.phoniex.ssg.domain.Updateinfo;

public class UpdateInfoParser {

	public static Updateinfo getUpdateInfo(InputStream is) throws Exception
	{
		XmlPullParser xmlParser  = Xml.newPullParser();
		Updateinfo info = new Updateinfo();
		xmlParser.setInput(is,"utf-8");
		//���ص�ǰ�ڵ������ �ǿ�ʼ�ڵ㻹�ǽ����ڵ� 
		int type = xmlParser.getEventType();
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type)
			{
				case XmlPullParser.START_TAG://����ǽڵ㿪ʼ�ı�־
					if ("description".equals(xmlParser.getName()))
					{//����ڵ���Ϊ������ ��ô��ֵ�� info����Ķ�Ӧ��
						info.setDescriptionStr(xmlParser.nextText());
						
					} else if("apkurl".equals(xmlParser.getName()))
					{
						info.setApkUrlString(xmlParser.nextText());
					}
					else if ("version".equals(xmlParser.getName())) {
						info.setVerStr(xmlParser.nextText());
					}
				break;
			}
			//�ƶ�����һ���ڵ�
				type = xmlParser.next();
		}
				
		
		return info;
		
	}
}
