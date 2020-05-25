package net.abi.abisEngine.rendering.shaderManagement.compiler.parsers;

import java.util.HashMap;

import net.abi.abisEngine.rendering.shaderManagement.Shader.ShaderType;
import net.abi.abisEngine.rendering.shaderManagement.compiler.fileTypes.yaml.AEShaderFileYAML;

public class AEShaderContainer {

	// Just temporary.
	AEShaderFileYAML wrapper;

	String name, description;// , passName;
	// CommandBuffer commands;
	// Type of shader then name of shader then the source;
	HashMap<ShaderType, HashMap<String, String>> subPrograms;

}
