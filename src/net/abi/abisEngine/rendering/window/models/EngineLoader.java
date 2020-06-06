package net.abi.abisEngine.rendering.window.models;

import net.abi.abisEngine.handlers.file.PathHandle;
import net.abi.abisEngine.rendering.image.AEImage;
import net.abi.abisEngine.rendering.renderPipeline.RenderingEngine;
import net.abi.abisEngine.rendering.scene.scenes.EngineSplashScreen;
import net.abi.abisEngine.rendering.window.GLFWWindow;
import net.abi.abisEngine.rendering.window.GLFWWindowManager;
import net.abi.abisEngine.util.exceptions.AECursorInitializationException;
import net.abi.abisEngine.util.exceptions.AEImageManipulationException;
import tests.game.scenes.MainMenu;
import tests.game.windows.MainGame;

public class EngineLoader extends GLFWWindow {

	public EngineLoader() {
		super(800, 600, "EngineSplash", "", false, false);
		super.properties.sc_height = 600;
		super.properties.sc_width = 800;
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
		AEImage i = new AEImage(new PathHandle("./res/textures/cursor.png"));

		try {
			// i.loadImage();
			i = AEImage.resize(i, 32, 32);
			super.setWindowIcon(i);
			super.setCursor(new StaticCursor("s", i, 0, 0));
			// } catch (AECursorInitializationException e) {
			// e.printStackTrace();
		} catch (AEImageManipulationException | AECursorInitializationException e) {
			e.printStackTrace();
		}
	}

}
