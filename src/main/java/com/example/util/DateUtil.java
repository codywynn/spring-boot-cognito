package com.example.util;

import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.TimeZone;

public class DateUtil {

	/**
	 * The method returns the current time in milliseconds
	 */
	public static long getCurrentTimeInMilliseconds() {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("US/Pacific"));
		long time = cal.getTimeInMillis();
		return time;
	}

	/**
	 * The method returns the days from the milliseconds
	 * 
	 * @param milliseconds
	 */
	public static long getDays(long milliseconds) {
		long days = (milliseconds / (60 * 60 * 24 * 1000));
		return days;
	}

	/**
	 * The method removes time from the datetime and returns only the date
	 * 
	 * @param dateInMilliseconds
	 */
	public static Calendar removeTimeFromDate(long dateInMilliseconds) {
		Calendar date = Calendar.getInstance(TimeZone.getTimeZone("US/Pacific"));
		date.setTimeInMillis(dateInMilliseconds);
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);

		return date;
	}

	/**
	 * The method calculates the number of days between two dates
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static long getDifferenceInDays(long start, long end) {
		Calendar startDate = removeTimeFromDate(start);
		Calendar endDate = removeTimeFromDate(end);

		// Add plus one day for numnber of days
		long daysBetween = ChronoUnit.DAYS.between(startDate.toInstant(), endDate.toInstant()) + 1;
		return daysBetween;
	}

	/**
	 * The method returns the time component from the date in milliseconds
	 * 
	 * @param dateInMilliseconds
	 * @return
	 */
	public static long getTime(long dateInMilliseconds) {
		Calendar date = Calendar.getInstance(TimeZone.getTimeZone("US/Pacific"));
		date.setTimeInMillis(dateInMilliseconds);
		int hours = date.get(Calendar.HOUR_OF_DAY);
		int mins = date.get(Calendar.MINUTE);
		long time = (hours * 60 + mins) * 60 * 1000;
		return time;
	}
}
