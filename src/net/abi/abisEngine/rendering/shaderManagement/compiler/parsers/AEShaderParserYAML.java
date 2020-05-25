package net.abi.abisEngine.rendering.shaderManagement.compiler.parsers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import net.abi.abisEngine.handlers.file.PathHandle;
import net.abi.abisEngine.rendering.shaderManagement.Shader.ShaderType;
import net.abi.abisEngine.rendering.shaderManagement.compiler.fileTypes.yaml.AEShaderFileYAML;
import net.abi.abisEngine.rendering.shaderManagement.compiler.fileTypes.yaml.AEShaderGLSLProgram;

public class AEShaderParserYAML implements AEShaderParserI {

	@Override
	public AEShaderContainer parse(PathHandle file) {
		AEShaderContainer con = new AEShaderContainer();
		Yaml yaml = new Yaml(new Constructor(AEShaderFileYAML.class));
		AEShaderFileYAML parsedFile = null;
		try {
			parsedFile = yaml.load(Files.newInputStream(Paths.get(file.getFileInstance().getAbsolutePath())));
		} catch (IOException e) {
			e.printStackTrace();
		}

		con.name = parsedFile.getAE_SHADER_NAME();
		con.description = parsedFile.getAE_SHADER_DESC();
		// con.passName = parsedFile.getAE_SHADER_PASS().getAE_SHADER_PASS_TAG();
		HashMap<ShaderType, HashMap<String, String>> subprgms = new HashMap<ShaderType, HashMap<String, String>>();

		for (AEShaderGLSLProgram p : parsedFile.getAE_SHADER_GLSL_PROGRAMS()) {
			HashMap<String, String> t = new HashMap<String, String>();
			System.out.println(p.getAE_SHADER_GLSL_PROGRAM_SOURCE());
			t.put(p.getAE_SHADER_GLSL_PROGRAM_NAME(), p.getAE_SHADER_GLSL_PROGRAM_SOURCE());

			subprgms.put(ShaderType.valueOf(p.getAE_SHADER_GLSL_PROGRAM_TYPE()), t);
		}

		return con;
	}

}
