package tests.renderTest;

import org.lwjgl.opengl.GL45;

import net.abi.abisEngine.core.Main;
import net.abi.abisEngine.handlers.file.PathHandle;
import net.abi.abisEngine.handlers.logging.LogLevel;
import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.rendering.shader.AEShader;
import net.abi.abisEngine.rendering.shader.compiler.AEShaderCompiler;
import net.abi.abisEngine.rendering.shader.parser.AEShaderParserYAML;
import net.abi.abisEngine.rendering.window.models.EngineLoader;
import tests.renderTest.windows.MainGame;

public class Start extends Main {

	static Start s;

	public static void main(String[] args) {
		s = new Start();
		s.run(args);
		/*
		 * Command buffer test
		 */
		try {
			System.out.println(GL45.class.getField("GL_EQUAL"));
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// LogManager.setCurrentLevel(LogLevel.ALL);
		// PathHandle p =
		// AEShader.DEFAULT_SHADER_ASSET_DIRECTORY_PATH.resolveChild("wireframe.ae-shader");
		// AEShaderCompiler c = new AEShaderCompiler(AEShaderParserYAML.parse(p), p);
		// c.compile();
		// c.getShaderInstance();
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
