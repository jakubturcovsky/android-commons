package cz.yellowjello.commons;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

public class Others {

	public static ArrayList<String> stringToArrayList(String srcString, String delimiter) {
		StringTokenizer st = new StringTokenizer(srcString, delimiter);
		ArrayList<String> tokenList = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			tokenList.add(st.nextToken());
		}
		return tokenList;
	}

	/**
	 * Get current time in miliseconds
	 *
	 * @return Milliseconds from epoch
	 */
	public static Long getCurrentMilis() {
		Calendar calendar = Calendar.getInstance();
		return calendar.getTime().getTime();
	}
}
