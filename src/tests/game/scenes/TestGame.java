package tests.game.scenes;

import org.lwjgl.glfw.GLFW;

import com.base.engine.components.Camera;
import com.base.engine.components.DirectionalLight;
import com.base.engine.components.FreeLook;
import com.base.engine.components.FreeMove;
import com.base.engine.components.MeshRenderer;
import com.base.engine.components.PointLight;
import com.base.engine.components.SpotLight;
import com.base.engine.core.Entity;
import com.base.engine.core.input.GLFWInput;
import com.base.engine.math.Vector3f;
import com.base.engine.rendering.meshLoading.Mesh;
import com.base.engine.rendering.resourceManagement.Material;
import com.base.engine.rendering.resourceManagement.Texture;
import com.base.engine.rendering.sceneManagement.Scene;
import com.base.engine.rendering.windowManagement.GLFWWindow;
import com.base.engine.util.Attenuation;

public class TestGame extends Scene {

	public TestGame(GLFWWindow prnt) {
		super("TestGame", prnt);
	}

	private Entity monkey, monkey2, cameraObject;
	private Camera cam;

	public void init() {
		super.init();

		Mesh mesh = new Mesh("plane3.obj", true);
		Material material = new Material();
		material.addTexture("diffuse", new Texture("bricks.jpg"));
		material.addTexture("normal_map", new Texture("bricks_normal.jpg"));
		material.addFloat("specularIntensity", 1);
		material.addFloat("specularPower", 8);
		Material material2 = new Material();
		material2.addTexture("diffuse", new Texture("bricks2.jpg"));
		material2.addTexture("normal_map", new Texture("bricks2_normal.jpg"));
		material2.addFloat("specularIntensity", 1);
		material2.addFloat("specularPower", 8);
		MeshRenderer meshRenderer = new MeshRenderer(mesh, material);
		MeshRenderer meshRenderer2 = new MeshRenderer(new Mesh("monkey.obj", false), material2);

		Entity planeObject = new Entity();
		planeObject.addComponent(meshRenderer);
		planeObject.getTransform().setTranslation(0, -5, 0);
		// planeObject.getTransform().getPosition().set(0, -1, 5);

		Entity directionalLightObject = new Entity();
		DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), 0.2f);

		directionalLightObject.addComponent(directionalLight);

		Entity pointLightObject = new Entity();
		pointLightObject.addComponent(new PointLight(new Vector3f(0, 1, 0), 0.4f, new Attenuation(0, 0, 1)));

		SpotLight spotLight = new SpotLight(new Vector3f(1, 0, 0), 0.4f, new Attenuation(0, 0, 0.8f), 0.7f);

		Entity spotLightObject = new Entity();

		cameraObject = new Entity();
		cam = new Camera((float) Math.toRadians(60.0f),
				(float) super.getParentWindow().getWidth() / (float) super.getParentWindow().getHeight(), 0.01f,
				1000.0f, "playerView");

		cameraObject.addComponent(cam);
		cameraObject.addComponent(new FreeLook(0.35f)).addComponent(new FreeMove(10f));

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
		Material anvilmat = new Material();
		anvilmat.addTexture("diffuse", new Texture("defaultModelTexture.png"));
		anvilmat.addTexture("normal_map", new Texture("Normal_Map_Anvil.png"));
		anvilmat.addFloat("specularIntensity", 1);
		anvilmat.addFloat("specularPower", 8);
		Entity anvil = new Entity().addComponent(new MeshRenderer(new Mesh("Anvil_LowPoly.obj", true), anvilmat));

		super.addChild(anvil);
		super.setMainCamera("playerView");
		super.addChild(spotLightObject);
		super.addChild(planeObject);
		super.addChild(directionalLightObject);
		super.addChild(monkey);
		super.addChild(ironMan);
		super.addChild(monkey2);
	}

	float temp = 0.0f;

	@Override
	public void update(float delta) {
		super.update(delta);
		temp = temp + delta;
	}

	@Override
	public void input(float delta) {
		super.input(delta);
		if (super.getInputController().isKeyDown(GLFWInput.GLFW_KEY_ESCAPE)) {
			super.getInputController().setCursorMode(GLFW.GLFW_CURSOR_NORMAL);
			// System.out.println(this);
			// System.exit(1);

		}
		if (super.getInputController().isKeyDown(GLFWInput.GLFW_KEY_C)) {
			super.getParentWindow().getSceneManager().setCurrentScene("MainMenu");
		}
	}
}