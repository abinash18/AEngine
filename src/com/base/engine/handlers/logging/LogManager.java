package com.base.engine.handlers.logging;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import com.base.engine.handlers.file.FileHandler;
import com.sun.xml.internal.bind.v2.runtime.Name;

// TODO: Fix File Name Problem.
public class LogManager {

	private static HashMap<String, Logger> loggers = new HashMap<String, Logger>();
	private static ArrayList<LogLevel> allowedLevels = new ArrayList<LogLevel>();

	private static LogLevel currentLevel;

	public static FileHandler fileHandler;

	public static Logger getLogger(String className) {
		if (!loggers.containsKey(className)) {
			Logger resultLogger = new Logger(className);
			loggers.put(className, resultLogger);
			if (fileHandler != null) {
				resultLogger.setOutputForLogFile(fileHandler.out);
			}
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

				for (Entry<String, Logger> entry : loggers.entrySet()) {
					String key = entry.getKey();
					Logger value = entry.getValue();

					System.out.println("Adding output " + key);
					value.setOutputForLogFile(fileHandler.out);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public static void addAllowedLevel(LogLevel l) {
		if (!allowedLevels.contains(l)) {
			allowedLevels.add(l);
		}
	}

	public static void removeAllowedLevel(LogLevel l) {
		if (!allowedLevels.contains(l)) {
			allowedLevels.remove(l);
		}
	}

	public static boolean isLevelAllowed(LogLevel l) {
		if (allowedLevels.contains(l)) {
			return (true);
		}
		return (false);
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
