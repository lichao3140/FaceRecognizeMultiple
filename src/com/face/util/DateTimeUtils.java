package com.face.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtils {
    public final static String DATETIME_FORMAT_ONE = "yyyy-MM-dd HH:mm:ss";
    
	public static String parseDataTimeToFormatString(long datetime) {
    	SimpleDateFormat format = new SimpleDateFormat(DATETIME_FORMAT_ONE, Locale.getDefault());
    	return format.format(new Date(datetime));
    }
	
	public static String parseDataTimeToFormatString(Date datetime) {
    	SimpleDateFormat format = new SimpleDateFormat(DATETIME_FORMAT_ONE, Locale.getDefault());
    	return format.format(datetime);
    }
}
