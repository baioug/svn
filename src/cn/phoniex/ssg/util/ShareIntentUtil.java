package cn.phoniex.ssg.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.os.Parcelable;
import android.widget.Toast;

public class ShareIntentUtil {

	/**
	 * try {
	    startActivity(chooserIntent);
      } catch (android.content.ActivityNotFoundException e) {
       Toast.makeText(context, "Can't find share component to share", Toast.LENGTH_SHORT).show();
   }
	 * @param context
	 * @return
	 */
	public static Intent filterChooser(Context context )
	{
		String contentDetails = "";
	    String contentBrief = "";
	    String shareUrl = "";
	    Intent it = new Intent(Intent.ACTION_SEND);
	    it.setType("text/plain");
	    List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(it, 0);
	    if (!resInfo.isEmpty()) {
	        List<Intent> targetedShareIntents = new ArrayList<Intent>();
	        for (ResolveInfo info : resInfo) {
	            Intent targeted = new Intent(Intent.ACTION_SEND);
	            targeted.setType("text/plain");
	            ActivityInfo activityInfo = info.activityInfo;
	            
	            // judgments : activityInfo.packageName, activityInfo.name, etc.
	            if (activityInfo.packageName.contains("bluetooth") || activityInfo.name.contains("bluetooth")) {
	                continue;
	            }
	            if (activityInfo.packageName.contains("gm") || activityInfo.name.contains("mail")) {
	                targeted.putExtra(Intent.EXTRA_TEXT, contentDetails);
	            } else if (activityInfo.packageName.contains("zxing")) {
	                targeted.putExtra(Intent.EXTRA_TEXT, shareUrl);
	            } else {
	                targeted.putExtra(Intent.EXTRA_TEXT, contentBrief);
	            }
	            targeted.setPackage(activityInfo.packageName);
	            targetedShareIntents.add(targeted);
	        }

	        Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "Select app to share");
	        if (chooserIntent == null) {
	            return null;
	        }

	        // A Parcelable[] of Intent or LabeledIntent objects as set with
	        // putExtra(String, Parcelable[]) of additional activities to place
	        // a the front of the list of choices, when shown to the user with a
	        // ACTION_CHOOSER.
	        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[] {}));
	        return chooserIntent;

	    }
	    return null;
	
	}
		
	}

