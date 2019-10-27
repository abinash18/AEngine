package tests.game;

import com.base.engine.core.Main;

import tests.game.windows.MainGame;

public class Start extends Main {

	static Start s;

	public static void main(String[] args) {
		s = new Start();
		s.run(args);
	}

	@Override
	public void run(String[] args) {
		super.run(args);
	}

	@Override
	protected void addWindows() {
		new MainGame();
	}

}
