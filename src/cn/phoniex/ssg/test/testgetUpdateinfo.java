package cn.phoniex.ssg.test;

import cn.phoniex.ssg.domain.Updateinfo;
import cn.phoniex.ssg.engine.UpdateInfoService;
import android.test.AndroidTestCase;

public class testgetUpdateinfo extends AndroidTestCase {

	public testgetUpdateinfo()
	{
		UpdateInfoService upser = new UpdateInfoService(getContext());
		try {
			Updateinfo info = upser.getUpdateinfo();
			assertEquals("http://localhost:8080/latest.apk", info.getApkUrlString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
