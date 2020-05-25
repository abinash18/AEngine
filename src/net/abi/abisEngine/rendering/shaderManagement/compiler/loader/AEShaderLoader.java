package net.abi.abisEngine.rendering.shaderManagement.compiler.loader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.abi.abisEngine.handlers.file.PathHandle;
import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;
import net.abi.abisEngine.rendering.shaderManagement.compiler.parsers.AEShaderContainer;
import net.abi.abisEngine.rendering.shaderManagement.compiler.parsers.AEShaderParserI;
import net.abi.abisEngine.rendering.shaderManagement.compiler.parsers.AEShaderParserYAML;
import net.abi.abisEngine.util.exceptions.AERuntimeException;

public class AEShaderLoader {

	private static Logger logger = LogManager.getLogger(AEShaderLoader.class);

	public static enum ShaderType {
		AE_VERTEX_SHADER(".glvs"), AE_FRAGMENT(".glfs"), AE_GEOMETRY_SHADER(".glgs"),
		AE_TESSELATION_EVALUATION_SHADER(".glte"), AE_TESSELATION_CONTROL_SHADER(".gltc"), AE_COMPUTE_SHADER(".glc"),
		AE_HEADER_FILE(".glh");

		public String extention;

		private ShaderType(String extention) {
			this.extention = extention;
		}

	}

	public static enum AEShaderFileType {
		/*
		 * These are singletons pretty much they only should have one instance which is
		 * stored here then destroyed after runtime.
		 */
		AE_SHADER_FILE_TYPE_YAML(new AEShaderParserYAML());// , AE_SHADER_FILE_TYPE_JSON();

		public AEShaderParserI parser;

		private AEShaderFileType(AEShaderParserI parser) {
			this.parser = parser;
		}
	}

	public static AEShaderContainer LoadAndParse(PathHandle file) {

		// TODO: Check to see if a shader with the same name already exists.

		AEShaderContainer shaderData = null;

		// Figure out the parser we need to use
		// This identifies the line which contains meta data about the shader always on
		// the first line of the file
		final String META_TAG_PREFIX = "??", COMMENT_PREFIX = "//";
		String metaTagLine;
		AEShaderFileType type;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file.getFileInstance()));
			metaTagLine = br.readLine();
//			if (metaTagLine.startsWith(META_TAG_PREFIX)) {
//				type = AEShaderFileType.valueOf(metaTagLine.substring(META_TAG_PREFIX.length(), metaTagLine.length()));
//			} else {
//				throw new AERuntimeException("Shader UnParsable: " + file.toString());
//			}

			shaderData = AEShaderFileType.AE_SHADER_FILE_TYPE_YAML.parser.parse(file);

		} catch (IOException e) {
			e.printStackTrace();
		}

		// Its pronounced pass eye
		// AEShaderPass[] pasie = loadShaderSource(shaderData);

		return shaderData;
	}
}
