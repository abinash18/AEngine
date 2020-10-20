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

import java.io.PrintWriter;

import net.abi.abisEngine.handlers.file.FileHandler;

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

	int lineCount = 0;

	private synchronized void println(String line) {
		System.out.println(line);
		if (out != null && lineCount <= LogManager.fileHandler.getMaxLines()) {
			this.out.println(line);
			lineCount++;
		}
	}

	private synchronized void println(Throwable e) {
		e.printStackTrace();
		if (out != null) {
			this.out.println(e.getClass().getSimpleName() + ": ");
			e.printStackTrace(this.out);
			this.out.flush();
		}
	}

	public synchronized void log(LogLevel l, String msg) {
		if (checkAllowed(l)) {
			println(this.formatLog(l) + msg);
		}
	}

	public synchronized void log(LogLevel l, String msg, Throwable e) {
		if (checkAllowed(l)) {
			println(this.formatLog(l) + msg);
			println(e);
		}
	}

	private synchronized String formatLog(LogLevel l) {
		// String log = "[" + LogManager.getCurrentTimeAndDate() + "] [" + this.name +
		// "] " + l + ": ";
		return String.format("[%s] [%s/%s] %s: ", LogManager.getCurrentTimeAndDate(), Thread.currentThread().getName(),
				this.name, l);
	}

	private boolean checkAllowed(LogLevel l) {
		return (LogManager.getCurrentLevel() == l || LogManager.getCurrentLevel() == LogLevel.ALL
				|| (LogManager.isLevelAllowed(l) || LogManager.isLevelAllowed(LogLevel.ALL)));
	}

	public synchronized boolean finest(String msg) {
		if (checkAllowed(LogLevel.FINEST)) {
			println(this.formatLog(LogLevel.FINEST) + msg);
			return (true);
		}
		return (false);
	}

	public synchronized void finest(String msg, Throwable e) {
		if (finest(msg)) {
			// e.printStackTrace();
			println(e);
		}
	}

	public synchronized boolean fine(String msg) {
		if (checkAllowed(LogLevel.FINE)) {
			println(this.formatLog(LogLevel.FINE) + msg);
			return (true);
		}
		return (false);
	}

	public synchronized void fine(String msg, Throwable e) {
		if (fine(msg)) {
			println(e);
		}
	}

	public synchronized boolean debug(String msg) {
		if (checkAllowed(LogLevel.DEBUG)) {
			println(this.formatLog(LogLevel.DEBUG) + msg);
			return (true);
		}
		return (false);
	}

	public synchronized void debug(String msg, Throwable e) {
		if (debug(msg)) {
			println(e);
		}
	}

	public synchronized boolean info(String msg) {
		if (checkAllowed(LogLevel.INFO)) {
			println(this.formatLog(LogLevel.INFO) + msg);
			return (true);
		}
		return (false);
	}

	public synchronized void info(String msg, Throwable e) {
		if (info(msg)) {
			println(e);
		}
	}

	public synchronized boolean warning(String msg) {
		if (checkAllowed(LogLevel.WARNING)) {
			println(this.formatLog(LogLevel.WARNING) + msg);
			return (true);
		}
		return (false);
	}

	public synchronized void warning(String msg, Throwable e) {
		if (warning(msg)) {
			println(e);
		}
	}

	public synchronized boolean error(String msg) {
		if (checkAllowed(LogLevel.ERROR)) {
			println(this.formatLog(LogLevel.ERROR) + msg);
			return (true);
		}
		return (false);
	}

	public synchronized void error(String msg, Throwable e) {
		if (error(msg)) {
			println(e);
		}
	}

}
