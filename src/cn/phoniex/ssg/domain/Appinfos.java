package cn.phoniex.ssg.domain;

import android.graphics.drawable.Drawable;

public class Appinfos {
	private String Appname ;
	private String PackageName;
	private Drawable icon;
	private boolean isSysApp;
	private boolean bLock;
	private long timeleft;
	

	public boolean isbLock() {
		return bLock;
	}
	public void setbLock(boolean bLock) {
		this.bLock = bLock;
	}
	public long getTimeleft() {
		return timeleft;
	}
	public void setTimeleft(long timeleft) {
		this.timeleft = timeleft;
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
