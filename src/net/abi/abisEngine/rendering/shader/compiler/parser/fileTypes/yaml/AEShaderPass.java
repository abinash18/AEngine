/*******************************************************************************
 * Copyright 2020 Abinash Singh | ABI INC.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.abi.abisEngine.rendering.shader.compiler.parser.fileTypes.yaml;

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
