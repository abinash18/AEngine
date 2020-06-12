package net.abi.abisEngine.rendering.shader.loader;

import net.abi.abisEngine.handlers.file.PathHandle;
import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;
import net.abi.abisEngine.rendering.shader.Shader;
import net.abi.abisEngine.rendering.shader.compiler.AEShaderCompiler;
import net.abi.abisEngine.rendering.shader.parser.AEShaderParserYAML;
import net.abi.abisEngine.rendering.shader.parser.fileTypes.yaml.AEShaderFileYAML;
import net.abi.abisEngine.util.cacheing.TwoFactorGenericCache;

public class AEShaderLoader {

	private static AEShaderParserYAML parser = new AEShaderParserYAML();
	//private static AEShaderCompiler compiler = new AEShaderCompiler();

	public static AEShaderParserYAML getParserInstance() {
		return parser;
	}

	private static Logger logger = LogManager.getLogger(AEShaderLoader.class);

	private static TwoFactorGenericCache<String, String, String> imports = new TwoFactorGenericCache<String, String, String>(
			String.class, String.class, String.class);

//	public static Shader get(PathHandle file) {
//		AEShaderFileYAML yamlParsedFile = parser.parse(file);
//		// Implemnt command buffer loading here.
//		return compiler.compile();
//	}
}
