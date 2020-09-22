package tests.renderTest.windows;

import net.abi.abisEngine.rendering.pipeline.RenderingEngine;
import net.abi.abisEngine.rendering.window.GLFWWindow;
import net.abi.abisEngine.rendering.window.GLFWWindowManager;
import tests.renderTest.scenes.TestGame;

public class MainGame extends GLFWWindow {

	public MainGame() {
		super();
		super.properties.GLFWWindowProperties.setName("EngineSplash");
		super.properties.GLFWWindowProperties.setTitle("AEngine");
		super.properties.GLFWWindowProperties.setSc_height(720);
		super.properties.GLFWWindowProperties.setSc_width(1270);
		super.properties.GLFWWindowProperties.setFullscreen(false);
		super.properties.GLFWWindowProperties.setvSync(GLFW_FALSE);
		super.properties.GLFWWindowProperties.setRenderEngine(new RenderingEngine());
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
