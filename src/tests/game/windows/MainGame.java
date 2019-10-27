package tests.game.windows;

import com.base.engine.rendering.windowManagement.GLFWWindow;
import com.base.engine.rendering.windowManagement.GLFWWindowManager;

import tests.game.scenes.MainMenu;
import tests.game.scenes.TestGame;

public class MainGame extends GLFWWindow {

	public MainGame() {
		super(800, 600, "MainGame", "Game", false, false);
	}

	@Override
	protected void addScenes() {
		new TestGame(this);
		new MainMenu(this);
	}

	@Override
	protected void init() {

	}

	@Override
	protected void close() {
		GLFWWindowManager.setStopRequested();
	}

}
