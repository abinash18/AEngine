package net.abi.abisEngine.rendering.shader.parser.fileTypes.yaml;

import java.util.List;

public class AEShaderPass {
	String AE_SHADER_PASS_TAG;
	List<AEPrepProperty> AE_PREP_PROPERTIES;
	List<AEShaderGLSLProgram> AE_SHADER_GLSL_PROGRAMS;

	public AEShaderPass() {
	}

	public String getAE_SHADER_PASS_TAG() {
		return AE_SHADER_PASS_TAG;
	}

	public void setAE_SHADER_PASS_TAG(String aE_SHADER_PASS_TAG) {
		AE_SHADER_PASS_TAG = aE_SHADER_PASS_TAG;
	}

	public List<AEPrepProperty> getAE_PREP_PROPERTIES() {
		return AE_PREP_PROPERTIES;
	}

	public void setAE_PREP_PROPERTIES(List<AEPrepProperty> props) {
		this.AE_PREP_PROPERTIES = props;
	}

	public List<AEShaderGLSLProgram> getAE_SHADER_GLSL_PROGRAMS() {
		return AE_SHADER_GLSL_PROGRAMS;
	}

	public void setAE_SHADER_GLSL_PROGRAMS(List<AEShaderGLSLProgram> subPrograms) {
		this.AE_SHADER_GLSL_PROGRAMS = subPrograms;
	}
}
