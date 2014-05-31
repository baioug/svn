package cn.phoniex.ssg.test;

import cn.phoniex.ssg.QueryPhoAddrActivity;
import cn.phoniex.ssg.engine.QueryPhoAddrService;
import android.test.AndroidTestCase;

public class testqueryphoaddr extends AndroidTestCase {

	public void testqueryphoaddr()
	{
		String  string = new QueryPhoAddrService().showphoaddr("18612255186",getContext());
		System.out.println(string);
	}

}
