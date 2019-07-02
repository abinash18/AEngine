package com.base.engine.core;

import com.base.engine.components.DirectionalLight;
import com.base.engine.components.MeshRenderer;
import com.base.engine.components.PointLight;
import com.base.engine.internalGame.Game;
import com.base.engine.rendering.Material;
import com.base.engine.rendering.Mesh;
import com.base.engine.rendering.Texture;
import com.base.engine.rendering.Vertex;

public class TestGame extends Game {

	public TestGame() {
	}

	public void init() {

		// cam = new Camera();

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

		GameObject dirLight = new GameObject();
		DirectionalLight dLight = new DirectionalLight(new Vector3f(0, 0, 1), 0.4f, new Vector3f(1, 1, 1));
		PointLight pointLight = new PointLight(new Vector3f(1, 0, 0), 0.4f, 0, 0, 1, new Vector3f(5, 0, 20), 10);
		// SpotLight spotLight = new SpotLight(pointLight, new Vector3f(5, 0, 5), 10);
		dirLight.addComponent(dLight);
		dirLight.addComponent(pointLight);
		// dirLight.addComponent(spotLight);
		getRootObject().addChild(dirLight);

	}
}
