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

public class AEDemoProperty {
	String AE_DEMO_PROPERTY_IDENTIFIER;
	String[] AE_DEMO_PROPERTY_PARAMETERS;

	public AEDemoProperty() {
	}

	public String getAE_DEMO_PROPERTY_IDENTIFIER() {
		return AE_DEMO_PROPERTY_IDENTIFIER;
	}

	public void setAE_DEMO_PROPERTY_IDENTIFIER(String aE_DEMO_PROPERTY_IDENTIFIER) {
		AE_DEMO_PROPERTY_IDENTIFIER = aE_DEMO_PROPERTY_IDENTIFIER;
	}

	public String[] getAE_DEMO_PROPERTY_PARAMETERS() {
		return AE_DEMO_PROPERTY_PARAMETERS;
	}

	public void setAE_DEMO_PROPERTY_PARAMETERS(String[] aE_DEMO_PROPERTY_PARAMETERS) {
		AE_DEMO_PROPERTY_PARAMETERS = aE_DEMO_PROPERTY_PARAMETERS;
	}

}
