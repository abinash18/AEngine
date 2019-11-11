package tests.game.windows;

import net.abi.abisEngine.rendering.windowManagement.GLFWWindow;
import tests.game.scenes.TestGame;

public class MainGame extends GLFWWindow {

	public MainGame() {
		super(800, 600, "MainGame", "Project Quaternion | pre-alpha release", false, false);
	}

	@Override
	protected void addScenes() {
		new TestGame(this);
	}

	@Override
	protected void init() {
		// super.getSceneManager().initScenes();
	}

	@Override
	protected void close() {
	}

}
