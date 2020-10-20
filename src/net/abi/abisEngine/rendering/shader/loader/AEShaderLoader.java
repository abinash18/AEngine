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
package net.abi.abisEngine.rendering.shader.loader;

import net.abi.abisEngine.rendering.shader.compiler.parser.AEShaderParserYAML;

public class AEShaderLoader {

	private static AEShaderParserYAML parser = new AEShaderParserYAML();
	//private static AEShaderCompiler compiler = new AEShaderCompiler();

	public static AEShaderParserYAML getParserInstance() {
		return parser;
	}

//	private static Logger logger = LogManager.getLogger(AEShaderLoader.class);
//
//	private static TwoFactorGenericCache<String, String, String> imports = new TwoFactorGenericCache<String, String, String>(
//			String.class, String.class, String.class);

//	public static Shader get(PathHandle file) {
//		AEShaderFileYAML yamlParsedFile = parser.parse(file);
//		// Implemnt command buffer loading here.
//		return compiler.compile();
//	}
}
