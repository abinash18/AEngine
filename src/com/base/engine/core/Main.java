package com.base.engine.core;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {

	public static void main(String[] args) {

		int width = 800, height = 600, frameRate = 60;
		boolean fullScreen = false, vSync = false;
		String windowTitle = "Project Quaternion | pre-alpha release";

		Options opts = new Options();

		opts.addOption(new Option("t", "title", true, "The Title Of The Frame."));
		opts.addOption(new Option("w", "width", true, "The Width Of The Frame."));
		opts.addOption(new Option("h", "height", true, "The Height Of The Frame."));
		opts.addOption(new Option("fps", "fps", true, "The Maximum Frame Rate Of The Engine."));
		// opts.addOption(new Option("r", "refreshrate", true, "The Maximum RefreshRate
		// Of The Engine."));
		opts.addOption(
				new Option("fs", "fullscreen", false, "Add This If The Game Is To Be Launched In Full Screen Mode."));
		opts.addOption(new Option("vs", "VSync", false, "Add This If The Game Is To Be Launched With VSync Mode."));

		CommandLine cmd = null;

		try {
			CommandLineParser parser = new DefaultParser();
			cmd = parser.parse(opts, args);
		} catch (ParseException e) {
			System.err.println("Unable to parse given arguments.");
			e.printStackTrace();
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

		TestGame game = new TestGame();

		CoreEngine engine = new CoreEngine(frameRate, game);
		engine.createWindow(width, height, windowTitle, fullScreen, vSync);
		engine.start();
	}

}