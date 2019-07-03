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

public class TestGame extends Game {

	public TestGame() {
		// init();
	}

	private GameObject dirLight, pLight, sLight;
	private DirectionalLight dLight;
	private PointLight pointLight;
	private SpotLight spotLight;

	public void init() {

		// cam = new Camera();
		dirLight = new GameObject();
		dLight = new DirectionalLight(new Vector3f(0, 0, 1), 0.4f, new Vector3f(1, 1, 1));
		pLight = new GameObject();
		pointLight = new PointLight(new Vector3f(1, 0, 0), 0.4f, 0, 0, 1, new Vector3f(5, 0, 20), 10);
		sLight = new GameObject();
		spotLight = new SpotLight(new Vector3f(0, 1, 0), 0.8f, 0, 0, 0.1f, new Vector3f(5, 0, 5), 100,
				new Vector3f(1, 0, 0), 0.7f);

		float fieldDepth = 10.0f;
		float fieldWidth = 10.0f;

		Vertex[] vertices = new Vertex[] {
				new Vertex(new Vector3f(-fieldWidth, 0.0f, -fieldDepth), new Vector2f(0.0f, 0.0f)),
				new Vertex(new Vector3f(-fieldWidth, 0.0f, fieldDepth * 3), new Vector2f(0.0f, 1.0f)),
				new Vertex(new Vector3f(fieldWidth * 3, 0.0f, -fieldDepth), new Vector2f(1.0f, 0.0f)),
				new Vertex(new Vector3f(fieldWidth * 3, 0.0f, fieldDepth * 3), new Vector2f(1.0f, 1.0f)) };

		int indices[] = { 0, 1, 2, 2, 1, 3 };

		Mesh mesh = new Mesh(vertices, indices, true);
		// new Mesh(vertices, indices, true);
		Material material = new Material(new Texture("defaultTexture.png"), new Vector3f(1, 1, 1), 1, 8);

		MeshRenderer meshRenderer = new MeshRenderer(mesh, material);

		GameObject planeObject = new GameObject();
		planeObject.addComponent(meshRenderer);
		planeObject.getTransform().setTranslation(0, -1, 5);

		getRootObject().addChild(planeObject);

		dirLight.addComponent(dLight);
		pLight.addComponent(pointLight);
		sLight.addComponent(spotLight);
		//getRootObject().addChild(dirLight);
		//getRootObject().addChild(pLight);
		getRootObject().addChild(sLight);

	}

	@Override
	public void update(float delta) {
		super.update(delta);
		spotLight.setDirection(RenderingEngine.mainCamera.getForward().normalize());
		spotLight.setPosition(RenderingEngine.mainCamera.getPosition());
	}
}
