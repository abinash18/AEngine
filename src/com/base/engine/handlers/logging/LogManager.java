package com.base.engine.handlers.logging;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.base.engine.handlers.file.FileHandler;

// TODO: Add Allowed Levels Array List To Allow Only Certain Levels To Be Shown (Eg. INFO, ERROR only).
public class LogManager {

	private static HashMap<String, Logger> loggers = new HashMap<String, Logger>();
	// private static ArrayList<LogLevel> allowedLevels = new ArrayList<LogLevel>();

	private static LogLevel currentLevel = LogLevel.ALL;

	public static FileHandler fileHandler;

	public static Logger getLogger(String className) {
		if (!loggers.containsKey(className)) {
			loggers.put(className, new Logger(className));
		}
		return (loggers.get(className));
	}

	public static void addFileHandler() {
		if (fileHandler == null) {
			try {
				fileHandler = new FileHandler("log.log", "./logs/");
				fileHandler.setAppend(false);
				fileHandler.initializeWriter();
				fileHandler.out.println("\n-------------------------------------" + getCurrentTimeAndDate()
						+ "-------------------------------------\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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

	public static String getCurrentTimeAndDate() {
		return (DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(new Date()));
	}

	static SimpleDateFormat sdf = new SimpleDateFormat("yyyyy.MMMMM.dd GGG hh:mm aaa");

	private static String getCurrentTimeAndDateForFile() {
		return (sdf.format(new Date()));
	}

	public static void setFileHandler(FileHandler fileHandler) {
		LogManager.fileHandler = fileHandler;
	}

}
