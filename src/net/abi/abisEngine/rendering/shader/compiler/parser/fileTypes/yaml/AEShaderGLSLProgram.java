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

public class AEShaderGLSLProgram {
	String AE_SHADER_GLSL_PROGRAM_TYPE;
	String AE_SHADER_GLSL_PROGRAM_NAME;
	String AE_SHADER_GLSL_PROGRAM_SOURCE;

	public AEShaderGLSLProgram() {
	}

	public String getAE_SHADER_GLSL_PROGRAM_TYPE() {
		return AE_SHADER_GLSL_PROGRAM_TYPE;
	}

	public void setAE_SHADER_GLSL_PROGRAM_TYPE(String aE_PROGRAM_TYPE) {
		AE_SHADER_GLSL_PROGRAM_TYPE = aE_PROGRAM_TYPE;
	}

	public String getAE_SHADER_GLSL_PROGRAM_NAME() {
		return AE_SHADER_GLSL_PROGRAM_NAME;
	}

	public void setAE_SHADER_GLSL_PROGRAM_NAME(String aE_PROGRAM_NAME) {
		AE_SHADER_GLSL_PROGRAM_NAME = aE_PROGRAM_NAME;
	}

	public String getAE_SHADER_GLSL_PROGRAM_SOURCE() {
		return AE_SHADER_GLSL_PROGRAM_SOURCE;
	}

	public void setAE_SHADER_GLSL_PROGRAM_SOURCE(String aE_SHADER_PROGRAM_SOURCE) {
		AE_SHADER_GLSL_PROGRAM_SOURCE = aE_SHADER_PROGRAM_SOURCE;
	}
}
