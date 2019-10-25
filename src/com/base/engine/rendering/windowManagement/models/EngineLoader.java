package com.base.engine.rendering.windowManagement.models;

import com.base.engine.rendering.windowManagement.GLFWWindow;

import tests.game.scenes.MainMenu;
import tests.game.scenes.TestGame;

public class EngineLoader extends GLFWWindow {

	public EngineLoader(int width, int height, String name, String title, boolean fullscreen, boolean vSync) {
		super(width, height, name, title, fullscreen, vSync);
	}

	@Override
	protected void addScenes() {
		new TestGame();
		new MainMenu();
	}

}
