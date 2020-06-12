package net.abi.abisEngine.rendering.window.models;

import net.abi.abisEngine.handlers.file.PathHandle;
import net.abi.abisEngine.rendering.image.AEImage;
import net.abi.abisEngine.rendering.renderPipeline.RenderingEngine;
import net.abi.abisEngine.rendering.scene.scenes.EngineSplashScreen;
import net.abi.abisEngine.rendering.window.GLFWWindow;
import net.abi.abisEngine.rendering.window.GLFWWindowManager;
import net.abi.abisEngine.util.exceptions.AECursorInitializationException;
import net.abi.abisEngine.util.exceptions.AEImageManipulationException;
import tests.renderTest.scenes.MainMenu;

public class EngineLoader extends GLFWWindow {

	public EngineLoader() {
		super();
		super.properties.name = "EngineSplash";
		super.properties.title = "AEngine";
		super.properties.sc_height = 1080;
		super.properties.sc_width = 1920;
		super.properties.fullscreen = true;
		super.properties.vSync = 0;
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
		GLFWWindowManager.raiseStopFlag();
	}

	@Override
	protected void post_init() {
		super.centerWindow();
		super.showWindow();
		AEImage i = new AEImage(new PathHandle("./res/icons/1x/ae-generic-icon-256x256.png"));
		try {
			i = AEImage.resize(i, 32, 32);
			super.setWindowIcon(i);
			super.setCursor(new StaticCursor("s", i, 0, 0));
		} catch (AEImageManipulationException | AECursorInitializationException e) {
			e.printStackTrace();
		}
	}

}
