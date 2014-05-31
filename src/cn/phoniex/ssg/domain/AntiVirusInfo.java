package cn.phoniex.ssg.domain;

import java.io.Serializable;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class AntiVirusInfo  implements Parcelable{
	
	/**
	 * Parcel��:http://developer.android.com/reference/android/os/Parcel.html <br>
	 * ��װ���ݵ���������װ������ݿ���ͨ��Intent��IPC���� <br>
	 * 
	 * Parcelable�ӿڣ�http://developer.android.com/reference/android/os/Parcelable.html <br>
	 * �Զ�����̳иýӿں���ʵ�������ܹ���д��Parcel���Parcel�лָ��� <br>
	 * 
	 * ���ĳ����ʵ��������ӿڣ���ô���Ķ���ʵ������д�뵽 Parcel �У������ܹ����лָ���
	 * ������������Ҫ��һ�� static �� field ����������ҪΪ CREATOR ����� field ��ĳ��ʵ���� Parcelable.Creator �ӿڵ���Ķ���ʵ����
	 */
	
	public String pkgName;
	public String appName;
	public int type;
	//public Drawable icon;// ò���������ʵ�����л��ӿ�
	
	//��̬��Parcelable.Creator�ӿ�
		public static final Parcelable.Creator<AntiVirusInfo> CREATOR = new Creator<AntiVirusInfo>() {

			@Override
			public AntiVirusInfo createFromParcel(Parcel source) {
				AntiVirusInfo info = new AntiVirusInfo();
				info.pkgName = source.readString();
				info.appName = source.readString();
				info.type = source.readInt();
				return info;
			}

			@Override
			public AntiVirusInfo[] newArray(int size) {
				// TODO Auto-generated method stub
				return new AntiVirusInfo[size];
			}
			
			
		};
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(pkgName);
		dest.writeString(appName);
		dest.writeInt(type);
	}
	
	
	
	
}
