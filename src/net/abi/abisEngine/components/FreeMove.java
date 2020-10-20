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
package net.abi.abisEngine.components;

import static net.abi.abisEngine.input.GLFWInput.GLFW_KEY_A;
import static net.abi.abisEngine.input.GLFWInput.GLFW_KEY_D;
import static net.abi.abisEngine.input.GLFWInput.GLFW_KEY_S;
import static net.abi.abisEngine.input.GLFWInput.GLFW_KEY_W;

import net.abi.abisEngine.input.GLFWInput;
import net.abi.abisEngine.input.GLFWMouseAndKeyboardInput;
import net.abi.abisEngine.math.Vector3f;

public class FreeMove extends SceneComponent {

	private int forwardKey, backKey, leftKey, rightKey;
	private float speed;

	public FreeMove(int forwardKey, int backKey, int leftKey, int rightKey, float speed) {
		this.forwardKey = forwardKey;
		this.backKey = backKey;
		this.leftKey = leftKey;
		this.rightKey = rightKey;
		this.speed = speed;
	}

	public FreeMove(float speed) {
		this(GLFW_KEY_W, GLFW_KEY_S, GLFW_KEY_A, GLFW_KEY_D, speed);
		this.speed = speed;
	}

	@Override
	public void input(float delta) {

		float moveAmount = (float) (speed * delta);

		/*
		 * Add Any Motivations To Movement Before The Move Method Is Called. NOTE: You
		 * Do Not Need To Use Input.getKeyDown Because It Automatically Returns The Key
		 * If It Is Down.
		 */
		if (((GLFWMouseAndKeyboardInput) super.getParentScene().getInputController())
				.isKeyDown((GLFWInput.GLFW_KEY_LEFT_SHIFT))) {
			moveAmount = (float) (5 * delta);
		}

		if (((GLFWMouseAndKeyboardInput) super.getParentScene().getInputController()).isKeyHeldDown(((forwardKey)))) {
			move(super.getTransform().getRotation().getForward(), moveAmount);
		}
		if (((GLFWMouseAndKeyboardInput) super.getParentScene().getInputController()).isKeyHeldDown(((leftKey)))) {
			move(super.getTransform().getRotation().getLeft(), moveAmount);
		}
		if (((GLFWMouseAndKeyboardInput) super.getParentScene().getInputController()).isKeyHeldDown(((rightKey)))) {
			move(super.getTransform().getRotation().getRight(), moveAmount);
		}
		if (((GLFWMouseAndKeyboardInput) super.getParentScene().getInputController()).isKeyHeldDown(((backKey)))) {
			move(super.getTransform().getRotation().getForward(), -moveAmount);
		}

	}

	public void move(Vector3f dir, float amt) {
		super.getTransform().setPosition(super.getTransform().getPosition().add(dir.mul(amt)));
	}

	public int getForwardKey() {
		return forwardKey;
	}

	public void setForwardKey(int forwardKey) {
		this.forwardKey = forwardKey;
	}

	public int getBackKey() {
		return backKey;
	}

	public void setBackKey(int backKey) {
		this.backKey = backKey;
	}

	public int getLeftKey() {
		return leftKey;
	}

	public void setLeftKey(int leftKey) {
		this.leftKey = leftKey;
	}

	public int getRightKey() {
		return rightKey;
	}

	public void setRightKey(int rightKey) {
		this.rightKey = rightKey;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}
}
