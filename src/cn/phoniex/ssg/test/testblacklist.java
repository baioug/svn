package cn.phoniex.ssg.test;

import cn.phoniex.ssg.dao.BlackListDAO;
import android.R.bool;
import android.test.AndroidTestCase;

public class testblacklist extends AndroidTestCase {

	public void testadd() throws Exception
	{
		boolean bRet = false;
		BlackListDAO  blDAO = new BlackListDAO(getContext());
		bRet = blDAO.addNo("18612255186");
		boolean bRet1 = blDAO.addNo("1357039670");
		if (bRet && bRet1) {
			System.out.println("Add successful!");
		}else {
			System.out.println("add err");
		}
	}
	public void testfind() throws Exception
	{
		boolean bRet = false;
		BlackListDAO dao = new BlackListDAO(getContext());
		bRet = dao.findNo("1357039670");
		if (bRet) {
			System.out.println("find Successful");
		}
		else {
			System.out.println("find err");
		}
		
	}
	public void testdel() throws Exception
	{
		boolean bRet = false;
		BlackListDAO dao = new BlackListDAO(getContext());
		bRet = dao.delNo("18612255186");
		if (bRet) {
			System.out.println("del Successful");
		}
		
	}
}



