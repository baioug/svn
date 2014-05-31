package cn.phoniex.ssg.domain;

import android.graphics.drawable.Drawable;

public class InternetTrafficinfos {
	private String Appname ;
	private String PackageName;
	private Drawable icon;
	private boolean isSysApp;
	private int uid;

	
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getAppname() {
		return Appname;
	}
	public void setAppname(String appname) {
		Appname = appname;
	}
	public String getPackageName() {
		return PackageName;
	}
	public void setPackageName(String packageName) {
		PackageName = packageName;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public boolean isSysApp() {
		return isSysApp;
	}
	public void setSysApp(boolean isSysApp) {
		this.isSysApp = isSysApp;
	}
	
	

}
