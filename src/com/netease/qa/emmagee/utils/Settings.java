package com.netease.qa.emmagee.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Parameters in Setting Activity
 * 
 * @author yrom
 * 
 */
public final class Settings {

	public static final String KEY_SENDER = "sender";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_RECIPIENTS = "recipients";
	public static final String KEY_SMTP = "smtp";
	public static final String KEY_ISFLOAT = "isfloat";
	public static final String KEY_INTERVAL = "interval";
	public static final String KEY_ROOT = "root";
	public static final String KEY_AUTO_STOP = "autoStop";
	public static final String LOG_TAG = "mylog";
	
	public static String serverIp = "114.215.153.178";
	public static String serverPort = "8088";
	public static final String KEY_SERVER_IP = "serverip";
	public static final String KEY_SERVER_PORT = "serverport";
	
	
	public static SharedPreferences getDefaultSharedPreferences(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

}
