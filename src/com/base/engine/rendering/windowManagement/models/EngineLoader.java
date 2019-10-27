package com.base.engine.rendering.windowManagement.models;

import com.base.engine.rendering.sceneManagement.scenes.EngineSplashScreen;
import com.base.engine.rendering.windowManagement.GLFWWindow;

public class EngineLoader extends GLFWWindow {

	public EngineLoader() {
		super(827, 196, "EngineSplash", "", false, false);
	}

	@Override
	protected void addScenes() {
		new EngineSplashScreen(this);
	}

	@Override
	protected void init() {

	}

	@Override
	public void update(float delta) {
		super.update(delta);

	}

	@Override
	protected void close() {
		// GLFWWindowManager.setCurrentWindow("EngineSplash");
	}

}
