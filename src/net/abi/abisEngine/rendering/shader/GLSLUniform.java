package net.abi.abisEngine.rendering.shader;

public class GLSLUniform {
	public String name;
	public String type;
	public GLSLLayoutQualifier qualifiers;
	/*
	 * TODO: Use this as a flag to determine if this uniform is bound. If the value
	 * is -1 then its not bound.
	 */
	public int boundLocation = -1;

	public GLSLUniform() {
		this.name = "";
		this.type = "";
		this.qualifiers = new GLSLLayoutQualifier();
	}

}
