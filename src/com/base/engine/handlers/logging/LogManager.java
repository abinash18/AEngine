package com.base.engine.handlers.logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.sun.org.apache.bcel.internal.generic.AALOAD;

// TODO: Add Allowed Levels Array List To Allow Only Certain Levels To Be Shown (Eg. INFO, ERROR only).
public class LogManager {

	private static HashMap<String, Logger> loggers = new HashMap<String, Logger>();
	// private static ArrayList<LogLevel> allowedLevels = new ArrayList<LogLevel>();

	private static LogLevel currentLevel = LogLevel.ALL;

	private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private static Date date = new Date();

	public static Logger getLogger(String className) {

		if (!loggers.containsKey(className)) {
			loggers.put(className, new Logger(className));
		}

		return (loggers.get(className));

	}

	public static void setLogLevel(LogLevel l) {
		currentLevel = l;
	}

	public static HashMap<String, Logger> getLoggers() {
		return loggers;
	}

	public static void setLoggers(HashMap<String, Logger> loggers) {
		LogManager.loggers = loggers;
	}

	public static LogLevel getCurrentLevel() {
		return currentLevel;
	}

	public static void setCurrentLevel(LogLevel currentLevel) {
		LogManager.currentLevel = currentLevel;
	}

	public static DateFormat getDateFormat() {
		return dateFormat;
	}

	public static void setDateFormat(DateFormat dateFormat) {
		LogManager.dateFormat = dateFormat;
	}

	public static Date getDate() {
		return date;
	}

	public static void setDate(Date date) {
		LogManager.date = date;
	}

	public static String getCurrentTimeAndDate() {
		return (dateFormat.format(date));
	}

}
