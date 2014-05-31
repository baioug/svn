package cn.phoniex.ssg;

import android.app.Application;
import android.app.Notification;

public class MyApplication extends Application {

	private static int notifyId = 0;
	private static Notification notification = null;

	public int getNotifyId() {
		return notifyId;
	}

	public void setNotifyId(int notifyId) {
		this.notifyId = notifyId;
	}

	public Notification getNotification() {
		return notification;
	}

	public void setNotification(Notification notification) {
		this.notification = notification;
	}
	
}
