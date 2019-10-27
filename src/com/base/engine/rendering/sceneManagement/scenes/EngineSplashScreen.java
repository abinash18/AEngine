package com.base.engine.rendering.sceneManagement.scenes;

import com.base.engine.components.Camera;
import com.base.engine.components.LockView;
import com.base.engine.components.MeshRenderer;
import com.base.engine.components.PointLight;
import com.base.engine.core.Entity;
import com.base.engine.rendering.meshLoading.legacy.Mesh;
import com.base.engine.rendering.resourceManagement.Material;
import com.base.engine.rendering.resourceManagement.Texture;
import com.base.engine.rendering.sceneManagement.Scene;
import com.base.engine.rendering.windowManagement.GLFWWindow;
import com.base.engine.util.Attenuation;
import com.base.engine.util.Color;

public class EngineSplashScreen extends Scene {

	public EngineSplashScreen(GLFWWindow prnt) {
		super("EngineSplash", prnt);
	}

	@Override
	public void init() {
		super.init();

		Entity cameraObject = new Entity();
		Camera cam = new Camera((float) Math.toRadians(60.0f),
				(float) super.getParentWindow().getWidth() / (float) super.getParentWindow().getHeight(), 0.01f,
				1000.0f, "playerView");

		cameraObject.addComponent(cam);
		// cameraObject.addComponent(new FreeLook(0.35f)).addComponent(new
		// FreeMove(10f));

		Mesh mesh = new Mesh("plane3.obj", true);
		Material material = new Material();
		material.addTexture("diffuse", new Texture("1x/Abi's Engine Splash Screen.png"));
		material.addTexture("normal_map", new Texture("default_normal.jpg"));
		material.addFloat("specularIntensity", 1);
		material.addFloat("specularPower", 8);
		Entity planeObject = new Entity();
		planeObject.addComponent(new MeshRenderer(mesh, material));
		planeObject.getTransform().setTranslation(0, 0, 5f);

		cameraObject.addChild(planeObject);

		planeObject.addComponent(new LockView());
		
		PointLight light = new PointLight(new Color(1, 0, 0), 100f, new Attenuation(1, 1, 1));

		Entity lightO = new Entity().addComponent(light);
		lightO.getTransform().setTranslation(0f, 0f, 5f);
		cameraObject.addChild(lightO);
		super.addCamera(cam);
		super.addChild(cameraObject);

	}

}
