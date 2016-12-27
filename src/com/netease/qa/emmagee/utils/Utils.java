package com.netease.qa.emmagee.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class Utils {
	// Device ID
	private static String s_deviceID = null;

	// DeviceID白名单
	private static final String[] DEVICEID_WHITE_LIST = { "TestWeb", "111111111111111", "11111111111111", "Unknown", "02:00:00:00:00:00", "w1t9hFVX6Dai3jCCUcbI4E8EbwavBHfrSUHgcozHm5k=", "giaLUIEbCJeuw4Qc/m4qQ1uFPFgA82Cga/5KzLQS24g=", "000000000000000", "0", "00000000000000", "0000000000000000", "00000000", "111111111111119", "358888888888886" };

	public static String getNetworkType(Context context){
		ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
		return networkInfo.getTypeName();
	}
	
	public static String getNetworkName(Context context){
		String name = "";
		if(getNetworkType(context).equals("WIFI")){
			WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifiMgr.getConnectionInfo();
			name = info.getSSID().replace("\"", "");
		}else{
			ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
			name = networkInfo.getExtraInfo();
		}
		
		return name;
	}

	public static void deletefile(String filepath){
		File file = new File(filepath);
		if(file.exists()){
			file.delete();
		}
	}
	
	/**
	 * 
	 * @Title: getDeviceID @Description:
	 * 获取手机唯一标示(根据系统DeviceID,AndroidID,MACAddress生成.为了兼容老版本的数据分析
	 * ,格式为:OldDeviceID;NewDeviceID) @param context @return String @throws
	 */
	public static final String getDeviceID(Context context) {
		// Device ID
		String oldDeviceID = null;
		String newDeviceID = null;
		String deviceId = null;
		String androidId = null;
		String macAddress = null;
		// 无自定义deviceid，正常流程
		try {

			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

			// Device ID
			deviceId = ("" + tm.getDeviceId()).trim();
			// Android ID
			androidId = ("" + Secure.getString(context.getContentResolver(), Secure.ANDROID_ID)).trim();
			// MAC Address
			macAddress = getMacAddress(context);

			// --------旧版本获取DeviceID的算法 Start------------
			if (TextUtils.isEmpty(deviceId)) {
				oldDeviceID = "" + androidId;
			} else {
				oldDeviceID = deviceId;
			}
			// ---------旧版本获取DeviceID的算法 End --------------

			// ---------新版本获取DeviecID的算法 Start-------------
			if (!TextUtils.isEmpty(oldDeviceID) && !Utils.isDeviceIDInWhiteList(oldDeviceID)) {
				newDeviceID = oldDeviceID;
			}

			if (TextUtils.isEmpty(oldDeviceID) && !TextUtils.isEmpty(macAddress)) {
				newDeviceID = macAddress;
			}

			if (isEmpty(newDeviceID) || Utils.isDeviceIDInWhiteList(newDeviceID)) {
				newDeviceID = UUID.randomUUID().toString();
			}
			// ---------新版本获取DeviceID的算法 End --------------

			// 打上艺龙标记
			newDeviceID = Utils.addElongMark2DeviceID(newDeviceID);

			// 把新老算法生成的DeviceID拼凑成一个新的APP DeviceID
			s_deviceID = ("" + oldDeviceID).concat("||").concat(newDeviceID);

		} catch (Exception e) {
			String uuid = UUID.randomUUID().toString();
			s_deviceID = uuid.concat("||").concat(Utils.addElongMark2DeviceID(uuid));

		}

		return s_deviceID;
	}

	/**
	 * 
	 * @Title: addElongMark2DeviceID
	 * @Description: DeviceID加上艺龙标示(索引为4,7,10,13的字符依次被替换为E109)
	 * @param deviceID
	 * @return String
	 */
	private static final String addElongMark2DeviceID(String deviceID) {

		// 如果deviceID不满15位则补全为15位
		if (TextUtils.isEmpty(deviceID) || deviceID.length() < 15) {
			for (int i = deviceID.length(); i < 15; i++) {
				deviceID = deviceID + "0";
			}
		}
		deviceID = deviceID.substring(0, 4) + "E" + deviceID.substring(5, 7) + "1" + deviceID.substring(8, 10) + "0" + deviceID.substring(11, 13) + "9" + deviceID.substring(14);
		return deviceID;
	}

	/**
	 * 
	 * @Title: isDeviceInWhiteList
	 * @Description: 判断DeviceID是否在白名单内
	 * @param deviceID
	 * @return boolean
	 */
	private static boolean isDeviceIDInWhiteList(String deviceID) {
		for (String item : DEVICEID_WHITE_LIST) {
			if (item.equals(deviceID)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @Title: getMacAddress @Description: 获取手机Mac地址 @param context @return
	 * String @throws
	 */
	private final static String getMacAddress(Context context) {

		String macAddress = "";
		try {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
				WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				WifiInfo info = wifi.getConnectionInfo();
				macAddress = info.getMacAddress();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return macAddress;

	}

	/** 压缩单个文件*/
	public static void ZipFile(String filepath ,String zippath) {
		try {
			// new a file input stream
			FileInputStream fis = new FileInputStream(filepath);
			BufferedInputStream bis = new BufferedInputStream(fis);
			ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zippath));
			BufferedOutputStream bos = new BufferedOutputStream(zos);

			// set the file name in the .zip file
			zos.putNextEntry(new ZipEntry("elong_qa_network.log"));

			byte[] b = new byte[1024];
			while (true) {
				int len = bis.read(b);
				if (len == -1)
					break;
				bos.write(b, 0, len);
			}
			fis.close();
			zos.close();
			bis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean isEmpty(String s) {
		return s == null || s.length() == 0 || s.trim().equals("") || s.trim().equals("null");
	}


}
