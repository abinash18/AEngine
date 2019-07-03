package com.base.engine.rendering;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_CW;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_EQUAL;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glFrontFace;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL32.GL_DEPTH_CLAMP;

import java.util.ArrayList;

import com.base.engine.components.BaseLight;
import com.base.engine.core.GameObject;
import com.base.engine.core.Vector3f;

public class RenderingEngine {

	public static Camera mainCamera;
	private Vector3f ambientLight;

	// More Permanent Structs
	private ArrayList<BaseLight> lights;
	private BaseLight activeLight;

	public RenderingEngine() {
		initGraphics();

		lights = new ArrayList<BaseLight>();

		mainCamera = new Camera((float) Math.toRadians(70f), (float) Window.getWidth() / (float) Window.getHeight(),
				0.01f, 1000.0f);
		ambientLight = new Vector3f(0.1f, 0.1f, 0.1f);

	}

	public Vector3f getAmbientLight() {
		return (ambientLight);
	}

	public static void clearScreen() {
		// TODO: Stencil Buffer
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}

	@Deprecated
	public void setTextures(boolean enabled) {

		if (enabled) {
			glEnable(GL_TEXTURE_2D);
		} else {
			glDisable(GL_TEXTURE_2D);
		}

	}

	public static void unBindTextures() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	private void initGraphics() {
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		glFrontFace(GL_CW);
		glCullFace(GL_BACK);
		glEnable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);

		glEnable(GL_DEPTH_CLAMP);

		glEnable(GL_TEXTURE_2D);
		// glEnable(GL_FRAMEBUFFER_SRGB);
	}

	@Deprecated
	public void setClearColor(Vector3f color) {
		glClearColor(color.getX(), color.getY(), color.getZ(), 1.0f);
	}

	public static String getOpenGLVersion() {
		return glGetString(GL_VERSION);
	}

	public void render(GameObject gameObject) {
		// Clear Screen Before Rendering
		clearScreen();

		lights.clear();

		gameObject.addToRenderingEngine(this);

		Shader forwardAmbientShader = ForwardAmbientShader.getInstance();
		forwardAmbientShader.setRenderingEngine(this);

		gameObject.render(forwardAmbientShader);

		glEnable(GL_BLEND);

		glBlendFunc(GL_ONE, GL_ONE);

		glDepthMask(false);

		glDepthFunc(GL_EQUAL);

		for (BaseLight light : lights) {
			light.getShader().setRenderingEngine(this); // CHECK THIS AFTER

			activeLight = light;

			gameObject.render(light.getShader());

		}

		glDepthFunc(GL_LESS);

		glDepthMask(true);

		glDisable(GL_BLEND);

	}

	public BaseLight getActiveLight() {
		return activeLight;
	}

	public void addLight(BaseLight light) {
		lights.add(light);
	}

	public Camera getMainCamera() {
		return mainCamera;
	}

	public void setMainCamera(Camera cam) {
		this.mainCamera = cam;
	}

	public void input(float delta) {
		mainCamera.input(delta);
	}

	public void setAmbientLight(Vector3f ambientLight) {
		this.ambientLight = ambientLight;
	}

}
