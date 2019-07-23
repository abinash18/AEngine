package com.base.engine.handlers.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.base.engine.handlers.logging.LogManager;
import com.base.engine.handlers.logging.Logger;

public class FileHandler {

	private static Logger logger = LogManager.getLogger(FileHandler.class.getName());
	private String fileName, filePath;
	private File file;
	public PrintWriter out;
	private BufferedReader bufferedReader;
	private boolean append, autoFlush = true;

	/**
	 * 
	 * @param fileName Needs to have the name of the file with the file extension
	 *                 included
	 * @param filePath The complete path to the file or relative ./
	 * @throws IOException
	 */
	public FileHandler(String fileName, String filePath) throws IOException {
		this.fileName = fileName;
		this.filePath = filePath;
		this.file = new File(filePath + fileName);
		if (!checkFileExists()) {
			file.createNewFile();
		}
		this.append = false;
	}

	/**
	 * setAppend Needs To Be Called Before Initializing Writer If The File Already
	 * Exists. Otherwise It Will Overwrite The End Of The File.
	 * 
	 * @throws IOException
	 */
	public void initializeWriter() throws IOException {
		if (!checkFileExists()) {
			logger.debug("File Dose Not Exist Creating: " + filePath + fileName);
			file.createNewFile();
		}
		out = new PrintWriter(new FileOutputStream(file, append));
	}

	public void initializeReader() throws IOException {
		File file = new File(filePath + fileName);

		bufferedReader = new BufferedReader(new FileReader(file));

	}

	public boolean checkFileExists() {
		return file.exists();
	}

	public void write(String line) throws IOException {

		if (out == null) {
			new Exception("Buffered Writer Not Initialized.").printStackTrace();
			return;
		}

		System.out.println("ss");
		out.println(line);

	}

	public String readLine() throws IOException {

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

	public static Logger getLogger() {
		return logger;
	}

	public static void setLogger(Logger logger) {
		FileHandler.logger = logger;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public BufferedReader getBufferedReader() {
		return bufferedReader;
	}

	public void setBufferedReader(BufferedReader bufferedReader) {
		this.bufferedReader = bufferedReader;
	}

	public PrintWriter getOut() {
		return out;
	}

	public void setOut(PrintWriter out) {
		this.out = out;
	}

	public void setAppend(boolean append) {
		this.append = append;
	}

	/**
	 * @return the autoFlush
	 */
	public boolean isAutoFlush() {
		return autoFlush;
	}

	/**
	 * @param autoFlush the autoFlush to set
	 */
	public void autoFlush() {
		this.autoFlush = true;
	}

}
