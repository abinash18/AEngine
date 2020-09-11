package tests.renderTest.windows;

import net.abi.abisEngine.rendering.pipeline.RenderingEngine;
import net.abi.abisEngine.rendering.window.GLFWWindow;
import net.abi.abisEngine.rendering.window.GLFWWindowManager;
import tests.renderTest.scenes.TestGame;

public class MainGame extends GLFWWindow {

	public MainGame() {
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
		new TestGame(this);
	}

	@Override
	protected void pre_init() {
	}

	@Override
	protected void close() {
		GLFWWindowManager.raiseStopFlag();
	}

	@Override
	protected void post_init() {
		//super.centerWindow();
		//super.showWindow();
	}

}
