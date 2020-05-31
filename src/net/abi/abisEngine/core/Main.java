package net.abi.abisEngine.core;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import net.abi.abisEngine.handlers.logging.LogLevel;
import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;
import net.abi.abisEngine.rendering.windowManagement.GLFWWindowManager;

public abstract class Main {

	private static Logger logger = LogManager.getLogger(Main.class.getName());

	protected abstract void addWindows();

	public void run(String[] args) {
		// int width = 800, height = 600, frameRate = 2000;
		int width = 1920, height = 1080, frameRate = 1000;
		boolean fullScreen = true, vSync = false;
		String windowTitle = "Project Quaternion | pre-alpha release";

		Options opts = new Options();
		LogManager.addFileHandler(1000);
		// LogManager.setCurrentLevel(LogLevel.ALL);
		// LogManager.addAllowedLevel(LogLevel.DEBUG);
//		LogManager.addAllowedLevel(LogLevel.INFO);
		// LogManager.addAllowedLevel(LogLevel.ERROR);
//		LogManager.addAllowedLevel(LogLevel.FINE);
//		LogManager.addAllowedLevel(LogLevel.WARNING);
//		LogManager.fileHandler.setAppend(false);

		opts.addOption(new Option("t", "title", true, "The Title Of The Frame."));
		opts.addOption(new Option("w", "width", true, "The Width Of The Frame."));
		opts.addOption(new Option("h", "height", true, "The Height Of The Frame."));
		opts.addOption(new Option("fps", "fps", true, "The Maximum Frame Rate Of The Engine."));
		// opts.addOption(new Option("r", "refresh rate", true, "The Maximum RefreshRate
		// Of The Engine."));
		opts.addOption(
				new Option("fs", "fullscreen", false, "Add This If The Scene Is To Be Launched In Full Screen Mode."));
		opts.addOption(new Option("vs", "VSync", false, "Add This If The Scene Is To Be Launched With VSync Mode."));

		CommandLine cmd = null;

		try {
			CommandLineParser parser = new DefaultParser();
			cmd = parser.parse(opts, args);
		} catch (ParseException e) {
			// System.err.println("Unable to parse given arguments.");
			// e.printStackTrace();
			logger.error("Unable to parse given arguments.", e);
			System.exit(1);

		}

		if (cmd.hasOption("t")) {
			windowTitle = cmd.getOptionValue("t");
		}

		if (cmd.hasOption("fs")) {
			fullScreen = true;
		}

		if (cmd.hasOption("vs")) {
			vSync = true;
		}

		if (cmd.hasOption("w") && cmd.hasOption("h")) {
			width = Integer.parseInt(cmd.getOptionValue("w"));
			height = Integer.parseInt(cmd.getOptionValue("h"));
		}

		if (cmd.hasOption("fps")) {
			frameRate = Integer.parseInt(cmd.getOptionValue("fps"));
		}
		CoreEngine engine = new CoreEngine(frameRate);
		addWindows();
		// engine.initialize();
		engine.start();
	};

}
