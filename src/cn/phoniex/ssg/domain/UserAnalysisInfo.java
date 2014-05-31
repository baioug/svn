package cn.phoniex.ssg.domain;

import android.R.integer;
import android.graphics.drawable.Drawable;

public class UserAnalysisInfo {
	
	private String Appname ;
	private String PackageName;
	private Drawable icon;
	private int icut;
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
	public int getIcut() {
		return icut;
	}
	public void setIcut(int icut) {
		this.icut = icut;
	}
	
	
	

}
