package tests.renderTest.scenes;

import org.lwjgl.glfw.GLFW;

import net.abi.abisEngine.components.Camera;
import net.abi.abisEngine.components.DirectionalLight;
import net.abi.abisEngine.components.FreeLook;
import net.abi.abisEngine.components.FreeMove;
import net.abi.abisEngine.components.PointLight;
import net.abi.abisEngine.components.SpotLight;
import net.abi.abisEngine.entities.Entity;
import net.abi.abisEngine.input.GLFWInput;
import net.abi.abisEngine.input.GLFWMouseAndKeyboardInput;
import net.abi.abisEngine.math.Vector3f;
import net.abi.abisEngine.rendering.asset.loaders.ModelSceneLoader;
import net.abi.abisEngine.rendering.mesh.ModelScene;
import net.abi.abisEngine.rendering.scene.Scene;
import net.abi.abisEngine.rendering.window.GLFWWindow;
import net.abi.abisEngine.util.Attenuation;
import tests.renderTest.materials.BricksOne;
import tests.renderTest.materials.BricksTwo;

public class TestGame extends Scene {

	public TestGame(GLFWWindow prnt) {
		super("TestGame", prnt);
	}

	private Entity monkey, monkey2, cameraObject, anvil;
	private Camera cam;

	private net.abi.abisEngine.rendering.asset.AssetManager man;

	public void init() {
		super.init();
		man = super.getParentWindow().getAssetManager();
		man.setLoader(ModelScene.class, null, new ModelSceneLoader("./res/models/"));

		BricksOne material = new BricksOne();
		BricksTwo material2 = new BricksTwo();

		// MeshRenderer meshRenderer2 = new
		// MeshRenderer(AIMeshLoader.loadModel("Anvil_LowPoly.obj", "Cube.003", 0),
		// material2).toggleWireFrames();

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
				(float) super.getParentWindow().getPWidth() / (float) super.getParentWindow().getPHeight(), 0.01f,
				1000.0f, "playerView");

		cameraObject.addComponent(cam);
		cameraObject.addComponent(new FreeLook(0.35f)).addComponent(new FreeMove(10f));

		// super.addChild(testMesh1);

		directionalLight.getTransform().rotate(new Vector3f(1, 0, 0), (float) Math.toRadians(-135));

		Entity ironMan = new Entity();
		// ironMan.addComponent(new MeshRenderer(AIMeshLoader.loadModel("monkey.obj",
		// "Suzanne.001", 0), material2));

		ironMan.getTransform().setTranslation(10, 5, 0);

		monkey = new Entity();
		// monkey.addComponent(meshRenderer2);

		monkey2 = new Entity();
		// monkey2.addComponent(
		// new MeshRenderer(AIMeshLoader.loadModel("monkey.obj", "Suzanne.001", 0),
		// material).toggleWireFrames());
		monkey2.getTransform().setTranslation(0, 0, 5);

		monkey2.addChild(cameraObject);
		monkey2.setTransform(cameraObject.getTransform());
		cam.getTransform().setTranslation(0, 0, -5);

//		Entity anvil = new Entity().addComponent(
//				new MeshRenderer(AIMeshLoader.loadModel("monkey.obj", "Suzanne.001", 0).bindModel(), anvilmat));

		man.load("monkey.obj", ModelScene.class, null);
		man.load("Anvil_LowPoly.obj", ModelScene.class, null);
		// man.load("IronMan.obj", ModelScene.class, null);
		man.load("stall.obj", ModelScene.class, null);

		super.setMainCamera("playerView");
		super.addChild(spotLightObject);
		// super.addChild(new Entity()
		// .addComponent(new MeshRenderer(AIMeshLoader.loadModel("plane3.obj", "Cube",
		// 0), new BricksOne())));
		super.addChild(directionalLightObject);
		// super.addChild(monkey);
		// super.addChild(ironMan);
		super.addChild(cameraObject);
	}

	float temp = 0.0f;

	@Override
	public void update(float delta) {
		super.update(delta);
		temp = temp + delta;
		monkey.getTransform().setTranslation(0f, 0f, 0.5f * temp);
	}

	@Override
	public void input(float delta) {
		super.input(delta);

		if (((GLFWMouseAndKeyboardInput) super.getInputController()).isKeyDown(GLFWInput.GLFW_KEY_ESCAPE)) {
			((GLFWMouseAndKeyboardInput) super.getInputController()).setCursorMode(GLFW.GLFW_CURSOR_NORMAL);
			// System.out.println(this);

		}
		if (((GLFWMouseAndKeyboardInput) super.getInputController()).isKeyDown(GLFWInput.GLFW_KEY_C)) {
			super.getParentWindow().getSceneManager().setCurrentScene("MainMenu");
		}
	}
}