package cn.phoniex.ssg;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class SercurityPermissionActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	Class spClass;
	try {
		//加载asp类
		spClass = getClass().getClassLoader().loadClass("android.widget.AppSecurityPermissions");
		// 获取asp的 构造方法 用来执行其中的 getPermissionView函数(根据系统的设置程序的源代码可知构造函数的两个参数一个是上下文 一个是包名)
		Constructor constructor =  spClass.getConstructor(Context.class,String.class);
		//实例化一个对象
		//Object aspObject = constructor.newInstance(this,"cn.phoniex.ssg");
		// 获取启动该Activity的intent里面传递过来的包名
		String pkgname = getIntent().getStringExtra("packagename");
		Object aspObject = constructor.newInstance(new Object[]{SercurityPermissionActivity.this,pkgname});
		//获取其中的方法   该函数是不需要参数的
		Method aspMethod = spClass.getDeclaredMethod("getPermissionsView", new Class[]{});
		//调用该函数 参数为asp的一个对象  和 该函数需要的参数
		View view = (View) aspMethod.invoke(aspObject, new Object[]{});
		setContentView(view);
				
				
	} catch (Exception e) {
		e.printStackTrace();
		Toast.makeText(SercurityPermissionActivity.this, "该package未找到", 0).show();
	}

	
	}

	
	
}
