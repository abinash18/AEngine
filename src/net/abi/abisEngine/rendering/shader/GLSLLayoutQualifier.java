package net.abi.abisEngine.rendering.shader;

import java.util.HashMap;

import net.abi.abisEngine.rendering.shader.compiler.Tokens.Qualifiers;

public class GLSLLayoutQualifier {

	public HashMap<Qualifiers, String> layoutQualifierIDList;
	
	public GLSLLayoutQualifier() {
		this.layoutQualifierIDList = new HashMap<>();
	}
	
}
