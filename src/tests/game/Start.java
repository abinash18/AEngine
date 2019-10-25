package tests.game;

import com.base.engine.core.Main;
import com.base.engine.rendering.windowManagement.models.EngineLoader;

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
		new EngineLoader(800, 600, "test", "s", false, false);
	}

}
