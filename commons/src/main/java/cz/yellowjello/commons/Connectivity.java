package cz.yellowjello.commons;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;

public class Connectivity {

	public enum Reachability {
		NOT_REACHABLE,
		REACHABLE_WAN,
		REACHABLE_WIFI
	}

	/**
	 * Get simple connectivity info
	 *
	 * @param context Context
	 *
	 * @return Reachability constant
	 */
	public static Reachability getSimpleReachability(final Context context) {
		if (isConnectedMobile(context)) {
			return Reachability.REACHABLE_WAN;
		} else if (isConnectedWifi(context)) {
			return Reachability.REACHABLE_WIFI;
		} else {
			return Reachability.NOT_REACHABLE;
		}
	}

	/**
	 * Get the network info
	 *
	 * @param context Context
	 *
	 * @return Network Info
	 */
	@Nullable
	public static NetworkInfo getNetworkInfo(final Context context) {
		final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo();
	}

	/**
	 * Check if there is any connectivity
	 *
	 * @param context Context
	 *
	 * @return True if connected, false otherwise
	 */
	public static boolean isConnected(final Context context) {
		final NetworkInfo info = getNetworkInfo(context);
		return (info != null && info.isConnected());
	}

	/**
	 * Check if there is any connectivity to a Wifi network
	 *
	 * @param context Context
	 *
	 * @return True if connected through wifi, false otherwise
	 */
	public static boolean isConnectedWifi(final Context context) {
		final NetworkInfo info = getNetworkInfo(context);
		return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
	}

	/**
	 * Returns name of the Wifi router
	 *
	 * @param context Application context
	 *
	 * @return SSID of the wifi router
	 */
	public static String getWifiName(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		return wifiInfo.getSSID().replace("\"", "");
	}

	/**
	 * Check if there is any connectivity to a mobile network
	 *
	 * @param context Context
	 *
	 * @return True if connected through mobile data, false otherwise
	 */
	public static boolean isConnectedMobile(final Context context) {
		final NetworkInfo info = getNetworkInfo(context);
		return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
	}

	/**
	 * Check if there is fast connectivity
	 *
	 * @param context Context
	 *
	 * @return True if there is fast connectivity
	 */
	public static boolean isConnectedFast(final Context context) {
		final NetworkInfo info = getNetworkInfo(context);
		return (info != null && info.isConnected() && isConnectionFast(info.getType(), info.getSubtype()));
	}

	/**
	 * Check if the connection is fast
	 *
	 * @param type Connection type (wifi or mobile)
	 * @param subType Network type
	 *
	 * @return True if connection is fast
	 */
	public static boolean isConnectionFast(final int type, final int subType) {
		if (type == ConnectivityManager.TYPE_WIFI) {
			return true;
		} else if (type == ConnectivityManager.TYPE_MOBILE) {
			switch (subType) {
				case TelephonyManager.NETWORK_TYPE_1xRTT:
					return false; // ~ 50-100 kbps
				case TelephonyManager.NETWORK_TYPE_CDMA:
					return false; // ~ 14-64 kbps
				case TelephonyManager.NETWORK_TYPE_EDGE:
					return false; // ~ 50-100 kbps
				case TelephonyManager.NETWORK_TYPE_EVDO_0:
					return true; // ~ 400-1000 kbps
				case TelephonyManager.NETWORK_TYPE_EVDO_A:
					return true; // ~ 600-1400 kbps
				case TelephonyManager.NETWORK_TYPE_GPRS:
					return false; // ~ 100 kbps
				case TelephonyManager.NETWORK_TYPE_HSDPA:
					return true; // ~ 2-14 Mbps
				case TelephonyManager.NETWORK_TYPE_HSPA:
					return true; // ~ 700-1700 kbps
				case TelephonyManager.NETWORK_TYPE_HSUPA:
					return true; // ~ 1-23 Mbps
				case TelephonyManager.NETWORK_TYPE_UMTS:
					return true; // ~ 400-7000 kbps
				// Unknown
				case TelephonyManager.NETWORK_TYPE_UNKNOWN:
					return false;
				default:
					if (Build.VERSION.SDK_INT >= 13) {
						if (subType == TelephonyManager.NETWORK_TYPE_HSPAP) // API level 13
						{
							return true; // ~ 10-20 Mbps
						}
					}
					if (Build.VERSION.SDK_INT >= 11) {
						if (subType == TelephonyManager.NETWORK_TYPE_EHRPD) // API level 11
						{
							return true; // ~ 1-2 Mbps
						}
						if (subType == TelephonyManager.NETWORK_TYPE_LTE) // API level 11
						{
							return true; // ~ 10+ Mbps
						}
					}
					if (Build.VERSION.SDK_INT >= 9) {
						if (subType == TelephonyManager.NETWORK_TYPE_EVDO_B) // API level 9
						{
							return true; // ~ 5 Mbps
						}
					}
					if (Build.VERSION.SDK_INT >= 8) {
						if (subType == TelephonyManager.NETWORK_TYPE_IDEN) // API level 8
						{
							return false; // ~25 kbps
						}
					}
					return false;
			}
		} else {
			return false;
		}
	}
}