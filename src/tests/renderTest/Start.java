package tests.renderTest;

import net.abi.abisEngine.core.CoreEngine;
import net.abi.abisEngine.core.Main;
import net.abi.abisEngine.rendering.window.models.EngineLoader;
import net.abi.abisEngine.util.exceptions.AEGLFWWindowInitializationException;

public class Start extends Main {

	static Start s;

	public static void main(String[] args) {
		// LogManager.setCurrentLevel(LogLevel.DEBUG);
		s = new Start();
		s.run(args);
		//System.out.println(Runtime.get);
		// PathHandle p =
		// AEShader.DEFAULT_SHADER_ASSET_DIRECTORY_PATH.resolveChild("frameworkTest.ae-shader");
		// AEShaderCompiler.compile(AEShaderParserYAML.parse(p), p);
		// GL_CLIPPING_OUTPUT_PRIMITIVES
	}

	@Override
	public void run(String[] args) {
		super.run(args);
	}

	@Override
	protected void openStartingWindow(CoreEngine e) {
		try {
			e.getWindowManager().openWindow(new EngineLoader());
		} catch (AEGLFWWindowInitializationException e1) {
			e1.printStackTrace();
		}
	}

}
