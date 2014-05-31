package cn.phoniex.ssg.domain;

public class WiFiDetail {
	
	private String SSID;
	private String MAC;
	private String password;
	private int linkspeed;
	private int RSSI;
	// private int ID;
	
	
	public WiFiDetail(String sSID, String mAC, String password, int linkspeed,
			int rSSI) {
		super();
		SSID = sSID;
		MAC = mAC;
		this.password = password;
		this.linkspeed = linkspeed;
		RSSI = rSSI;
	}
	
	public int getLinkspeed() {
		return linkspeed;
	}
	public void setLinkspeed(int linkspeed) {
		this.linkspeed = linkspeed;
	}
	public String getSSID() {
		return SSID;
	}
	public void setSSID(String sSID) {
		SSID = sSID;
	}
	public String getMAC() {
		return MAC;
	}
	public void setMAC(String mAC) {
		MAC = mAC;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getRSSI() {
		return RSSI;
	}
	public void setRSSI(int rSSI) {
		RSSI = rSSI;
	}
	
	

}
