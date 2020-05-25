package net.abi.abisEngine.rendering.shaderManagement.compiler.parsers;

import net.abi.abisEngine.handlers.file.PathHandle;

public interface AEShaderParserI {

	public AEShaderContainer parse(PathHandle file);
	
}
