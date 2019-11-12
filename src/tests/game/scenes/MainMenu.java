package tests.game.scenes;

import net.abi.abisEngine.components.Camera;
import net.abi.abisEngine.components.DirectionalLight;
import net.abi.abisEngine.components.FreeLook;
import net.abi.abisEngine.components.FreeMove;
import net.abi.abisEngine.components.MeshRenderer;
import net.abi.abisEngine.components.PointLight;
import net.abi.abisEngine.components.SpotLight;
import net.abi.abisEngine.core.Entity;
import net.abi.abisEngine.input.GLFWInput;
import net.abi.abisEngine.math.Vector2f;
import net.abi.abisEngine.math.Vector3f;
import net.abi.abisEngine.rendering.meshLoading.Mesh;
import net.abi.abisEngine.rendering.meshLoading.Vertex;
import net.abi.abisEngine.rendering.resourceManagement.Material;
import net.abi.abisEngine.rendering.resourceManagement.Texture;
import net.abi.abisEngine.rendering.sceneManagement.Scene;
import net.abi.abisEngine.rendering.windowManagement.GLFWWindow;
import net.abi.abisEngine.util.Attenuation;

public class MainMenu extends Scene {

	public MainMenu(GLFWWindow prnt) {
		super("MainMenu", prnt);
	}

	private Entity monkey, monkey2, cameraObject;
	private Camera cam;

	public void init() {
		super.init();
		float fieldDepth = 10.0f;
		float fieldWidth = 10.0f;

		Vertex[] vertices = new Vertex[] {
				new Vertex(new Vector3f(-fieldWidth, 0.0f, -fieldDepth), new Vector2f(0.0f, 0.0f)),
				new Vertex(new Vector3f(-fieldWidth, 0.0f, fieldDepth * 3), new Vector2f(0.0f, 1.0f)),
				new Vertex(new Vector3f(fieldWidth * 3, 0.0f, -fieldDepth), new Vector2f(1.0f, 0.0f)),
				new Vertex(new Vector3f(fieldWidth * 3, 0.0f, fieldDepth * 3), new Vector2f(1.0f, 1.0f)) };

		int indices[] = { 0, 1, 2, 2, 1, 3 };

//		Vertex[] vertices2 = new Vertex[] {
//				new Vertex(new Vector3f(-fieldWidth / 10, 0.0f, -fieldDepth / 10), new Vector2f(0.0f, 0.0f)),
//				new Vertex(new Vector3f(-fieldWidth / 10, 0.0f, fieldDepth / 10 * 3), new Vector2f(0.0f, 1.0f)),
//				new Vertex(new Vector3f(fieldWidth / 10 * 3, 0.0f, -fieldDepth / 10), new Vector2f(1.0f, 0.0f)),
//				new Vertex(new Vector3f(fieldWidth / 10 * 3, 0.0f, fieldDepth / 10 * 3), new Vector2f(1.0f, 1.0f)) };
//
//		int indices2[] = { 0, 1, 2, 2, 1, 3 };

		// Mesh mesh2 = new Mesh(vertices2, indices2, true);

		Mesh mesh = new Mesh("plane3.obj", false);
		Material material = new Material();
		material.addTexture("diffuse", new Texture("bricks2.jpg"));
		material.addTexture("normal_map", new Texture("bricks2_normal.jpg"));
		material.addFloat("specularIntensity", 1);
		material.addFloat("specularPower", 8);
		Material material2 = new Material();
		material2.addTexture("diffuse", new Texture("defaultModelTexture.png"));
		material2.addFloat("specularIntensity", 1);
		material2.addFloat("specularPower", 8);
		MeshRenderer meshRenderer = new MeshRenderer(mesh, material);
		MeshRenderer meshRenderer2 = new MeshRenderer(new Mesh("monkey.obj", true), material2);

		Entity planeObject = new Entity();
		planeObject.addComponent(meshRenderer);
		planeObject.getTransform().setTranslation(0, -5, 0);
		// planeObject.getTransform().getPosition().set(0, -1, 5);

		Entity directionalLightObject = new Entity();
		DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), 0.2f);

		directionalLightObject.addComponent(directionalLight);

		Entity pointLightObject = new Entity();
		pointLightObject.addComponent(new PointLight(new Vector3f(0, 1, 0), 0.4f, new Attenuation(0, 0, 1)));

		SpotLight spotLight = new SpotLight(new Vector3f(1, 1, 1), 0.4f, new Attenuation(0, 0, 0.8f), 0.7f);

		Entity spotLightObject = new Entity();

		cameraObject = new Entity();
		cam = new Camera((float) Math.toRadians(70.0f),
				(float) super.getParentWindow().getPWidth() / (float) super.getParentWindow().getPHeight(), 0.01f,
				1000.0f, "playerView");
		cameraObject.addComponent(cam);
		cameraObject.addComponent(spotLight).addComponent(new FreeLook(0.35f)).addComponent(new FreeMove(10f));

		// super.addChild(testMesh1);

		directionalLight.getTransform().rotate(new Vector3f(1, 0, 0), (float) Math.toRadians(-135));

		Entity ironMan = new Entity();
		ironMan.addComponent(new MeshRenderer(new Mesh("simpleCube.obj", false), material2));

		ironMan.getTransform().setTranslation(10, 5, 0);

		monkey = new Entity();
		monkey.addComponent(meshRenderer2);

		monkey2 = new Entity();
		monkey2.addComponent(new MeshRenderer(new Mesh("monkey.obj", true), material));
		monkey2.getTransform().setTranslation(0, 0, 5);

		monkey2.addChild(cameraObject);
		monkey2.setTransform(cameraObject.getTransform());
		cam.getTransform().setTranslation(0, 0, -5);
		super.setMainCamera("playerView");
		super.addChild(spotLightObject);
		super.addChild(planeObject);
		super.addChild(directionalLightObject);
		super.addChild(monkey);
		// super.addChild(ironMan);
		super.addChild(monkey2);

//		Entity t = new Entity()
//				.addComponent(new Camera((float) Math.toRadians(70.0f),
//						(float) Window.getWidth() / (float) Window.getHeight(), 0.01f, 1000.0f, "playerView2"))
//				.addComponent(new FreeLook(0.35f)).addComponent(new FreeMove(10f))
//				.addComponent(new SpotLight(new Vector3f(0, 0, 1), 0.4f, new Attenuation(0, 0, 0.8f), 0.7f));
//		super.addChild(t);

	}

	float temp = 0.0f;

	@Override
	public void update(float delta) {
		super.update(delta);
		temp = temp + delta;
		// float angle = (float) Math.toRadians(temp * 180 * 2);
		// monkey.getTransform().setRotation(new Quaternion(Transform.X_AXIS, angle));
	}

	@Override
	public void input(float delta) {
		super.input(delta);
		if (super.getInputController().isKeyDown(GLFWInput.GLFW_KEY_ESCAPE)) {
			// Input.setCursor(true);
			System.out.println(this);
			// System.exit(1);

		}
		if (super.getInputController().isKeyDown(GLFWInput.GLFW_KEY_ESCAPE)) {
			super.getParentWindow().getSceneManager().setCurrentScene("TestGame");
		}
//		if (Input.getKeyDown(Input.KEY_B)) {
//			super.setMainCamera("playerView");
//		}
	}
}