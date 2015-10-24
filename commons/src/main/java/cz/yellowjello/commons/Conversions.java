package cz.yellowjello.commons;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

public class Conversions {

	// objs
	final private static String SMILEY_DELIM = "^_^";
	final private static char[] hexArray = "0123456789ABCDEF".toCharArray();


	// hex to byte array
	public static String byteArrayToHexString(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
					+ Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	// time
	public static Long getCurrentMilis() {
		Calendar calendar = Calendar.getInstance();
		return calendar.getTime().getTime();
	}

	// parsing string
	public static String getSmileyDelimiter() {
		return SMILEY_DELIM;
	}

	public static byte[] getSmileyDeliminterInBytes() {
		return getSmileyDelimiter().getBytes();
	}

	public static int getSmileyDelimiterSize() {
		return getSmileyDelimiter().length();
	}

	public static ArrayList<String> getArrayListFromString(String srcString, String delimiter) {
		StringTokenizer st = new StringTokenizer(srcString, delimiter);
		ArrayList<String> tokenList = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			tokenList.add(st.nextToken());
		}
		return tokenList;
	}

	public static ArrayList<String> getArrayListFromStringWithSmileyDelimiter(String srcString) {
		StringTokenizer st = new StringTokenizer(srcString, "^_^");
		ArrayList<String> tokenList = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			tokenList.add(st.nextToken());
		}
		return tokenList;
	}


	// parsing ip address + ports
	public static String longIpAddressToString(long addr) {
		Long lAddr = addr;
		StringBuilder sb = new StringBuilder(15);

		for (int i = 0; i < 4; i++) {
			sb.insert(0, Long.toString(lAddr & 0xff));
			if (i < 3) {
				sb.insert(0, '.');
			}
			lAddr >>= 8;
		}
		return sb.toString();
	}

	/**
	 * Convert raw IP address to string.
	 *
	 * @param rawBytes raw IP address.
	 *
	 * @return a string representation of the raw ip address.
	 */
	public static String rawIpAddressToString(@NonNull final byte[] rawBytes) {
		byte i = 4;
		final StringBuilder ipAddress = new StringBuilder(4 * 2);
		for (final byte raw : rawBytes) {
			ipAddress.append(raw & 0xFF);
			if (--i > 0) {
				ipAddress.append(".");
			}
		}

		return ipAddress.toString();
	}

	public static byte[] portToByteArray(int port) {
		if (port < 0 || port > 65535) {
			return null;
		}
		byte[] bPort = new byte[2];
		bPort[0] = (byte) (port & 0xFF);
		bPort[1] = (byte) ((port >> 8) & 0xFF);
		return bPort;
	}

	public static int byteArrayToPort(byte[] data) {
		if (data.length != 2) {
			return 0;
		}

		int port = ((byte) data[1] & 0xff) * 256 + ((byte) data[0] & 0xff);

		return port;
	}

	public static char byteArrayToChar(byte[] data) throws Exception {
		if (data.length != 2) {
			throw new Exception("The byte[] MUST have size 2");
		}

		char c = (char) (((byte) data[1] & 0xff) * 256 + ((byte) data[0] & 0xff));

		return c;
	}

	public static byte[] charToByteArray(char c) {
		byte[] bChar = new byte[2];
		bChar[0] = (byte) (c & 0xFF);
		bChar[1] = (byte) ((c >> 8) & 0xFF);

		return bChar;
	}

	public static byte[] ipAddressToByteArray(String ipAddress) {
		byte[] bIP;

		// ip address
		InetSocketAddress ipa = new InetSocketAddress(ipAddress, 0);
		bIP = ipa.getAddress().getAddress();

		return bIP;
	}


	public static InetSocketAddress byteArrayToInetSocketAddress(byte[] data) {
		if ((data == null) || (data.length != 6)) {
			return null;
		}

		// ip address
		String address = String.format("%d.%d.%d.%d", ((byte) data[0] & 0xff), ((byte) data[1] & 0xff), ((byte) data[2] & 0xff), ((byte) data[3] & 0xff));

		// port
		int port = ((byte) data[5] & 0xff) * 256 + ((byte) data[4] & 0xff);

		// ip address
		InetSocketAddress ipa = new InetSocketAddress(address, port);

		return ipa;
	}

	public static InetAddress byteArrayToInetAddress(byte[] data) {
		if ((data == null) || (data.length != 4)) {
			return null;
		}

		// ip address
		String address = String.format("%d.%d.%d.%d", ((byte) data[0] & 0xff), ((byte) data[1] & 0xff), ((byte) data[2] & 0xff), ((byte) data[3] & 0xff));

		// ip address
		InetSocketAddress ipa = new InetSocketAddress(address, 0);

		return ipa.getAddress();
	}

	public static Integer safeLongToInt(Long l) {
		if (l == null) {
			return null;
		}
		if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
			throw new IllegalArgumentException
					(l + " cannot be cast to int without changing its value.");
		}
		return (int) l.longValue();
	}


	public static String getStackTraceMessageFromException(Exception e, int maxLevel) {
		StackTraceElement[] ste = e.getStackTrace();
		if (ste.length > 0) {
			Integer count = ste.length;
			String s = "STACK[" + count.toString() + "]:\r\n";
			for (int i = 0; i < ste.length; i++) {
				if ((maxLevel <= 0) || (i <= maxLevel)) {
					Integer intValue = i;
					s = s + "[" + intValue.toString() + "]=" + ste[intValue].toString() + "\r\n";
				}
			}
			return s;
		} else {
			return null;
		}
	}

	public static String hexStringToAscii(String s) {
		int n = s.length();
		StringBuilder sb = new StringBuilder(n / 2);
		for (int i = 0; i < n; i += 2) {
			char a = s.charAt(i);
			char b = s.charAt(i + 1);
			sb.append((char) ((Conversions.hexToInt(a) << 4) | Conversions.hexToInt(b)));
		}

		return sb.toString();
	}

	public static int hexToInt(char ch) {
		if ('a' <= ch && ch <= 'f') {
			return ch - 'a' + 10;
		}
		if ('A' <= ch && ch <= 'F') {
			return ch - 'A' + 10;
		}
		if ('0' <= ch && ch <= '9') {
			return ch - '0';
		}
		throw new IllegalArgumentException(String.valueOf(ch));
	}

	public static byte[] longToBytes(long x) {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.putLong(x);
		return buffer.array();
	}

	public static byte[] longToBytesLittleEndian(long x) {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.putLong(x);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		return buffer.array();
	}

	public static long bytesToLong(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.put(bytes);
		buffer.flip();//need flip
		return buffer.getLong();
	}

	public static byte[] longToBytesFast(long longNum) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) (longNum & 0xFF);
		bytes[1] = (byte) ((longNum >> 8) & 0xFF);
		bytes[2] = (byte) ((longNum >> 16) & 0xFF);
		bytes[3] = (byte) ((longNum >> 24) & 0xFF);

		return bytes;
	}

	public static long bytesToLongFast(byte[] bytes) {
		return bytes[3] << 24 | (bytes[2] & 0xFF) << 16 | (bytes[1] & 0xFF) << 8 | (bytes[0] & 0xFF);
	}

	//public static byte[] encryptBytes(final byte[] bytes, final String aes) throws InvalidKeyException, UnsupportedEncodingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
	//    return AppController.getInstance().getCryptLib().encryptB(bytes, aes, Const.iv);
	//}
	//
	//public static byte[] decryptBytes(final byte[] bytes, final String aes) throws InvalidKeyException, UnsupportedEncodingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
	//    return AppController.getInstance().getCryptLib().decryptB(bytes, aes, Const.iv);
	//}

	/**
	 * Converts Bitmap to bytes.
	 *
	 * @param bitmap - input Bitmap
	 *
	 * @return - output byte[]
	 */
	private byte[] bitmapToBytes(Bitmap bitmap) {

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		return byteArray;
	}

	/**
	 * Converts bytes to Bitmap.
	 *
	 * @param bytes - input byte[]
	 *
	 * @return - output Bitmap
	 */
	private Bitmap bytesToBitmap(byte[] bytes) {

		Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

		return bitmap;
	}

	/**
	 * Converts dp value to pixels
	 *
	 * @param dp - dp to convert
	 *
	 * @return - dp converted to Px
	 */
	public static int dpToPx(int dp, Context context) {
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
		return px;
	}


	/**
	 * Converts pixels value to dp
	 *
	 * @param px - pixels to convert
	 *
	 * @return - px converted to dp
	 */
	public static int pxToDp(int px, Context context) {
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
		return dp;
	}


}
