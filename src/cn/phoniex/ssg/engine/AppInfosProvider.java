package cn.phoniex.ssg.engine;

import java.util.ArrayList;
import java.util.List;

import cn.phoniex.ssg.domain.Appinfos;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class AppInfosProvider {
	private static final String TAG = "AppInfoProvider";
	private Context context;
	private PackageManager packmanager;

	
	public AppInfosProvider(Context context) {
		this.context = context;
		packmanager = context.getPackageManager();
	}

	public List<Appinfos> getAllApps(){
		List<Appinfos> appinfos = new ArrayList<Appinfos>();
		List<PackageInfo> packinfos = packmanager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		for(PackageInfo info :packinfos){
			Appinfos myApp = new Appinfos();
			String packname = info.packageName;
			myApp.setPackageName(packname);
			ApplicationInfo appinfo = info.applicationInfo;
			Drawable icon = appinfo.loadIcon(packmanager);
			myApp.setIcon(icon);
			String appname = appinfo.loadLabel(packmanager).toString();
			myApp.setAppname(appname);
			 if(filterApp(appinfo)){
				 myApp.setSysApp(false);
			 }else{
				 myApp.setSysApp(true);
			 }
			appinfos.add(myApp);
		}
		return appinfos;
	}
	

    public boolean filterApp(ApplicationInfo info) {
        if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            return true;
        } else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            return true;
        }
        return false;
    }
}
