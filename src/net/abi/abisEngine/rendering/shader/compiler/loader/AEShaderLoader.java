package net.abi.abisEngine.rendering.shader.compiler.loader;

import net.abi.abisEngine.handlers.file.PathHandle;
import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;
import net.abi.abisEngine.rendering.shader.Shader;
import net.abi.abisEngine.rendering.shader.compiler.AEShaderCompiler;
import net.abi.abisEngine.rendering.shader.compiler.fileTypes.yaml.AEShaderFileYAML;
import net.abi.abisEngine.rendering.shader.compiler.parsers.AEShaderParserYAML;
import net.abi.abisEngine.util.cacheing.TwoFactorGenericCache;

public class AEShaderLoader {

	private static AEShaderParserYAML parser = new AEShaderParserYAML();
	private static AEShaderCompiler compiler = new AEShaderCompiler();

	public static AEShaderParserYAML getParserInstance() {
		return parser;
	}

	private static Logger logger = LogManager.getLogger(AEShaderLoader.class);

	private static TwoFactorGenericCache<String, String, String> imports = new TwoFactorGenericCache<String, String, String>(
			String.class, String.class, String.class);

	public static enum ShaderType {
		AE_VERTEX_SHADER, AE_FRAGMENT_SHADER, AE_GEOMETRY_SHADER, AE_TESSELATION_EVALUATION_SHADER,
		AE_TESSELATION_CONTROL_SHADER, AE_COMPUTE_SHADER, AE_HEADER_FILE, AE_SHADER_IMPORT;
	}

	public static Shader get(PathHandle file) {
		AEShaderFileYAML yamlParsedFile = parser.parse(file);
		// Implemnt command buffer loading here.
		return compiler.compile(yamlParsedFile);
	}
}
