package com.base.engine.rendering;

import java.util.HashMap;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;

import com.base.engine.components.Light;
import com.base.engine.components.Camera;
import com.base.engine.handlers.logging.LogManager;
import com.base.engine.handlers.logging.Logger;
import com.base.engine.math.Transform;
import com.base.engine.math.Vector3f;
import com.base.engine.rendering.resourceManagement.MappedValues;
import com.base.engine.rendering.resourceManagement.Material;
import com.base.engine.rendering.sceneManagement.Scene;
import com.base.engine.rendering.shaders.Shader;

public class RenderingEngine extends MappedValues {

	private static final Logger logger = LogManager.getLogger(RenderingEngine.class.getName());

	private HashMap<String, Integer> samplerMap;

	private Shader forwardAmbientShader;

	private Light activeLight;
	private Camera mainCamera;

	public RenderingEngine() {
		super();

		samplerMap = new HashMap<String, Integer>();

		samplerMap.put("diffuse", 0);
		samplerMap.put("normal_map", 1);

		super.addVector3f("ambient", new Vector3f(0.1f, 0.1f, 0.1f));

		forwardAmbientShader = new Shader("forward-ambient");

		RenderingEngine.initGraphics();
	}

	public void updateUniformStruct(Transform transform, Material mat, Shader shader, String uniformName,
			String uniformType) {
		logger.error("'" + uniformType
				+ "' is not a valid Supported Type. Or is misspelled, please check shader program or change the prefix of the variable.",
				new IllegalArgumentException("'" + uniformType + "' is not a valid Supported Type."));
	}

	public static void clearScreen() {
		// TODO: Stencil Buffer
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	@Deprecated
	public void setTextures(boolean enabled) {

		if (enabled) {
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		} else {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
		}

	}

	public static void unBindTextures() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	private static void initGraphics() {
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		GL11.glFrontFace(GL11.GL_CW);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		//GL11.glEnable(GL32.GL_DEPTH_CLAMP);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		// GL11.glEnable(GL32.GL_FRAMEBUFFER_SRGB);
	}

	@Deprecated
	public void setClearColor(Vector3f color) {
		GL11.glClearColor(color.getX(), color.getY(), color.getZ(), 1.0f);
	}

	public static String getOpenGLVersion() {
		return GL11.glGetString(GL11.GL_VERSION);
	}

	public void render(Scene scene) {

		if (this.mainCamera != scene.getMainCamera()) {
			this.mainCamera = scene.getMainCamera();
		}

		// Clear Screen Before Rendering
		clearScreen();

		/*
		 * lights.clear();
		 * 
		 * gameObject.addToRenderingEngine(this);
		 */

		// Shader forwardAmbientShader = ForwardAmbientShader.getInstance();
		// forwardAmbientShader.setRenderingEngine(this);

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

	public HashMap<String, Integer> getSamplerMap() {
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

	public Camera getMainCamera() {
		return mainCamera;
	}

	public void setMainCamera(Camera mainCamera) {
		this.mainCamera = mainCamera;
	}

}
