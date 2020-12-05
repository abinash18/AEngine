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
package net.abi.abisEngine.rendering.pipeline;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL45;
import org.lwjgl.opengl.GLDebugMessageCallbackI;
import org.lwjgl.openvr.VREventInputBindingLoad;
import org.yaml.snakeyaml.reader.UnicodeReader;

import net.abi.abisEngine.components.Camera;
import net.abi.abisEngine.components.Light;
import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;
import net.abi.abisEngine.math.Transform;
import net.abi.abisEngine.math.vector.Vector3f;
import net.abi.abisEngine.rendering.gl.memory.GLUniform;
import net.abi.abisEngine.rendering.gl.memory.GLUniformBuffer;
import net.abi.abisEngine.rendering.material.Material;
import net.abi.abisEngine.rendering.scene.Scene;
import net.abi.abisEngine.rendering.shader.legacy.Shader;
import net.abi.abisEngine.util.Color;
import net.abi.abisEngine.util.Expendable;
import net.abi.abisEngine.util.MappedValues;

public class RenderingEngine extends MappedValues implements Expendable {

	private static final Logger logger = LogManager.getLogger(RenderingEngine.class.getName());

	public static boolean depth_test = false;
	public static Color c = Color.hex2Rgb("#b9bec1").normalize();
	private Map<String, Integer> samplerMap;
	private Map<String, GLUniformBuffer> globalUniformBuffer;
	GLUniformBuffer cameraBuffer;
	private Shader forwardAmbientShader, depthShader, edge_dectect;

	private Light activeLight;
	private Camera activeCamera;

	public RenderingEngine() {
		super();
		samplerMap = new HashMap<String, Integer>();
		samplerMap.put("diffuse", 0);
		samplerMap.put("normal_map", 1);
		globalUniformBuffer = new HashMap<>();
		globalUniformBuffer.put("Camera", (cameraBuffer = new GLUniformBuffer("Camera", 4 * 3)));
		cameraBuffer.addUniform("C_eyePos", new GLUniform("C_eyePos"));
		cameraBuffer.bindBufferBase(2);
		super.addVector3f("ambient", new Vector3f(0.1f, 0.1f, 0.1f));
	}

	public void updateUniformStruct(Transform transform, Material mat, Shader shader, String uniformName,
			String uniformType) {
		logger.error("'" + uniformType
				+ "' is not a valid Supported Type. Or is misspelled, please check shader program or change the prefix of the variable.",
				new IllegalArgumentException("'" + uniformType + "' is not a valid Supported Type."));
	}

	public static void clearScreen() {
		// TODO: Stencil Buffer
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
		GL11.glClearColor(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
	}

	public static void unBindTextures() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	public void initGraphics() {
		forwardAmbientShader = new Shader("forward-ambient");
		depthShader = new Shader("depthVisualizer");
		// edge_dectect = new Shader("visualizers/edge_detect");
		GL11.glEnable(GL45.GL_DEBUG_OUTPUT);
		GL45.glDebugMessageCallback(new GLDebugMessageCallbackI() {
			@Override
			public void invoke(int source, int type, int id, int severity, int length, long message, long userParam) {
				System.out.println("GL CALLBACK: " + (type == GL45.GL_DEBUG_TYPE_ERROR ? "** GL ERROR **" : "")
						+ " type = 0x" + type + ", severity = 0x" + severity + ", message = " + message + "\n");
			}
		}, 0);
		GL11.glFrontFace(GL11.GL_CW);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		// GL11.glEnable(GL11.GL_STENCIL_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public static String getOpenGLVersion() {
		return GL11.glGetString(GL11.GL_VERSION);
	}

	public void render(Scene scene) {
		if (this.activeCamera != scene.getMainCamera()) {
			this.activeCamera = scene.getMainCamera();
		}
		// Clear Screen Before Rendering
		clearScreen();
		if (depth_test) {
			renderDepth(scene);
		} else {
			renderLights(scene);
		}
		// renderVisualizers(scene);
	}

	private void renderVisualizers(Scene scene) {
		scene.getRootObject().renderAll(edge_dectect, this);
	}

	private void renderDepth(Scene scene) {
		GL11.glDepthFunc(GL11.GL_LESS);
		// GL11.glDepthMask(false);
		scene.getRootObject().renderAll(depthShader, this);
	}

	private void renderLights(Scene scene) {
		scene.getRootObject().renderAll(forwardAmbientShader, this);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
		GL11.glDepthMask(false);
		GL11.glDepthFunc(GL11.GL_EQUAL);
		for (Light light : scene.getLights()) {
			this.activeLight = light;
			scene.getRootObject().renderAll(light.getShader(), this);
		}
		GL11.glDepthFunc(GL11.GL_LESS);
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public void addCamera(Camera camera) {
		logger.debug("Adding Camera" + camera);
	}

	public Map<String, Integer> getSamplerMap() {
		return samplerMap;
	}

	public void setSamplerMap(HashMap<String, Integer> samplerMap) {
		this.samplerMap = samplerMap;
	}

	public int getSamplerSlot(String uniformKey) {
		return this.samplerMap.get(uniformKey);
	}

	public Light getActiveLight() {
		return activeLight;
	}

	public void setActiveLight(Light activeLight) {
		this.activeLight = activeLight;
	}

	public Camera getActiveCamera() {
		return activeCamera;
	}

	public void setActiveCamera(Camera camera) {
		this.activeCamera = camera;
	}

	/**
	 * 
	 */
	public static void toggleDepthTest() {
		depth_test = depth_test == false ? true : false;
	}

	@Override
	public void dispose() {

	}

}
