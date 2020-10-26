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
package net.abi.abisEngine.rendering.shader.compiler.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import net.abi.abisEngine.handlers.file.PathHandle;
import net.abi.abisEngine.rendering.shader.compiler.parser.fileTypes.yaml.AEShaderFileYAML;

public class AEShaderParserYAML {

	public static AEShaderFileYAML parse(PathHandle file) {
		Yaml yaml = new Yaml(new Constructor(AEShaderFileYAML.class));
		AEShaderFileYAML parsedFile = null;
		try {
			parsedFile = yaml.load(Files.newInputStream(Paths.get(file.getFileInstance().getAbsolutePath())));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (parsedFile.getAE_SHADER_NAME().equals("!!AE_SHADER_FILE_NAME")) {
			parsedFile.setAE_SHADER_NAME(file.getNameWithoutExtension());
		}
		return parsedFile;
	}
}
