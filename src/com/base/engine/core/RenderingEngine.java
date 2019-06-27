package com.base.engine.core;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_CW;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glFrontFace;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL32.GL_DEPTH_CLAMP;

import com.base.engine.rendering.BasicShader;
import com.base.engine.rendering.Camera;
import com.base.engine.rendering.Shader;
import com.base.engine.rendering.Window;

public class RenderingEngine {

	private Camera mainCamera;
	private Vector3f ambientLight;
	
	public RenderingEngine() {
		initGraphics();

		mainCamera = new Camera((float) Math.toRadians(70f), (float) Window.getWidth() / (float) Window.getHeight(),
				0.01f, 1000.0f);
		ambientLight = new Vector3f(0.2f, 0.2f, 0.2f);
	}

	public static void clearScreen() {
		// TODO: Stencil Buffer
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}

	private static void setTextures(boolean enabled) {

		if (enabled) {
			glEnable(GL_TEXTURE_2D);
		} else {
			glDisable(GL_TEXTURE_2D);
		}

	}

	public static void unBindTextures() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	private static void initGraphics() {
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		glFrontFace(GL_CW);
		glCullFace(GL_BACK);
		glEnable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);

		glEnable(GL_DEPTH_CLAMP);

		glEnable(GL_TEXTURE_2D);
		// glEnable(GL_FRAMEBUFFER_SRGB);
	}

	private static void setClearColor(Vector3f color) {
		glClearColor(color.getX(), color.getY(), color.getZ(), 1.0f);
	}

	public static String getOpenGLVersion() {
		return glGetString(GL_VERSION);
	}

	public void render(GameObject gameObject) {
		// Clear Screen Before Rendering
		clearScreen();

		Shader shader = BasicShader.getInstance();
		shader.setRenderingEngine(this);

		gameObject.render(shader);

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

}
