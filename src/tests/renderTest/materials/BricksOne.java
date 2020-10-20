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
package tests.renderTest.materials;

import net.abi.abisEngine.rendering.material.Material;
import net.abi.abisEngine.rendering.texture.Texture;

public class BricksOne extends Material {

	public BricksOne() {
		super();
		super.addTexture("diffuse", new Texture("bricks.jpg").load());
		super.addTexture("normal_map", new Texture("bricks_normal.jpg").load());
		super.addFloat("specularIntensity", 0.25f);
		super.addFloat("specularPower", 12);
	}

}
