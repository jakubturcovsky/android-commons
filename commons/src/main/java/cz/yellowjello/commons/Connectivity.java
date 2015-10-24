package cz.yellowjello.commons;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;

import cz.master.annie.app.Const;

public class Connectivity {

	public static final byte NOT_REACHABLE = 0;
	public static final byte REACHABLE_WAN = 1;
	public static final byte REACHABLE_WIFI = 2;

	/**
	 * Get simple connectivity info
	 *
	 * @param context Context
	 *
	 * @return Reachability constant
	 */
	public static byte getSimpleReachability(final Context context) {
		if (isConnectedMobile(context)) {
			return REACHABLE_WAN;
		} else if (isConnectedWifi(context)) {
			return REACHABLE_WIFI;
		} else {
			return NOT_REACHABLE;
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
		final NetworkInfo info = cz.master.annie.utils.Connectivity.getNetworkInfo(context);
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
		final NetworkInfo info = cz.master.annie.utils.Connectivity.getNetworkInfo(context);
		return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
	}

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
		final NetworkInfo info = cz.master.annie.utils.Connectivity.getNetworkInfo(context);
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
		final NetworkInfo info = cz.master.annie.utils.Connectivity.getNetworkInfo(context);
		return (info != null && info.isConnected() && cz.master.annie.utils.Connectivity.isConnectionFast(info.getType(), info.getSubtype()));
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

	/**
	 * Get actual name of connection
	 *
	 * @param context Context
	 *
	 * @return Connection name
	 */
	@Nullable
	@Const.ConnectivityType
	public static String getConnectionName(final Context context) {
		final NetworkInfo info = cz.master.annie.utils.Connectivity.getNetworkInfo(context);
		if (info == null) {
			return null;
		}
		final int type = info.getType();
		final int subType = info.getSubtype();
		if (type == ConnectivityManager.TYPE_WIFI) {
			return Const.CONN_TYPE_WIFI;
		} else if (type == ConnectivityManager.TYPE_MOBILE) {
			switch (subType) {
				case TelephonyManager.NETWORK_TYPE_1xRTT:
					return Const.CONN_TYPE_1XRTT; // ~ 50-100 kbps
				case TelephonyManager.NETWORK_TYPE_CDMA:
					return Const.CONN_TYPE_CDMA; // ~ 14-64 kbps
				case TelephonyManager.NETWORK_TYPE_EDGE:
					return Const.CONN_TYPE_EDGE; // ~ 50-100 kbps
				case TelephonyManager.NETWORK_TYPE_EVDO_0:
					return Const.CONN_TYPE_EVDO_0; // ~ 400-1000 kbps
				case TelephonyManager.NETWORK_TYPE_EVDO_A:
					return Const.CONN_TYPE_EVDO_A; // ~ 600-1400 kbps
				case TelephonyManager.NETWORK_TYPE_GPRS:
					return Const.CONN_TYPE_GPRS; // ~ 100 kbps
				case TelephonyManager.NETWORK_TYPE_HSDPA:
					return Const.CONN_TYPE_HSDPA; // ~ 2-14 Mbps
				case TelephonyManager.NETWORK_TYPE_HSPA:
					return Const.CONN_TYPE_HSPA; // ~ 700-1700 kbps
				case TelephonyManager.NETWORK_TYPE_HSUPA:
					return Const.CONN_TYPE_HSUPA; // ~ 1-23 Mbps
				case TelephonyManager.NETWORK_TYPE_UMTS:
					return Const.CONN_TYPE_UMTS; // ~ 400-7000 kbps
				// Unknown
				case TelephonyManager.NETWORK_TYPE_UNKNOWN:
					return Const.CONN_TYPE_UNKNOWN;
				default:
					if (Build.VERSION.SDK_INT >= 13) {
						if (subType == TelephonyManager.NETWORK_TYPE_HSPAP) // API level 13
						{
							return Const.CONN_TYPE_HSPAP; // ~ 10-20 Mbps
						}
					}
					if (Build.VERSION.SDK_INT >= 11) {
						if (subType == TelephonyManager.NETWORK_TYPE_EHRPD) // API level 11
						{
							return Const.CONN_TYPE_EHRPD; // ~ 1-2 Mbps
						}
						if (subType == TelephonyManager.NETWORK_TYPE_LTE) // API level 11
						{
							return Const.CONN_TYPE_LTE; // ~ 10+ Mbps
						}
					}
					if (Build.VERSION.SDK_INT >= 9) {
						if (subType == TelephonyManager.NETWORK_TYPE_EVDO_B) // API level 9
						{
							return Const.CONN_TYPE_EVDO_B; // ~ 5 Mbps
						}
					}
					if (Build.VERSION.SDK_INT >= 8) {
						if (subType == TelephonyManager.NETWORK_TYPE_IDEN) // API level 8
						{
							return Const.CONN_TYPE_IDEN; // ~25 kbps
						}
					}
					if (!cz.master.annie.utils.Connectivity.isConnected(context)) {
						return null;
					}

					return Const.CONN_TYPE_UNKNOWN;
			}
		} else {
			if (!cz.master.annie.utils.Connectivity.isConnected(context)) {
				return null;
			}

			return Const.CONN_TYPE_UNKNOWN;
		}
	}

	public static String getNetworkClass(Context context) {
		final NetworkInfo info = cz.master.annie.utils.Connectivity.getNetworkInfo(context);
		if (info == null) {
			return Const.CONN_TYPE_UNKNOWN;
		}
		final int type = info.getType();
		if (type == ConnectivityManager.TYPE_WIFI) {
			return Const.CONN_TYPE_WIFI;
		} else if (type == ConnectivityManager.TYPE_MOBILE) {
			TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			int networkType = mTelephonyManager.getNetworkType();
			switch (networkType) {
				case TelephonyManager.NETWORK_TYPE_GPRS:
				case TelephonyManager.NETWORK_TYPE_EDGE:
				case TelephonyManager.NETWORK_TYPE_CDMA:
				case TelephonyManager.NETWORK_TYPE_1xRTT:
				case TelephonyManager.NETWORK_TYPE_IDEN:
					return Const.CONN_TYPE_EDGE;
				case TelephonyManager.NETWORK_TYPE_UMTS:
				case TelephonyManager.NETWORK_TYPE_EVDO_0:
				case TelephonyManager.NETWORK_TYPE_EVDO_A:
				case TelephonyManager.NETWORK_TYPE_HSDPA:
				case TelephonyManager.NETWORK_TYPE_HSUPA:
				case TelephonyManager.NETWORK_TYPE_HSPA:
				case TelephonyManager.NETWORK_TYPE_EVDO_B:
				case TelephonyManager.NETWORK_TYPE_EHRPD:
				case TelephonyManager.NETWORK_TYPE_HSPAP:
					return Const.CONN_TYPE_3G;
				case TelephonyManager.NETWORK_TYPE_LTE:
					return Const.CONN_TYPE_LTE;
				default:
					return Const.CONN_TYPE_UNKNOWN;
			}
		}

		return Const.CONN_TYPE_UNKNOWN;
	}

	public static boolean isPeerMobile(String type) {
		return !(type.equals(Const.CONN_TYPE_WIFI) || type.equals(Const.CONN_TYPE_UNKNOWN));
	}

}