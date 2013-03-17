/**
 * 
 */
package il.ac.shenkar.todo.utilities;

import java.util.Date;
import java.util.TimeZone;

import android.text.TextUtils;

import com.google.api.client.util.DateTime;

/**
 * @author ran
 *
 */
public abstract class DateTimeUtils {
	
	/**
	 * Formats the datetime to the default time zone.
	 * 
	 * @param dateTime	DateTime to format
	 * @return			DateTime formated to the default time zone
	 */
	public static DateTime getTimeZonedDateTime(DateTime dateTime) {
		long timeZoneOffset = TimeZone.getDefault().getOffset(dateTime.getValue());
		return new DateTime(dateTime.getValue() - timeZoneOffset);
	}

	/**
	 * Converts two strings date and time to DateTime
	 * 
	 * @param dateStr	String represents the date in readable format
	 * @param timeStr	String represents the time in readable format
	 * @return		DateTime represents the given strings date and time,
	 * 				or null if the strings can't be parse to date
	 */
	public static DateTime fromDateTimeStrsToDateTime(String dateStr, String timeStr) {
		// Validates arguments
		if (TextUtils.isEmpty(dateStr) || TextUtils.isEmpty(timeStr)) {
			return null;
		}
		try {
			String dateRFC3999 = fromDateStrToDateRFC3999(dateStr);
			String timeRFC3999 = fromTimeStrToTimeRFC3999(timeStr);
			StringBuilder sb = new StringBuilder()
			.append(dateRFC3999).append("T").append(timeRFC3999).append("Z");
			String dateTimeRFC3999 = sb.toString();
			DateTime dateTime = DateTime.parseRfc3339(dateTimeRFC3999);
			return dateTime;
		} catch (NumberFormatException e) {
			// The strings date or/and time can't be parsed,
			// Do nothing
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Converts a date readable string to DateTime
	 * 
	 * @param dateStr	String represents the date readable string
	 * @return		DateTime represents the given strings date and time,
	 * 				or null if the strings can't be parse to date.
	 */
	public static DateTime fromDateStrToDateTime(String dateStr) {
		// Validates arguments
		if (TextUtils.isEmpty(dateStr)) {
			return null;
		}
		try {
			String dateRFC3999 = fromDateStrToDateRFC3999(dateStr);
			StringBuilder sb = new StringBuilder()
			.append(dateRFC3999).append("T00:00:00.000Z");
			String dateTimeStr = sb.toString();
			return DateTime.parseRfc3339(dateTimeStr);
		} catch (NumberFormatException e) {
			// The strings date or/and time can't be parsed,
			// Do nothing
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Converts from date to datetime.
	 * 
	 * @param date	Date represents date to be converted
	 * @return		DateTime represents the date, or null if date is null
	 */
	public static DateTime fromDateToDateTime(Date date) {
		if (date != null) {
			return new DateTime(date, TimeZone.getDefault());
		} else {
			return null;
		}
	}
	
	/**
	 * Returns now datetime.
	 * 
	 * @return		DateTime represents now date
	 */
	public static DateTime getDateTimeNow() {
		return fromDateToDateTime(new Date());
	}
	
	/**
	 * Returns now datetime date only without time.
	 * 
	 * @return		DateTime represents now date
	 */
	public static DateTime getDateNow() {
		DateTime now = fromDateToDateTime(new Date());
		String nowRFC3339 = DateTimeUtils.getDateRFC3999FromDateTime(now);
		return DateTime.parseRfc3339(nowRFC3339);
	}
	
	/**
	 * Returns the date RFC3999 format string from datetime.
	 * 
	 * @param dateTime	DateTime date contains date data
	 * @return			String represents the date RFC3999 string
	 */
	public static String getDateRFC3999FromDateTime(DateTime dateTime) {
		String dateTimeStr = dateTime.toString();
		int tPos = dateTimeStr.indexOf('T');
		return dateTimeStr.substring(0, tPos);
	}
	
	/**
	 * Returns the date readable format string from datetime.
	 * 
	 * @param dateTime	DateTime date contains date data
	 * @return			String represents the date readable string
	 */
	public static String getDateStrFromDateTime(DateTime dateTime) {
		String dateRFC3999 = getDateRFC3999FromDateTime(dateTime);
		return fromDateRFC3999ToDateStr(dateRFC3999);
	}
	
	/**
	 * Returns the time RFC3999 format string from datetime.
	 * 
	 * @param dateTime	DateTime date contains time data
	 * @return			String represents the time RFC3999 string
	 */
	public static String getTimeRFC3999FromDateTime(DateTime dateTime) {
		String dateTimeStr = dateTime.toString();
		int tPos = dateTimeStr.indexOf('T');
		int colonPos = dateTimeStr.lastIndexOf(':');
		return dateTimeStr.substring(tPos+1, colonPos);
	}
	
	/**
	 * Returns the time readable format string from datetime.
	 * 
	 * @param dateTime	DateTime date contains time data
	 * @return			String represents the time readable string
	 */
	public static String getTimeStrFromDateTime(DateTime dateTime) {
		String timeRFC3999 = getTimeRFC3999FromDateTime(dateTime);
		return fromTimeRFC3999ToTimeStr(timeRFC3999);
	}
	
	/**
	 * Formats hour and minute to semi RFC3999
	 * datetime string format the time part.
	 * 
	 * @param hourOfDay	Integer represents the hour
	 * @param minute	Integer represents the minute
	 * @return			String represents the time part in
	 * 					semi RFC3999 string format
	 */
	public static String formatTimeParamsToTimeRFC3999(int hourOfDay, int minute) {
		String hourStr = (hourOfDay < 10) ? "0" + hourOfDay : "" + hourOfDay;
		String minuteStr = (minute < 10) ? "0" + minute : "" + minute;
		StringBuilder sb = new StringBuilder()
		.append(hourStr).append(":").append(minuteStr).append(":00.000");
		return sb.toString();
	}
	
	/**
	 * Formats hour and minute to readable string format the time part.
	 * 
	 * @param hourOfDay	Integer represents the hour
	 * @param minute	Integer represents the minute
	 * @return			String represents the time part in
	 * 					readable string
	 */
	public static String formatTimeParamsToTimeStr(int hourOfDay, int minute) {
		String hourStr = (hourOfDay < 10) ? "0" + hourOfDay : "" + hourOfDay;
		String minuteStr = (minute < 10) ? "0" + minute : "" + minute;
		StringBuilder sb = new StringBuilder()
		.append(hourStr).append(":").append(minuteStr);
		return sb.toString();
	}
	
	/**
	 * Formats year, month and day to semi RFC3999
	 * datetime string format the date part.
	 * 
	 * @param year	Integer represents the year
	 * @param month	Integer represents the month
	 * @param day	Integer represents the day
	 * @return		String represents the date part in
	 * 				semi RFC3999 string format
	 */
	public static String formatDateParamsToDateRFC3999(int year, int monthOfYear, int dayOfMonth) {
		monthOfYear += 1;
		String monthStr = (monthOfYear < 10) ? "0" + monthOfYear : "" + monthOfYear;
		String dayStr = (dayOfMonth < 10) ? "0" + dayOfMonth : "" + dayOfMonth;
		StringBuilder sb = new StringBuilder()
		.append(year).append("-").append(monthStr).append("-").append(dayStr);
		return sb.toString();
	}
	
	/**
	 * Formats year, month and day to readable string format the date part.
	 * 
	 * @param year	Integer represents the year
	 * @param month	Integer represents the month
	 * @param day	Integer represents the day
	 * @return		String represents the date part in
	 * 				readable string
	 */
	public static String formatDateParamsToDateStr(int year, int monthOfYear, int dayOfMonth) {
		monthOfYear += 1;
		String monthStr = (monthOfYear < 10) ? "0" + monthOfYear : "" + monthOfYear;
		String dayStr = (dayOfMonth < 10) ? "0" + dayOfMonth : "" + dayOfMonth;
		StringBuilder sb = new StringBuilder()
		.append(dayStr).append(".").append(monthStr).append(".").append(year);
		return sb.toString();
	}
	
	/**
	 * Formats time RFC3999 string to readable time string.
	 * 
	 * @param timeRFC3999	String represents time in RFC3999 format
	 * @return				String represents time in readable format
	 */
	public static String fromTimeRFC3999ToTimeStr(String timeRFC3999) {
		int lastColonPos = timeRFC3999.lastIndexOf(':');
		return timeRFC3999.substring(0, lastColonPos);
	}
	
	/**
	 * Formats time readable string to RFC3999 time string.
	 * 
	 * @param timeStr	String represents time in readable format
	 * @return			String represents time in RFC3999 format
	 */
	public static String fromTimeStrToTimeRFC3999(String timeStr) {
		return timeStr + ":00.000";
	}
	
	/**
	 * Formats date RFC3999 string to readable date string.
	 * 
	 * @param dateRFC3999	String represents date in RFC3999 fromat
	 * @return				String represents date in readable fromat
	 */
	public static String fromDateRFC3999ToDateStr(String dateRFC3999) {
		String[] stringArray = dateRFC3999.split("-");
		StringBuilder sb = new StringBuilder()
		.append(stringArray[2]).append(".").append(stringArray[1]).append(".").append(stringArray[0]);
		return sb.toString();
	}
	
	/**
	 * Formats readable date string to date RFC3999 string.
	 * 
	 * @param dateStr	String represents date in readable fromat
	 * @return			String represents date in RFC3999 fromat
	 */
	public static String fromDateStrToDateRFC3999(String dateStr) {
		String[] stringArray = dateStr.split("\\.");
		StringBuilder sb = new StringBuilder()
		.append(stringArray[2]).append("-").append(stringArray[1]).append("-").append(stringArray[0]);
		return sb.toString();
	}
	
	/**
	 * Formats datetime RFC3999 string to readable datetime string.
	 * 
	 * @param dateTimeRFC3999	String represents datetime in RFC3999 format
	 * @return					String represents datetime in readable format
	 */
	public static String fromDateTimeRFC3999ToDateTimeStr(String dateTimeRFC3999) {
		int tPos = dateTimeRFC3999.indexOf('T');
		int zPos = dateTimeRFC3999.lastIndexOf('Z');
		String dateRFC3999 = dateTimeRFC3999.substring(0, tPos);
		String timeRFC3999 = null;
		// If z found found
		if (zPos != -1) {
			timeRFC3999 = dateTimeRFC3999.substring(tPos+1, zPos);
		// If z not found
		} else {
			timeRFC3999 = dateTimeRFC3999.substring(tPos+1);
		}
		String dateStr = fromDateRFC3999ToDateStr(dateRFC3999);
		String timeStr = fromTimeRFC3999ToTimeStr(timeRFC3999);
		StringBuilder sb = new StringBuilder()
		.append(timeStr).append(" ").append(dateStr);
		return sb.toString();
	}

}
