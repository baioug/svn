package cn.phoniex.ssg.test;

import cn.phoniex.ssg.service.BackUpService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.test.ServiceTestCase;

public class testbackupsms extends ServiceTestCase {

//Test run failed: Instrumentation run failed due to 'Process crashed.'
// �鿴logcat ����ȱ�� ��д sms ��Ȩ�� ���Ȩ�޳ɹ�ִ��
	private int iOps = 0;
	private final int OPS_BCK = 0;
	private final int OPS_RES = 1;
	private boolean bbind = false;
	private BackUpService bkservice;
	/*
	 * junit.framework.AssertionFailedError: Class ����.testbackupsms
	 *  has no public constructor TestCase(String name) or TestCase()
	  */
	public testbackupsms() {
		super(BackUpService.class);
	}

	
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
	}



	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}


	
	
	
	public void testbackup()  throws Exception
	{
		
		Intent bkIntent  = new Intent(getContext(),BackUpService.class);
		
		startService(bkIntent);
		IBinder  serviceB = bindService(bkIntent);
		BackUpService bkservice = ((BackUpService.BackUpBinder)serviceB).getService();
		bkservice.BackupSms();
		bkservice.RestoreSms();
		//bindService(bkIntent, conn, Context.BIND_AUTO_CREATE);
		
	}
	

}
