/**
 * 
 */
package il.ac.shenkar.todo.utilities;

import java.util.Date;
import java.util.TimeZone;

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
	 * @param date	String represents the date
	 * @param time	String represents the time
	 * @return		DateTime represents the given strings date and time,
	 * 				or null if the strings can't be parse to date
	 */
	public static DateTime fromStringsToDateTime(String date, String time) {
		// Validates arguments
		if (date == null || time == null || "".equals(date) || "".equals(time)) {
			return null;
		}		
		try {
			String dateTimeStr = date+"T"+time+"Z";
			DateTime dateTime = DateTime.parseRfc3339(dateTimeStr);
			return getTimeZonedDateTime(dateTime);
		} catch (NumberFormatException e) {
			// The strings date or/and time can't be parsed,
			// Do nothing
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Converts a date string to DateTime
	 * 
	 * @param date	String represents the date
	 * @return		DateTime represents the given strings date and time,
	 * 				or null if the strings can't be parse to date
	 */
	public static DateTime fromStringToDateTime(String date) {
		// Validates arguments
		if (date == null || "".equals(date)) {
			return null;
		}
		try {
			String dateTimeStr = date+"T00:00:00.000Z";
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
	 * 
	 * 
	 * @param rhs	DateTime to compare
	 * @param lhs	DateTime to compare
	 * @return		Integer represents the compare result,
	 * 				positive if rhs later than lhs,
	 * 				zero if rhs equals lhs,
	 * 				negative if lhs later than rhs.
	 */
	public static int compare(DateTime rhs, DateTime lhs) {
		return (int) (rhs.getValue() - lhs.getValue());
	}
	
	/**
	 * Converts from datetime to date.
	 * 
	 * @param dateTime	DateTime represents date to be converted
	 * @return			Date represents the datetime, or null if datetime is null
	 *//*
	public static Date fromDateTimeToDate(DateTime dateTime) {
		if (dateTime != null) {
			return new Date();
		} else {
			return null;
		}
	}*/
	
	/**
	 * Returns the date format string from datetime.
	 * 
	 * @param dateTime	DateTime date contains date data
	 * @return			String represents the datetime date
	 */
	public static String getDateFromDateTime(DateTime dateTime) {
		if (dateTime != null) {
			String dateTimeStr = dateTime.toString();
			int tPos = dateTimeStr.indexOf('T');
			return dateTimeStr.substring(0, tPos);
		}
		return null;
	}
	
	/**
	 * Returns the time format string from datetime.
	 * 
	 * @param dateTime	DateTime date contains time data
	 * @return			String represents the datetime time
	 */
	public static String getTimeFromDateTime(DateTime dateTime) {
		if (dateTime != null) {
			String dateTimeStr = dateTime.toString();
			int tPos = dateTimeStr.indexOf('T');
			int colonPos = dateTimeStr.lastIndexOf(':');
			return dateTimeStr.substring(tPos+1, colonPos);
		}
		return null;
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
	public static String formatTimeToString(int hourOfDay, int minute) {
		String hourStr = (hourOfDay < 10) ? "0" + hourOfDay : "" + hourOfDay;
		String minuteStr = (minute < 10) ? "0" + minute : "" + minute;
		return hourStr + ":" + minuteStr + ":00.000";
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
	public static String formatDateToString(int year, int monthOfYear, int dayOfMonth) {
		monthOfYear += 1;
		String monthStr = (monthOfYear < 10) ? "0" + monthOfYear : "" + monthOfYear;
		String dayStr = (dayOfMonth < 10) ? "0" + dayOfMonth : "" + dayOfMonth;
		return year + "-" + monthStr + "-" + dayStr;
	}

}
