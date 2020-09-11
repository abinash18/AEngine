package net.abi.abisEngine.core;

import net.abi.abisEngine.handlers.logging.LogLevel;
import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;

public abstract class Main {

	private static Logger logger = LogManager.getLogger(Main.class.getName());

	protected abstract void openStartingWindow(CoreEngine e);

	public void run(String[] args) {
		int frameRate = 500;
		// Options opts = new Options();
		// LogManager.addFileHandler(1000);
		CoreEngine engine = new CoreEngine(frameRate);
		openStartingWindow(engine);
		engine.start();
	};

}
