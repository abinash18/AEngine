package net.abi.abisEngine.rendering.shader;

public class GLSLUniformBlockObjectData {
	private String name;
	private GLSLLayoutQualifier qualifiers;
	private GLSLUniform[] uniforms;

	public GLSLUniformBlockObjectData(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public GLSLLayoutQualifier getQualifiers() {
		return qualifiers;
	}

	public void setQualifiers(GLSLLayoutQualifier qualifiers) {
		this.qualifiers = qualifiers;
	}

	public GLSLUniform[] getUniforms() {
		return uniforms;
	}

	public void setUniforms(GLSLUniform[] uniforms) {
		this.uniforms = uniforms;
	}
}