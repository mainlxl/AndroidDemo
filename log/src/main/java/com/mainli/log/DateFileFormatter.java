package com.mainli.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by pqpo on 2017/11/24.
 */
public class DateFileFormatter {
    private SimpleDateFormat simpleDateFormat = null;
    private Date date = new Date();
    private String lastDataFormated = null;
    private StringBuffer mStringBuffer;
    private int mTimeLength = 0;

    public DateFileFormatter() {
        this("yyyy:MM:dd HH:mm:ss");
    }

    public DateFileFormatter(String pattern) {
        simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        mStringBuffer = new StringBuffer();
    }

    public synchronized String format(int logLevel, String tag, String msg) {
        if ((System.currentTimeMillis() - date.getTime()) > 1000 || lastDataFormated == null) {
            date.setTime(System.currentTimeMillis());
            lastDataFormated = simpleDateFormat.format(date);
            resetTimePrefix();
            return formatString(logLevel, tag, msg);
        }
        return formatString(logLevel, tag, msg);
    }

    private void resetTimePrefix() {
        if (mStringBuffer.length() > 0) {
            mStringBuffer.delete(0, mStringBuffer.length());
        }
        mTimeLength = mStringBuffer.append(lastDataFormated).append(' ').length();
    }

    private String formatString(int logLevel, String tag, String msg) {
        if (mStringBuffer.length() > mTimeLength) {
            mStringBuffer.delete(mTimeLength, mStringBuffer.length());
        }
        return mStringBuffer.append(Level.getShortLevelName(logLevel)).append('/').append(tag).append(": ").append(msg).append('\n').toString();
    }
}
