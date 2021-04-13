package com.mainli.log;

import android.util.Log;

public class Level {


    public static String getLevelName(int logLevel) {
        String levelName;
        switch (logLevel) {
            case  Log.VERBOSE:
                levelName = "VERBOSE";
                break;
            case  Log.DEBUG:
                levelName = "DEBUG";
                break;
            case  Log.INFO:
                levelName = "INFO";
                break;
            case  Log.WARN:
                levelName = "WARN";
                break;
            case  Log.ERROR:
                levelName = "ERROR";
                break;
            default:
                if (logLevel <  Log.VERBOSE) {
                    levelName = "VERBOSE-" + ( Log.VERBOSE - logLevel);
                } else {
                    levelName = "ERROR+" + (logLevel -  Log.ERROR);
                }
                break;
        }
        return levelName;
    }

    public static String getShortLevelName(int logLevel) {
        String levelName;
        switch (logLevel) {
            case  Log.VERBOSE:
                levelName = "V";
                break;
            case  Log.DEBUG:
                levelName = "D";
                break;
            case  Log.INFO:
                levelName = "I";
                break;
            case  Log.WARN:
                levelName = "W";
                break;
            case  Log.ERROR:
                levelName = "E";
                break;
            default:
                if (logLevel <  Log.VERBOSE) {
                    levelName = "V-" + ( Log.VERBOSE - logLevel);
                } else {
                    levelName = "E+" + (logLevel -  Log.ERROR);
                }
                break;
        }
        return levelName;
    }

}