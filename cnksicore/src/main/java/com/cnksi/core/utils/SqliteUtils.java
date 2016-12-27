package com.cnksi.core.utils;

public class SqliteUtils {

	// Sqlite 得到当前时间 eg: 2014-08-19 10:05:48
	public static final String DATE_TIME_NOW = "(SELECT datetime('now','localtime'))";
	// Sqlite 得到今天的结束时间 eg: 2014-08-19 23:59:59
	public static final String DATE_TIME_END_OF_DAY = "(SELECT datetime('now','start of day','+23 hour','+59 minute','+59 second'))";
	// Sqlite 得到今天的开始时间 eg: 2014-08-19 00:00:00
	public static final String DATE_TIME_START_OF_DAY = "(SELECT datetime('now','start of day'))";
	// Sqlite 得到今天的时间 eg: 2014-08-19
	public static final String DATE_NOW_DAY = "(SELECT date('now','start of day'))";
	
	// Sqlite 得到当前时间
	public static final String DATE_TIME_NOW_TIMESTAMP = "(SELECT strftime('%s','now'))";
	// Sqlite 得到今天的结束时间戳  
	public static final String DATE_TIME_END_OF_DAY_TIMESTAMP = "(SELECT strftime('%s','now','start of day','+23 hour','+59 minute','+59 second'))";
	// Sqlite 得到今天的开始时间 戳
	public static final String DATE_TIME_START_OF_DAY_TIMESTAMP = "(SELECT strftime('%s','now','start of day'))";
	// Sqlite 得到今天的时间戳  
	public static final String DATE_NOW_DAY_TIMESTAMP = "(SELECT strftime('%s','now','start of day'))";

	// sqlite 得到某天时间的开始时间
	public static final String getTimeStartOfDay(String dateTime) {
		return "(SELECT datetime(" + dateTime + ",'start of day'))";
	}

	/**
	 * sqlite 得到某天时间的结束时间 2014-08-25 23:59:59
	 * 
	 * sql select datetime('2014-08-18 18:20:48','start of day','+23 hour','+59 minute','+59 second');
	 */
	public static final String getTimeEndOfDay(String dateTime) {
		return "(SELECT datetime(" + dateTime + ",'start of day','+23 hour','+59 minute','+59 second'))";
	}

	/**
	 * Sqlite 得到当前几天后的某天的结束时间
	 * 
	 * sql： select datetime('2014-08-18 18:20:48','start of day','+7 day','+23 hour','+59 minute','+59 second');
	 */
	public static final String getDaysLaterTimeEndOfDay(int dateLater) {
		return "(SELECT datetime('now','start of day','+" + dateLater + " day','+23 hour','+59 minute','+59 second'))";
	}

	/**
	 * Sqlite 得到当前时间几天后的开始时间
	 * 
	 * @param dateLater
	 * @return select datetime('2014-08-18 18:20:48','start of day','+7 day');
	 */
	public static final String getDaysLaterTimeStartOfDay(int dateLater) {
		return "(SELECT datetime('now','start of day','" + dateLater + " day'))";
	}
}
