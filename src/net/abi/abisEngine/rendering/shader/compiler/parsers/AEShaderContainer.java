package net.abi.abisEngine.rendering.shader.compiler.parsers;

import java.util.HashMap;

import net.abi.abisEngine.rendering.shader.Shader.ShaderType;
import net.abi.abisEngine.rendering.shader.compiler.fileTypes.yaml.AEShaderFileYAML;

public class AEShaderContainer {

	// Just temporary.
	AEShaderFileYAML wrapper;

	String name, description;// , passName;
	// CommandBuffer commands;
	// Type of shader then name of shader then the source;
	HashMap<ShaderType, HashMap<String, String>> subPrograms;

}
