package com.mainli.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public final class ManifestUtils {
    private final static String TAG = "ManifestUtils";

    private ManifestUtils() {
    }

    /**
     * 返回AndroidManifest.xml中注册的Activity的class
     *
     * @param context     环境
     * @param packageName 包名
     * @param excludeList 排除class列表
     * @return
     */
    public final static ArrayList<ActivityItem> getActivitiesClass(Context context, String packageName, List<Class> excludeList) {

        ArrayList<ActivityItem> returnClassList = new ArrayList<ActivityItem>();
        try {
            //Get all activity classes in the AndroidManifest.xml
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            if (packageInfo.activities != null) {
//                Log.d(TAG, "Found " + packageInfo.activities.length + " activity in the AndrodiManifest.xml");
                for (ActivityInfo ai : packageInfo.activities) {
                    Class c;
                    try {
                        c = Class.forName(ai.name);
                        if (Activity.class.isAssignableFrom(c) && !excludeList.contains(c)) {
                            returnClassList.add(new ActivityItem(ai.loadLabel(packageManager), c));
//                            Log.d(TAG, ai.name + "...OK");
                        }
                    } catch (ClassNotFoundException e) {
                        Log.d(TAG, "Class Not Found:" + ai.name);
                    }
                }
//                Log.d(TAG, "Filter out, left " + returnClassList.size() + " activity," + Arrays.toString(returnClassList.toArray()));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return returnClassList;
    }

    public static class ActivityItem {
        CharSequence name;
        Class<? extends Activity> clazz;

        public ActivityItem(CharSequence name, Class<? extends Activity> clazz) {
            this.name = name;
            this.clazz = clazz;
        }

        public CharSequence getName() {
            return name;
        }

        public void setName(CharSequence name) {
            this.name = name;
        }

        public Class<? extends Activity> getClazz() {
            return clazz;
        }

        public void setClazz(Class<? extends Activity> clazz) {
            this.clazz = clazz;
        }
    }
}
