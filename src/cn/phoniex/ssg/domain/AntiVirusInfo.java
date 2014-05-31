package cn.phoniex.ssg.domain;

import java.io.Serializable;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class AntiVirusInfo  implements Parcelable{
	
	/**
	 * Parcel类:http://developer.android.com/reference/android/os/Parcel.html <br>
	 * 封装数据的容器，封装后的数据可以通过Intent或IPC传递 <br>
	 * 
	 * Parcelable接口：http://developer.android.com/reference/android/os/Parcelable.html <br>
	 * 自定义类继承该接口后，其实例化后能够被写入Parcel或从Parcel中恢复。 <br>
	 * 
	 * 如果某个类实现了这个接口，那么它的对象实例可以写入到 Parcel 中，并且能够从中恢复，
	 * 并且这个类必须要有一个 static 的 field ，并且名称要为 CREATOR ，这个 field 是某个实现了 Parcelable.Creator 接口的类的对象实例。
	 */
	
	public String pkgName;
	public String appName;
	public int type;
	//public Drawable icon;// 貌似这货不能实现序列化接口
	
	//静态的Parcelable.Creator接口
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
