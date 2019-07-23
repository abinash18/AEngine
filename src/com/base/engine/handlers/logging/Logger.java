package com.base.engine.handlers.logging;

import java.io.PrintWriter;

import com.base.engine.handlers.file.FileHandler;

public class Logger {

	private String name;
	private PrintWriter out;

	public Logger(String name) {
		this.name = name;
		// this.out = LogManager.fileHandler.out;
	}

	public Logger(String name, FileHandler fileHandler) {
		this.name = name;
		// this.out = LogManager.fileHandler.out;
	}

	public void setOutputForLogFile(PrintWriter out) {
		this.out = out;
	}

	public void setClassName(String className) {
		this.name = className;
	}

	private void println(String line) {
		System.out.println(line);
		if (out != null) {
			this.out.println(line);
			this.out.flush();
		}
	}

	private void println(Throwable e) {
		e.printStackTrace();
		if (out != null) {
			this.out.println(e.getClass().getSimpleName() + ": ");
			e.printStackTrace(this.out);
			this.out.flush();
		}
	}

	public void log(String msg) {
		println(this.formatLog(LogLevel.ALL) + msg);
	}

	public void log(String msg, Throwable e) {
		println(this.formatLog(LogLevel.ALL) + msg);
		println(e);
	}

	private String formatLog(LogLevel l) {
		String log = "[" + LogManager.getCurrentTimeAndDate() + "] [" + this.name + "] " + l + ": ";
		return log;
	}

	private boolean checkAllowed(LogLevel l) {
		if (LogManager.getCurrentLevel() == l || LogManager.getCurrentLevel() == LogLevel.ALL
				|| (LogManager.isLevelAllowed(l) || LogManager.isLevelAllowed(LogLevel.ALL))) {
			return (true);
		}
		return (false);
	}

	public boolean finnest(String msg) {
		if (checkAllowed(LogLevel.FINNEST)) {
			println(this.formatLog(LogLevel.FINNEST) + msg);
			return (true);
		}
		return (false);
	}

	public void finnest(String msg, Throwable e) {
		if (finnest(msg)) {
			// e.printStackTrace();
			println(e);
		}
	}

	public boolean fine(String msg) {
		if (checkAllowed(LogLevel.FINE)) {
			println(this.formatLog(LogLevel.FINE) + msg);
			return (true);
		}
		return (false);
	}

	public void fine(String msg, Throwable e) {
		if (fine(msg)) {
			println(e);
		}
	}

	public boolean debug(String msg) {
		if (checkAllowed(LogLevel.DEBUG)) {
			println(this.formatLog(LogLevel.DEBUG) + msg);
			return (true);
		}
		return (false);
	}

	public void debug(String msg, Throwable e) {
		if (debug(msg)) {
			println(e);
		}
	}

	public boolean info(String msg) {
		if (checkAllowed(LogLevel.INFO)) {
			println(this.formatLog(LogLevel.INFO) + msg);
			return (true);
		}
		return (false);
	}

	public void info(String msg, Throwable e) {
		if (info(msg)) {
			println(e);
		}
	}

	public boolean warning(String msg) {
		if (checkAllowed(LogLevel.WARNING)) {
			println(this.formatLog(LogLevel.WARNING) + msg);
			return (true);
		}
		return (false);
	}

	public void warning(String msg, Throwable e) {
		if (warning(msg)) {
			println(e);
		}
	}

	public boolean error(String msg) {
		if (checkAllowed(LogLevel.ERROR)) {
			println(this.formatLog(LogLevel.ERROR) + msg);
			return (true);
		}
		return (false);
	}

	public void error(String msg, Throwable e) {
		if (error(msg)) {
			println(e);
		}
	}

}
