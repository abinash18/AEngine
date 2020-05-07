package net.abi.abisEngine.rendering.sceneManagement.scenes;

import org.lwjgl.glfw.GLFW;

import net.abi.abisEngine.components.Camera;
import net.abi.abisEngine.components.DirectionalLight;
import net.abi.abisEngine.components.FreeLook;
import net.abi.abisEngine.components.FreeMove;
import net.abi.abisEngine.components.MeshRenderer;
import net.abi.abisEngine.components.PointLight;
import net.abi.abisEngine.components.SpotLight;
import net.abi.abisEngine.core.Entity;
import net.abi.abisEngine.input.GLFWInput;
import net.abi.abisEngine.input.GLFWMouseAndKeyboardInput;
import net.abi.abisEngine.math.Quaternion;
import net.abi.abisEngine.math.Transform;
import net.abi.abisEngine.math.Vector3f;
import net.abi.abisEngine.rendering.asset.AssetLoaderParameters.LoadedCallback;
import net.abi.abisEngine.rendering.asset.AssetManager;
import net.abi.abisEngine.rendering.asset.loaders.ModelSceneLoader;
import net.abi.abisEngine.rendering.meshManagement.Mesh;
import net.abi.abisEngine.rendering.meshManagement.ModelScene;
import net.abi.abisEngine.rendering.pipelineManagement.RenderingEngine;
import net.abi.abisEngine.rendering.resourceManagement.Material;
import net.abi.abisEngine.rendering.resourceManagement.Texture;
import net.abi.abisEngine.rendering.sceneManagement.Scene;
import net.abi.abisEngine.rendering.windowManagement.GLFWWindow;
import net.abi.abisEngine.rendering.windowManagement.GLFWWindowManager;
import net.abi.abisEngine.util.Attenuation;
import tests.game.materials.BricksOne;
import tests.game.materials.BricksTwo;
import tests.game.windows.MainGame;

public class EngineSplashScreen extends Scene {

	public EngineSplashScreen(GLFWWindow prnt) {
		super("EngineSplash", prnt);
	}

	private Entity monkey, monkey2, cameraObject, anvil, test;
	private Camera cam;

	private AssetManager man;

	public void init() {
		super.init();
		test = super.getRootObject();
		man = new AssetManager(super.getParentWindow().getGlfw_Handle());
		// man = super.getParentWindow().getAssetManager();
		man.setLoader(ModelScene.class, "", new ModelSceneLoader("./res/models/"));

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
		cameraObject.addComponent(spotLight);
		spotLight.getTransform().setParent(cam.getTransform());

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

		// Entity anvil = new Entity().addComponent(
		// new MeshRenderer(AIMeshLoader.loadModel("monkey.obj", "Suzanne.001",
		// 0).bindModel(), anvilmat));

		ModelSceneLoader.Params parm = new ModelSceneLoader.Params();
		parm.loadedCallback = new LoadedCallback() {
			public void finishedLoading(AssetManager assetManager, String fileName, Class type) {
				Material anvilmat = new Material();
				anvilmat.addTexture("diffuse", new Texture("defaultModelTexture.png"));
				anvilmat.addTexture("normal_map", new Texture("Normal_Map_Anvil.png"));
				anvilmat.addFloat("specularIntensity", 1);
				anvilmat.addFloat("specularPower", 8);
				ModelScene m = assetManager.get("IronMan.obj", ModelScene.class);
				Mesh s = m.getMesh("monkey").bindModel();
				monkey = new Entity().addComponent(new MeshRenderer(s, anvilmat));
				monkey.getTransform().setScale(0.250f, 0.250f, 0.250f);
				test.addChild(monkey);
			}
		};

		// man.load("monkey.obj", ModelScene.class, parm);
//		man.load("Anvil_LowPoly.obj", ModelScene.class, parm);
		man.load("IronMan.obj", ModelScene.class, parm);
//		man.load("monkey.obj", ModelScene.class, parm);

		super.setMainCamera("playerView");
		// super.addChild(spotLightObject);
		// super.addChild(new Entity()
		// .addComponent(new MeshRenderer(AIMeshLoader.loadModel("plane3.obj", "Cube",
		// 0), new BricksOne())));
		super.addChild(directionalLightObject);
		super.addChild(monkey2);
		// super.addChild(ironMan);
		// super.addChild(cameraObject);
	}

	float temp = 0.0f;
	boolean done = false, done2 = false;

	@Override
	public void update(float delta) {
		super.update(delta);
		// System.out.println(man.getQueuedAssets());
		if (man.update()) {
			done = true;
			// System.out.println("done");
		}

		// try {
//		if (done && !done2) {
//			Material anvilmat = new Material();
//			anvilmat.addTexture("diffuse", new Texture("defaultModelTexture.png"));
//			anvilmat.addTexture("normal_map", new Texture("Normal_Map_Anvil.png"));
//			anvilmat.addFloat("specularIntensity", 1);
//			anvilmat.addFloat("specularPower", 8);
//			ModelScene m = man.get("monkey.obj", ModelScene.class);
//			Mesh s = m.getMesh("Suzanne.001").bindModel();
////			monkey = new Entity().addComponent(new MeshRenderer(s, anvilmat));
////			super.addChild(monkey);
//
//			m = man.get("IronMan.obj", ModelScene.class);
//			s = m.getMesh("IronMan").bindModel();
//			Entity _anvil = new Entity().addComponent(new MeshRenderer(s, anvilmat));
//			_anvil.getTransform().setScale(0.025f, 0.025f, 0.025f);
//			super.addChild(_anvil);
//			done2 = true;
//		}
		// } catch (Exception e) {
		// done = false;
		// done2 = false;
		// }

		temp = temp + delta;
		float angle = (float) Math.toRadians(temp * 360);
		monkey.getTransform().getRotation().rotate(Transform.X_AXIS, (float) Math.toRadians(angle * 2));
	}

	@Override
	public void input(float delta) {
		super.input(delta);
		if (((GLFWMouseAndKeyboardInput) super.getInputController()).isKeyDown(GLFWInput.GLFW_KEY_ESCAPE)) {
			((GLFWMouseAndKeyboardInput) super.getInputController()).setCursorMode(GLFW.GLFW_CURSOR_NORMAL);
		}

		if (((GLFWMouseAndKeyboardInput) super.getInputController())
				.isMouseButtonDown(GLFWInput.GLFW_MOUSE_BUTTON_RIGHT)) {
			super.getParentWindow().toggleVSync();
			MeshRenderer.toggleWireFrames();
		}

		if (((GLFWMouseAndKeyboardInput) super.getInputController())
				.isMouseButtonDown(GLFWInput.GLFW_MOUSE_BUTTON_LEFT)) {
			RenderingEngine.depth_test = RenderingEngine.depth_test == false ? true : false;
		}

		if (((GLFWMouseAndKeyboardInput) super.getInputController()).isKeyDown(GLFWInput.GLFW_KEY_C)) {
			super.getParentWindow().getSceneManager().setCurrentScene("MainMenu");
		}
	}

}
