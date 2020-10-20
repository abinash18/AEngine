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

import net.abi.abisEngine.math.Matrix4f;
import net.abi.abisEngine.math.Vector3f;

public class Camera extends SceneComponent {

	private Matrix4f projection;

	public Camera(float fov, float aspectRatio, float zNear, float zFar, String name) {
		this.projection = new Matrix4f().initProjection(fov, aspectRatio, zNear, zFar);
		super.setName(name);
	}

	public Camera(float left, float right, float bottom, float top, float zNear, float zFar, String name) {
		this.projection = new Matrix4f().initOrthographic(left, right, bottom, top, zNear, zFar);
		super.setName(name);
	}

	public Camera(Vector3f pos, Vector3f forward, Vector3f up, String name) {
		up.normalize();
		forward.normalize();
		super.setName(name);
	}

	public Matrix4f getViewProjection() {
		Matrix4f cameraRotationMatrix = super.getTransform().getTransformedRotation().conjugate().toRotationMatrix();
		/*
		 * Doing Negative multiplication here to eradicate the use of it in the return
		 * statement.
		 */
		Vector3f cameraPosition = super.getTransform().getTransformedPosition().mul(-1);
		Matrix4f cameraTranslationMatrix = new Matrix4f().initTranslation(cameraPosition.x(), cameraPosition.y(),
				cameraPosition.z());
		return projection.mul(cameraRotationMatrix.mul(cameraTranslationMatrix));
	}

	public Matrix4f getViewOrthographic() {
		Matrix4f cameraRotationMatrix = super.getTransform().getTransformedRotation().conjugate().toRotationMatrix();
		/*
		 * Doing Negative multiplication here to eradicate the use of it in the return
		 * statement.
		 */
		Vector3f cameraPosition = super.getTransform().getTransformedPosition().mul(-1);
		Matrix4f cameraTranslationMatrix = new Matrix4f().initTranslation(cameraPosition.x(), cameraPosition.y(),
				cameraPosition.z());
		return projection.mul(cameraRotationMatrix.mul(cameraTranslationMatrix));
	}

	@Override
	public void input(float delta) {
		super.input(delta);
	}

	public Vector3f getLeft() {
		Vector3f left = super.getTransform().getRotation().getForward()
				.cross(super.getTransform().getRotation().getUp()).normalize();
		return (left);
	}

	public Vector3f getRight() {
		Vector3f right = super.getTransform().getRotation().getUp()
				.cross(super.getTransform().getRotation().getForward()).normalize();
		return (right);
	}

	@Override
	public void addToScene() {
		super.getParentScene().addCamera(this);
	}

	public void setProjection(Matrix4f projection) {
		this.projection = projection;
	}

	public Matrix4f getProjection() {
		return projection;
	}

}
