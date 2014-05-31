package cn.phoniex.ssg.domain;

import android.R.string;
import android.graphics.drawable.Drawable;

public class ProcessInfo {
	
	private String appname;
	private String pkgname;
	
	private int pid;
	private Drawable icon;
	private boolean binList;
	private boolean bSysPro;
	private int memSize;
	public String getAppname() {
		return appname;
	}
	public void setAppname(String appname) {
		this.appname = appname;
	}
	public String getPkgname() {
		return pkgname;
	}
	public void setPkgname(String pkgname) {
		this.pkgname = pkgname;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public boolean isBinList() {
		return binList;
	}
	public void setBinList(boolean binList) {
		this.binList = binList;
	}
	public boolean isbSysPro() {
		return bSysPro;
	}
	public void setbSysPro(boolean bSysPro) {
		this.bSysPro = bSysPro;
	}
	public int getmemSize() {
		return memSize;
	}
	public void setmemSize(int memSize) {
		this.memSize = memSize;
	}
	
	
	

}
