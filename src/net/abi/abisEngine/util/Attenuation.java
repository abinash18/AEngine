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
package net.abi.abisEngine.util;

import net.abi.abisEngine.math.vector.Vector3f;

public class Attenuation extends Vector3f {

	public Attenuation(float constant, float linear, float exponent) {
		super(constant, linear, exponent);
	}

	public float getConstant() {
		return super.x();
	}

	public void setConstant(float constant) {
		super.setX(constant);
	}

	public float getLinear() {
		return super.y();
	}

	public void setLinear(float linear) {
		super.setY(linear);
	}

	public float getExponent() {
		return super.z();
	}

	public void setExponent(float exponent) {
		super.setZ(exponent);
	}

}
