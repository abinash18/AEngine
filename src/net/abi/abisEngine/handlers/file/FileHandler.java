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
package net.abi.abisEngine.handlers.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;

public class FileHandler {

	private static Logger logger = LogManager.getLogger(FileHandler.class.getName());
	final String fileName, filePath;
	private File file;
	public PrintWriter out;
	private BufferedReader bufferedReader;
	private boolean append;
	private int maxLines;

	/**
	 * 
	 * @param fileName Needs to have the name of the file with the file extension
	 *                 included
	 * @param filePath The complete path to the file or relative ./
	 * @throws IOException
	 */
	public FileHandler(String fileName, String filePath) throws Exception {
		this.fileName = fileName;
		this.filePath = filePath;
		this.file = new File(filePath + fileName);
		if (!checkFileExists()) {
			file.createNewFile();
		}
		this.append = false;
		this.maxLines = 0;
	}

	/**
	 * setAppend Needs To Be Called Before Initializing Writer If The File Already
	 * Exists. Otherwise It Will Overwrite The End Of The File.
	 * 
	 * @throws IOException
	 */
	public synchronized void initializeWriter(boolean autoFlush) throws Exception {
		if (!checkFileExists()) {
			logger.debug("File Dose Not Exist Creating: " + filePath + fileName);
			file.createNewFile();
		}
		out = new PrintWriter(new FileOutputStream(file, append), autoFlush);
	}

	/**
	 * setAppend Needs To Be Called Before Initializing Writer If The File Already
	 * Exists. Otherwise It Will Overwrite The End Of The File.
	 * 
	 * @throws IOException
	 */
	public synchronized void initializeWriter(boolean autoFlush, int maxLines) throws Exception {
		if (!checkFileExists()) {
			logger.debug("File Dose Not Exist Creating: " + filePath + fileName);
			file.createNewFile();
		}
		this.maxLines = maxLines;
		out = new PrintWriter(new FileOutputStream(file, append), autoFlush);
	}

	public synchronized void initializeReader() throws IOException {
		File file = new File(filePath + fileName);

		bufferedReader = new BufferedReader(new FileReader(file));

	}

	public boolean checkFileExists() {
		return file.exists();
	}

	public String readLine() throws Exception {

		if (bufferedReader == null) {
			new Exception("Buffered Writer Not Initialized.").printStackTrace();
			return null;
		}

		return bufferedReader.readLine();

	}

	public void destroy() {
		this.finalize();
	}

	@Override
	public void finalize() {
		try {
			super.finalize();
			out.close();
			bufferedReader.close();
		} catch (Throwable e) {
			logger.error("Unable to finalize file handler.", e);
			// e.printStackTrace();
		}
	}

	public boolean isAppend() {
		return append;
	}

	public void setAppend(boolean append) {
		this.append = append;
	}

	public int getMaxLines() {
		return maxLines;
	}
}
