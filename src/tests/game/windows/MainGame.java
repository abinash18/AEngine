package tests.game.windows;

import net.abi.abisEngine.rendering.window.GLFWWindow;
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
	protected void pre_init() {
		// super.getSceneManager().initScenes();
	}

	@Override
	protected void close() {
	}

	@Override
	protected void post_init() {
		// TODO Auto-generated method stub

	}

}
