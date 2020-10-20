/*******************************************************************************
 * Copyright 2020 Abinash Singh | ABI INC.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.abi.abisEngine.handlers.logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import net.abi.abisEngine.handlers.file.FileHandler;

import java.util.Set;

// TODO: Fix File Name Problem.
public class LogManager {

	private static Map<String, Logger> loggers = new HashMap<>();
	private static Set<LogLevel> allowedLevels = new HashSet<>();

	private static LogLevel currentLevel;

	public static FileHandler fileHandler;

	public synchronized static Logger getLogger(String className) {
		return loggers.computeIfAbsent(className, name -> {
			Logger result = new Logger(name);
			if (fileHandler != null) {
				result.setOutputForLogFile(fileHandler.out);
			}
			return result;
		});
	}

	public synchronized static <T> Logger getLogger(Class<T> s) {
		return getLogger(s.getSimpleName());
	}

	public synchronized static void addFileHandler(int maxLines) {
		if (fileHandler != null) {
			return;
		}

		try {
			fileHandler = new FileHandler("log.log", "./logs/");
			fileHandler.setAppend(false);
			fileHandler.initializeWriter(true, maxLines);
			fileHandler.out.println("\n-------------------------------------" + getCurrentTimeAndDate()
					+ "-------------------------------------\n");

			for (Entry<String, Logger> entry : loggers.entrySet()) {
				String key = entry.getKey();
				Logger value = entry.getValue();

				System.out.println("Adding output " + key);
				value.setOutputForLogFile(fileHandler.out);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public synchronized static void addAllowedLevel(LogLevel l) {
		allowedLevels.add(l);
	}

	public synchronized static void removeAllowedLevel(LogLevel l) {
		allowedLevels.remove(l);
	}

	public synchronized static boolean isLevelAllowed(LogLevel l) {
		return allowedLevels.contains(l);
	}

	public static void setLogLevel(LogLevel l) {
		currentLevel = l;
	}

	public static Map<String, Logger> getLoggers() {
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
		return (DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date()));
	}

	public static void setFileHandler(FileHandler fileHandler) {
		LogManager.fileHandler = fileHandler;
	}

}
