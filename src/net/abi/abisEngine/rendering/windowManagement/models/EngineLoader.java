package net.abi.abisEngine.rendering.windowManagement.models;

import net.abi.abisEngine.rendering.pipelineManagement.RenderingEngine;
import net.abi.abisEngine.rendering.sceneManagement.scenes.EngineSplashScreen;
import net.abi.abisEngine.rendering.windowManagement.GLFWWindow;
import net.abi.abisEngine.rendering.windowManagement.GLFWWindowManager;
import tests.game.scenes.MainMenu;
import tests.game.windows.MainGame;

public class EngineLoader extends GLFWWindow {

	public EngineLoader() {
		super(800, 600, "EngineSplash", "", false, false);
		super.properties.sc_height = 600;
		super.properties.sc_height = 800;
		super.properties.renderEngine = new RenderingEngine();
	}

	@Override
	protected void addScenes() {
		new EngineSplashScreen(this);
		new MainMenu(this);
	}

	@Override
	protected void pre_init() {

	}

	@Override
	public void update(float delta) {
		super.update(delta);
	}

	@Override
	protected void close() {
		/*
		 * try { GLFWWindowManager.openWindow(new MainGame(), NULL,
		 * this.getGlfw_Handle()); } catch (Exception e) { e.printStackTrace(); }
		 */
	}

	@Override
	protected void post_init() {
		super.centerWindow();
		super.showWindow();
		// super.setWindowOpacity(0.5f);
	}

}
