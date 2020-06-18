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
		super.properties.sc_height = 720;
		super.properties.sc_width = 1270;
		super.properties.fullscreen = false;
		super.properties.vSync = GLFW_FALSE;
		super.properties.renderEngine = new RenderingEngine();
	}

	@Override
	protected void addScenes() {
		new EngineSplashScreen(this);
		new MainMenu(this);
	}

	@Override
	protected void pre_init() {
		// super.addGLFWWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, GLFW_TRUE);
		// super.addGLFWWindowHint(GLFW_DECORATED, GLFW_FALSE);
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
		AEImage i = new AEImage(new PathHandle("./res/textures/cursor.png"));
		try {
			i = AEImage.resize(i, 32, 32);
			super.setWindowIcon(i);
			//super.setCursor(new StaticCursor("s", i, 0, 0));
			super.setCursor(new StaticCursor("normal"));
		} catch (AEImageManipulationException | AECursorInitializationException e) {
			e.printStackTrace();
		}
	}

}
