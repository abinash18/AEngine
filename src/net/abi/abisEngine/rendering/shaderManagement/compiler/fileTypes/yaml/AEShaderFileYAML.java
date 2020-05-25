package net.abi.abisEngine.rendering.shaderManagement.compiler.fileTypes.yaml;

import java.util.List;

/**
 * This class defines the structure for the .ae-shader yaml files.
 * 
 * @author abina
 *
 */
public class AEShaderFileYAML {
	String AE_SHADER_NAME, AE_SHADER_DESC;
	// AEShaderPass AE_SHADER_PASS;
	String AE_SHADER_PASS_TAG;
	List<AEPrepProperties> AE_PREP_PROPERTIES;
	List<AEShaderGLSLProgram> AE_SHADER_GLSL_PROGRAMS;

	public AEShaderFileYAML() {
	}

	public String getAE_SHADER_PASS_TAG() {
		return AE_SHADER_PASS_TAG;
	}

	public void setAE_SHADER_PASS_TAG(String aE_SHADER_PASS_TAG) {
		AE_SHADER_PASS_TAG = aE_SHADER_PASS_TAG;
	}

	public List<AEPrepProperties> getAE_PREP_PROPERTIES() {
		return AE_PREP_PROPERTIES;
	}

	public void setAE_PREP_PROPERTIES(List<AEPrepProperties> props) {
		this.AE_PREP_PROPERTIES = props;
	}

	public List<AEShaderGLSLProgram> getAE_SHADER_GLSL_PROGRAMS() {
		return AE_SHADER_GLSL_PROGRAMS;
	}

	public void setAE_SHADER_GLSL_PROGRAMS(List<AEShaderGLSLProgram> subPrograms) {
		this.AE_SHADER_GLSL_PROGRAMS = subPrograms;
	}

	public String getAE_SHADER_NAME() {
		return AE_SHADER_NAME;
	}

	public void setAE_SHADER_NAME(String aE_SHADER_NAME) {
		AE_SHADER_NAME = aE_SHADER_NAME;
	}

	public String getAE_SHADER_DESC() {
		return AE_SHADER_DESC;
	}

	public void setAE_SHADER_DESC(String aE_SHADER_DESC) {
		AE_SHADER_DESC = aE_SHADER_DESC;
	}

//	public AEShaderPass getAE_SHADER_PASS() {
//		return AE_SHADER_PASS;
//	}
//
//	public void setAE_SHADER_PASS(AEShaderPass aE_SHADER_PASS) {
//		AE_SHADER_PASS = aE_SHADER_PASS;
//	}

}
