package tests.game;

import net.abi.abisEngine.core.Main;
import net.abi.abisEngine.handlers.file.PathHandle;
import net.abi.abisEngine.handlers.file.PathType;
import net.abi.abisEngine.handlers.logging.LogLevel;
import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.rendering.shaderManagement.Shader;
import net.abi.abisEngine.rendering.shaderManagement.compiler.AEShaderCompiler;
import net.abi.abisEngine.rendering.shaderManagement.compiler.loader.AEShaderLoader;
import net.abi.abisEngine.rendering.windowManagement.models.EngineLoader;
import tests.game.windows.MainGame;

public class Start extends Main {

	static Start s;

	public static void main(String[] args) {
		// s = new Start();
		// s.run(args);
		LogManager.setCurrentLevel(LogLevel.ALL);
		AEShaderLoader.get(Shader.DEFAULT_SHADER_ASSET_DIRECTORY_PATH.resolveChild("wireframe.ae-shader"));
//		String s = "ss";
//		System.out.print(s.length());
//		System.out.print(s.charAt(s.length() - 1));
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
