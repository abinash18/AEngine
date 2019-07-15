package com.base.engine.core;

import com.base.engine.components.Camera;
import com.base.engine.components.DirectionalLight;
import com.base.engine.components.MeshRenderer;
import com.base.engine.components.PointLight;
import com.base.engine.components.SpotLight;
import com.base.engine.internalGame.Game;
import com.base.engine.math.Quaternion;
import com.base.engine.math.Transform;
import com.base.engine.math.Vector2f;
import com.base.engine.math.Vector3f;
import com.base.engine.rendering.Material;
import com.base.engine.rendering.Mesh;
import com.base.engine.rendering.Texture;
import com.base.engine.rendering.Vertex;
import com.base.engine.rendering.Window;

public class TestGame extends Game {

	public TestGame() {
		// init();
	}

	private GameObject monkey, monkey2, dirLight, dirLight2, pLight, sLight;
	private DirectionalLight dLight, dLight2;
	private PointLight pointLight;
	private SpotLight spotLight;

	public void init() {

		float fieldDepth = 10.0f;
		float fieldWidth = 10.0f;

		Vertex[] vertices = new Vertex[] {
				new Vertex(new Vector3f(-fieldWidth, 0.0f, -fieldDepth), new Vector2f(0.0f, 0.0f)),
				new Vertex(new Vector3f(-fieldWidth, 0.0f, fieldDepth * 3), new Vector2f(0.0f, 1.0f)),
				new Vertex(new Vector3f(fieldWidth * 3, 0.0f, -fieldDepth), new Vector2f(1.0f, 0.0f)),
				new Vertex(new Vector3f(fieldWidth * 3, 0.0f, fieldDepth * 3), new Vector2f(1.0f, 1.0f)) };

		int indices[] = { 0, 1, 2, 2, 1, 3 };

		Vertex[] vertices2 = new Vertex[] {
				new Vertex(new Vector3f(-fieldWidth / 10, 0.0f, -fieldDepth / 10), new Vector2f(0.0f, 0.0f)),
				new Vertex(new Vector3f(-fieldWidth / 10, 0.0f, fieldDepth / 10 * 3), new Vector2f(0.0f, 1.0f)),
				new Vertex(new Vector3f(fieldWidth / 10 * 3, 0.0f, -fieldDepth / 10), new Vector2f(1.0f, 0.0f)),
				new Vertex(new Vector3f(fieldWidth / 10 * 3, 0.0f, fieldDepth / 10 * 3), new Vector2f(1.0f, 1.0f)) };

		int indices2[] = { 0, 1, 2, 2, 1, 3 };

		// Mesh mesh2 = new Mesh(vertices2, indices2, true);

		Mesh mesh = new Mesh(vertices, indices, true);
		Material material = new Material();
		material.addTexture("diffuse", new Texture("defaultTexture.png"));
		material.addFloat("specularIntensity", 1);
		material.addFloat("specularPower", 8);
		Material material2 = new Material();
		material2.addTexture("diffuse", new Texture("defaultModelTexture.png"));
		material2.addFloat("specularIntensity", 1);
		material2.addFloat("specularPower", 8);
		MeshRenderer meshRenderer = new MeshRenderer(mesh, material);
		MeshRenderer meshRenderer2 = new MeshRenderer(new Mesh("monkey.obj", true), material2);

		GameObject planeObject = new GameObject();
		planeObject.addComponent(meshRenderer);
		planeObject.getTransform().setTranslation(0, -5, 0);
		// planeObject.getTransform().getPosition().set(0, -1, 5);

		GameObject directionalLightObject = new GameObject();
		DirectionalLight directionalLight = new DirectionalLight(new Vector3f(0, 0, 1), 0.2f);

		directionalLightObject.addComponent(directionalLight);

		GameObject pointLightObject = new GameObject();
		pointLightObject.addComponent(new PointLight(new Vector3f(0, 1, 0), 0.4f, new Vector3f(0, 0, 1)));

		SpotLight spotLight = new SpotLight(new Vector3f(1, 1, 1), 0.4f, new Vector3f(0, 0, 0.8f), 0.7f);

		GameObject spotLightObject = new GameObject();
		super.addChild(planeObject);
		super.addChild(directionalLightObject);

		// GameObject testMesh1 = new GameObject().addComponent(new MeshRenderer(mesh2,
		// material));
		// GameObject testMesh2 = new GameObject().addComponent(new MeshRenderer(mesh2,
		// material));

		// testMesh1.getTransform().getPosition().set(0, 2, 0);
		// testMesh1.getTransform().setRotation(new Quaternion(new Vector3f(0, 1, 0),
		// 0.2f));

		// testMesh2.getTransform().getPosition().set(0, 0, 5);

		GameObject cameraObject = new GameObject();
		Camera cam = new Camera((float) Math.toRadians(70.0f), (float) Window.getWidth() / (float) Window.getHeight(),
				0.01f, 1000.0f);
		cameraObject.addComponent(cam);
		cameraObject.addComponent(spotLight);

		super.addChild(cameraObject);

		// super.addChild(testMesh1);

		directionalLight.getTransform().rotate(new Vector3f(1, 0, 0), (float) Math.toRadians(-135));

		// Mesh m = new Mesh("monkey.obj", true);

		monkey = new GameObject();
		monkey.addComponent(meshRenderer2);
		monkey.getTransform().setRotation(new Quaternion(Transform.Z_AXIS, (float) Math.toRadians((90.0f))));

		super.addChild(monkey);
		
		monkey2 = new GameObject();
		monkey2.addComponent(new MeshRenderer(new Mesh("monkey.obj", true), material));
		monkey2.getTransform().setTranslation(5, 0, 5);;

		super.addChild(monkey2);

	}

	float temp = 0.0f;

	@Override
	public void update(float delta) {
		super.update(delta);

		temp = temp + delta;
		float angle = (float) Math.toRadians(temp * 180 * 2);
		monkey.getTransform().setRotation(new Quaternion(Transform.X_AXIS, angle));

	}

}
