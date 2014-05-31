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
		//返回当前节点的类型 是开始节点还是结束节点 
		int type = xmlParser.getEventType();
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type)
			{
				case XmlPullParser.START_TAG://如果是节点开始的标志
					if ("description".equals(xmlParser.getName()))
					{//如果节点名为描述符 那么赋值给 info里面的对应项
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
			//移动到下一个节点
				type = xmlParser.next();
		}
				
		
		return info;
		
	}
}
