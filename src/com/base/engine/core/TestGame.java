package com.base.engine.core;

import com.base.engine.components.DirectionalLight;
import com.base.engine.components.MeshRenderer;
import com.base.engine.components.PointLight;
import com.base.engine.components.SpotLight;
import com.base.engine.internalGame.Game;
import com.base.engine.rendering.Material;
import com.base.engine.rendering.Mesh;
import com.base.engine.rendering.RenderingEngine;
import com.base.engine.rendering.Texture;
import com.base.engine.rendering.Vertex;
import com.base.engine.util.Attenuation;
import com.base.engine.util.Color;

public class TestGame extends Game {

	public TestGame() {
		// init();
	}

	private GameObject dirLight, dirLight2, pLight, sLight;
	private DirectionalLight dLight, dLight2;
	private PointLight pointLight;
	private SpotLight spotLight;

	public void init() {

		// cam = new Camera();
		dirLight = new GameObject();
		dirLight2 = new GameObject();
		dLight = new DirectionalLight(new Color(0, 0, 1), 0.4f, new Vector3f(1, 1, 1));
		dLight2 = new DirectionalLight(new Color(0, 1, 1), 0.4f, new Vector3f(1, 1, 0));
		pLight = new GameObject();
		pointLight = new PointLight(new Color(1, 0, 0), 0.4f, new Attenuation(0, 0, 1), new Vector3f(5, 0, 20), 10);
		sLight = new GameObject();
		spotLight = new SpotLight(new Color(1, 1, 1), 0.8f, new Attenuation(0, 0, 0.1f), new Vector3f(5, 0, 5), 500,
				new Vector3f(1, 0, 0), 0.7f);

		float fieldDepth = 10.0f;
		float fieldWidth = 10.0f;

		Vertex[] vertices = new Vertex[] {
				new Vertex(new Vector3f(-fieldWidth, 0.0f, -fieldDepth), new Vector2f(0.0f, 0.0f)),
				new Vertex(new Vector3f(-fieldWidth, 0.0f, fieldDepth * 3), new Vector2f(0.0f, 1.0f)),
				new Vertex(new Vector3f(fieldWidth * 3, 0.0f, -fieldDepth), new Vector2f(1.0f, 0.0f)),
				new Vertex(new Vector3f(fieldWidth * 3, 0.0f, fieldDepth * 3), new Vector2f(1.0f, 1.0f)) };

		int indices[] = { 0, 1, 2, 2, 1, 3 };

		// Mesh mesh = new Mesh(vertices, indices, true);
		Mesh mesh = new Mesh("IronMan.obj", true);
		// new Mesh(vertices, indices, true);
		Material material = new Material(new Texture("defaultTexture.png"), new Vector3f(1, 1, 1), 1, 8);

		MeshRenderer meshRenderer = new MeshRenderer(mesh, material);

		GameObject planeObject = new GameObject();
		planeObject.addComponent(meshRenderer);
		planeObject.getTransform().setTranslation(0, -1, 5);
		planeObject.getTransform().setScale(0.25f, 0.25f, 0.25f);

		getRootObject().addChild(planeObject);

		dirLight.addComponent(dLight);
		dirLight2.addComponent(dLight2);
		pLight.addComponent(pointLight);
		sLight.addComponent(spotLight);
		getRootObject().addChild(dirLight);
		getRootObject().addChild(dirLight2);
		getRootObject().addChild(pLight);
		getRootObject().addChild(sLight);

	}

	@Override
	public void update(float delta) {
		super.update(delta);
		spotLight.setDirection(RenderingEngine.mainCamera.getForward());
		spotLight.setPosition(RenderingEngine.mainCamera.getPosition());
	}
}
