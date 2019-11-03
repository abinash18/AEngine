package tests.game.windows;

import net.abi.abisEngine.rendering.windowManagement.GLFWWindow;
import net.abi.abisEngine.rendering.windowManagement.GLFWWindowManager;
import tests.game.scenes.MainMenu;
import tests.game.scenes.TestGame;

public class MainGame extends GLFWWindow {

	public MainGame() {
		super(800, 600, "MainGame", "Project Quaternion | pre-alpha release", false, false);
	}

	@Override
	protected void addScenes() {
		new TestGame(this);
		new MainMenu(this);
	}

	@Override
	protected void init() {
		// super.getSceneManager().initScenes();
	}

	@Override
	protected void close() {
		GLFWWindowManager.raiseStopFlag();
	}

}
