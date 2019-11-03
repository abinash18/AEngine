package net.abi.abisEngine.rendering.windowManagement.models;

import net.abi.abisEngine.rendering.sceneManagement.scenes.EngineSplashScreen;
import net.abi.abisEngine.rendering.windowManagement.GLFWWindow;
import net.abi.abisEngine.rendering.windowManagement.GLFWWindowManager;
import tests.game.windows.MainGame;

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
		GLFWWindowManager.openWindow(new MainGame());
	}

}
