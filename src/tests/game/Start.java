package tests.game;

import net.abi.abisEngine.core.Main;
import net.abi.abisEngine.handlers.file.PathHandle;
import net.abi.abisEngine.rendering.shaderManagement.compiler.loader.AEShaderLoader;
import net.abi.abisEngine.rendering.windowManagement.models.EngineLoader;
import tests.game.windows.MainGame;

public class Start extends Main {

	static Start s;

	public static void main(String[] args) {
		//s = new Start();
		//s.run(args);
		
		AEShaderLoader.LoadAndParse(new PathHandle("./res/shaders/wireframe/wireframe.ae-shader"));
		
	}

	@Override
	public void run(String[] args) {
		super.run(args);
	}

	@Override
	protected void addWindows() {
		new EngineLoader();
		new MainGame();
	}

}
