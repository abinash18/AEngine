package com.base.engine.handlers.logging;

import com.base.engine.handlers.file.FileHandler;

public class Logger {

	private String name;

	public Logger(String name) {
		this.name = name;
	}

	public Logger(String name, FileHandler fileHandler) {
		this.name = name;
	}

	public void setClassName(String className) {
		this.name = className;
	}

	private void println(String line) {
		System.out.println(line);
		if (LogManager.fileHandler != null) {
			try {
				LogManager.fileHandler.out.println(line);
				LogManager.fileHandler.out.flush();
			} catch (Exception e) {
				System.out.println("Error Writing To Log File.");
				e.printStackTrace();
			}
		}
	}

	public void log(String msg) {
		println(this.formatLog(LogLevel.ALL) + msg);
	}

	public void log(String msg, Exception e) {
		println(this.formatLog(LogLevel.ALL) + msg);
		e.printStackTrace();
	}

	private String formatLog(LogLevel l) {
		String log = "[" + LogManager.getCurrentTimeAndDate() + "] [" + this.name + "] " + l + ": ";
		return log;
	}

	public boolean finnest(String msg) {
		if (LogManager.getCurrentLevel() == LogLevel.FINNEST || LogManager.getCurrentLevel() == LogLevel.ALL) {
			println(this.formatLog(LogLevel.FINNEST) + msg);
			return (true);
		}
		return (false);
	}

	public void finnest(String msg, Exception e) {
		if (finnest(msg)) {
			// e.printStackTrace();
			println(e.getStackTrace().toString());
		}
	}

	public boolean fine(String msg) {
		if (LogManager.getCurrentLevel() == LogLevel.FINE || LogManager.getCurrentLevel() == LogLevel.ALL) {
			println(this.formatLog(LogLevel.FINE) + msg);
			return (true);
		}
		return (false);
	}

	public void fine(String msg, Exception e) {
		if (fine(msg)) {
			println(e.getStackTrace().toString());
		}
	}

	public boolean debug(String msg) {
		if (LogManager.getCurrentLevel() == LogLevel.DEBUG || LogManager.getCurrentLevel() == LogLevel.ALL) {
			println(this.formatLog(LogLevel.DEBUG) + msg);
			return (true);
		}
		return (false);
	}

	public void debug(String msg, Exception e) {
		if (debug(msg)) {
			println(e.getStackTrace().toString());
		}
	}

	public boolean info(String msg) {
		if (LogManager.getCurrentLevel() == LogLevel.INFO || LogManager.getCurrentLevel() == LogLevel.ALL) {
			println(this.formatLog(LogLevel.INFO) + msg);
			return (true);
		}
		return (false);
	}

	public void info(String msg, Exception e) {
		if (info(msg)) {
			println(e.getStackTrace().toString());
		}
	}

	public boolean warning(String msg) {
		if (LogManager.getCurrentLevel() == LogLevel.WARNING || LogManager.getCurrentLevel() == LogLevel.ALL) {
			println(this.formatLog(LogLevel.WARNING) + msg);
			return (true);
		}
		return (false);
	}

	public void warning(String msg, Exception e) {
		if (warning(msg)) {
			println(e.getStackTrace().toString());
		}
	}

	public boolean error(String msg) {
		if (LogManager.getCurrentLevel() == LogLevel.ERROR || LogManager.getCurrentLevel() == LogLevel.ALL) {
			println(this.formatLog(LogLevel.ERROR) + msg);
			return (true);
		}
		return (false);
	}

	public void error(String msg, Throwable e) {
		if (error(msg)) {
			println(e.getStackTrace().toString());
		}
	}

}
