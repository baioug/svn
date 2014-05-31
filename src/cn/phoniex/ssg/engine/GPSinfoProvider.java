package cn.phoniex.ssg.engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class GPSinfoProvider {

	
	private static Context context;
	private static GPSinfoProvider mGPSinstance;
	private LocationListener myListener;
	private Location mlocation;
	private String locStr = null;
	//私有化构造方法 避免程序构造多个该类的实例
	
	private GPSinfoProvider(){};
	public static synchronized GPSinfoProvider getInstance(Context context)
	{
		
		if (mGPSinstance == null) {
			
			new GPSinfoProvider();
			GPSinfoProvider.context = context;
		}
		
		return mGPSinstance;
	}
	
	
	public String getLoction()
	{
		LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		//manager.getAllProviders();
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setPowerRequirement(Criteria.POWER_HIGH);
		criteria.setSpeedRequired(true);
		criteria.setCostAllowed(true);
		String provider = manager.getBestProvider(criteria, true);
		myListener = new myLocationListener();
		manager.requestLocationUpdates(provider, 60*1000, 50, myListener );
		return locStr;
		
	}
	
	
	private class myLocationListener implements LocationListener
	{

		@Override
		public void onLocationChanged(Location location) {
			//latitude 纬度 longitude 经度
			mlocation = location;
			String   latitude = Double.toString(location.getLatitude());
			String  longitude =Double.toString(location.getLongitude());
			String timestr = Double.toString(location.getTime());
			SharedPreferences sp = context.getSharedPreferences("lostprocfg", Context.MODE_PRIVATE);
			Editor ed = sp.edit();
			ed.putString("latitude", latitude);
			ed.putString("longitude", longitude);
			ed.putString("timestr", timestr);
			ed.commit();
			locStr = "latitude:"+latitude +"\t longitude:"+longitude +"\t timestr:"+timestr;
		}

		@Override
		public void onProviderDisabled(String provider) {
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}
		
		
	}
}
