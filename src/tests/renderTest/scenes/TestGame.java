package tests.renderTest.scenes;

import net.abi.abisEngine.handlers.file.PathHandle;
import net.abi.abisEngine.rendering.scene.Scene;
import net.abi.abisEngine.rendering.shader.AEShader;
import net.abi.abisEngine.rendering.shader.compiler.AEShaderCompiler;
import net.abi.abisEngine.rendering.shader.parser.AEShaderParserYAML;
import net.abi.abisEngine.rendering.window.GLFWWindow;

public class TestGame extends Scene {

	public TestGame(GLFWWindow prnt) {
		super("TestGame", prnt);
	}

	public void init() {
		super.init();
		PathHandle p = AEShader.DEFAULT_SHADER_ASSET_DIRECTORY_PATH.resolveChild("wireframe.ae-shader");
		AEShaderCompiler.compile(AEShaderParserYAML.parse(p), p, true);
	}

	float temp = 0.0f;

	@Override
	public void update(float delta) {
		super.update(delta);
	}

	@Override
	public void input(float delta) {
		super.input(delta);
	}
}