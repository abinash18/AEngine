package com.base.engine.core;

import com.base.engine.internalGame.*;
import com.base.engine.rendering.*;

public class TestGame extends Game {
	private Camera cam;
	// private GameObject planeObject;

//	PointLight pLight1 = new PointLight(new BaseLight(new Vector3f(1, 0.5f, 0), 0.8f), new Attenuation(0, 0, 1),
//			new Vector3f(-2, 0, 5f), 10),
//			pLight2 = new PointLight(new BaseLight(new Vector3f(0, 0.5f, 1), 0.8f), new Attenuation(0, 0, 1),
//					new Vector3f(2, 0, 7f), 10);
//	SpotLight sLight = new SpotLight(new PointLight(new BaseLight(new Vector3f(0, 1f, 1f), 0.8f),
//			new Attenuation(0, 0, 0.1f), new Vector3f(-2, 0, 5f), 30), new Vector3f(1, 1, 1), 0.7f);

	public TestGame() {
	}

	public void init() {

		cam = new Camera();

		float fieldDepth = 10.0f;
		float fieldWidth = 10.0f;

		Vertex[] vertices = new Vertex[] {
				new Vertex(new Vector3f(-fieldWidth, 0.0f, -fieldDepth), new Vector2f(0.0f, 0.0f)),
				new Vertex(new Vector3f(-fieldWidth, 0.0f, fieldDepth * 3), new Vector2f(0.0f, 1.0f)),
				new Vertex(new Vector3f(fieldWidth * 3, 0.0f, -fieldDepth), new Vector2f(1.0f, 0.0f)),
				new Vertex(new Vector3f(fieldWidth * 3, 0.0f, fieldDepth * 3), new Vector2f(1.0f, 1.0f)) };

		int indices[] = { 0, 1, 2, 2, 1, 3 };

		Mesh mesh = new Mesh(vertices, indices, true);
		Material material = new Material(new Texture("defaultTexture.png"), new Vector3f(1, 1, 1), 1, 8);

		MeshRenderer meshRenderer = new MeshRenderer(mesh, material);

		GameObject planeObject = new GameObject();
		planeObject.addComponent(meshRenderer);
		planeObject.getTransform().setTranslation(0, -8, 20);

		getRootObject().addChild(planeObject);

		Transform.setProjection(500f, Window.getWidth(), Window.getHeight(), 0.1f, 1000);
		Transform.setCam(cam);
		// MeshRenderer meshRenderer = new MeshRenderer(mesh, mat);
		// root.addComponent(meshRenderer);

//		cam.setPos(new Vector3f(-12.774877f, 5.2086587f, -7.0980463f));
//		cam.setView(new Vector3f(0.70283586f, -0.14349261f, 0.6967293f),
//				new Vector3f(0.102347106f, 0.9896512f, 0.10057626f));

		// root.addComponent(mr);
		// Transform.setProjection(70f, Window.getWidth(), Window.getHeight(), 0.1f,
		// 1000f);

		// PhongShader.setAmbientLight(new Vector3f(0.1f, 0.1f, 0.1f));
		// PhongShader.getBaseLight().setIntensity(.08f);
		// PhongShader.setDirectionalLight(new
		// DirectionalLight(PhongShader.getBaseLight(), new Vector3f(1, 1, 1)));

		// PhongShader.setPointLights(new PointLight[] { pLight1, pLight2 });
		// PhongShader.setSpotLights(new SpotLight[] { sLight });
	}

//	public void input() {
//		cam.input();
//		root.input();
//	}
//
//	float temp = 0.0f;
//
//	public void update() {
//		temp += Time.getDelta();
//
//		float sin = (float) Math.sin(temp);
//		float cos = (float) Math.cos(temp);
//		root.getTransform().setTranslation(0, -1, 5);
//		root.update();
//
////		// transform.setRotation(0, sin * 180, 0);
////		// transform.setScale(3, 3, 5);
////		// cam.setPos(pLight1.getPosition());
////		pLight1.setPosition(new Vector3f(3, 0, 8.0f * (float) (Math.sin(temp) + 1.0 / 2.0) + 10));
////		pLight2.setPosition(new Vector3f(7, 0, 8.0f * (float) (Math.cos(temp) + 1.0 / 2.0) + 10));
////		sLight.getPointLight().setPosition(cam.getPos());
////		sLight.setDirection(cam.getForward());
//
//	}
//
//	public void render() {
//		root.render();
//		// RenderUtil.setClearColor(new Vector3f(0.0f, 0.0f, 0.0f));
//		// RenderUtil.setClearColor(Transform.getCam().getPos().div(2048f).abs());
//	}
}
