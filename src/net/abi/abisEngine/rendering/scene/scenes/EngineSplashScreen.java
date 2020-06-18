package net.abi.abisEngine.rendering.scene.scenes;

import org.lwjgl.glfw.GLFW;

import net.abi.abisEngine.components.Camera;
import net.abi.abisEngine.components.DirectionalLight;
import net.abi.abisEngine.components.FreeLook;
import net.abi.abisEngine.components.FreeMove;
import net.abi.abisEngine.components.MeshRenderer;
import net.abi.abisEngine.components.PointLight;
import net.abi.abisEngine.components.SpotLight;
import net.abi.abisEngine.entities.Entity;
import net.abi.abisEngine.input.GLFWInput;
import net.abi.abisEngine.input.GLFWMouseAndKeyboardInput;
import net.abi.abisEngine.math.Transform;
import net.abi.abisEngine.math.Vector3f;
import net.abi.abisEngine.rendering.asset.AssetContainer;
import net.abi.abisEngine.rendering.asset.AssetLoaderParameters.LoadedCallback;
import net.abi.abisEngine.rendering.asset.AssetManager;
import net.abi.abisEngine.rendering.asset.loaders.ModelSceneLoader;
import net.abi.abisEngine.rendering.material.Material;
import net.abi.abisEngine.rendering.mesh.Mesh;
import net.abi.abisEngine.rendering.mesh.ModelScene;
import net.abi.abisEngine.rendering.renderPipeline.RenderingEngine;
import net.abi.abisEngine.rendering.scene.Scene;
import net.abi.abisEngine.rendering.texture.Texture;
import net.abi.abisEngine.rendering.window.GLFWWindow;
import net.abi.abisEngine.util.Attenuation;
import tests.renderTest.materials.BricksOne;
import tests.renderTest.materials.BricksTwo;

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
		DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), 0.5f);

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
		// cameraObject.addComponent(spotLight);
		// spotLight.getTransform().setParent(cam.getTransform());

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
		// monkey2.getTransform().setTranslation(0, 0, 5);

		// monkey2.addChild(cameraObject);
		// monkey2.setTransform(cameraObject.getTransform());
		// cam.getTransform().setTranslation(0, 0, -5);

		// Entity anvil = new Entity().addComponent(
		// new MeshRenderer(AIMeshLoader.loadModel("monkey.obj", "Suzanne.001",
		// 0).bindModel(), anvilmat));

		ModelSceneLoader.Parameter parm = new ModelSceneLoader.Parameter();
		parm.loadedCallback = new LoadedCallback() {
			@Override
			public void finishedLoading(AssetManager assetManager, String fileName, AssetContainer container) {
				Material anvilmat = new Material();
				anvilmat.addTexture("diffuse", new Texture("defaultModelTexture.png"));
				anvilmat.addTexture("normal_map", new Texture("Normal_Map_Anvil.png"));
				anvilmat.addFloat("specularIntensity", 1);
				anvilmat.addFloat("specularPower", 8);
				ModelScene m = assetManager.get(fileName, ModelScene.class);
				Mesh s = m.getMesh("monkey").bindModel();
				monkey = new Entity().addComponent(new MeshRenderer(s, anvilmat));
				monkey.getTransform().setTranslation(new Vector3f(0f, 0f, 10f));
				monkey.getTransform().setScale(0.025f, 0.025f, 0.025f);
				monkey.getTransform().getRotation().rotate(Transform.Y_AXIS, (float) Math.toRadians(180));
				test.addChild(monkey);

				// monkey2 = new Entity().addComponent(new MeshRenderer(s, anvilmat));
				// monkey2.getTransform().setTranslation(0, 0, 10.0f);
			}

		};

		// man.load("monkey.obj", ModelScene.class, parm);
//		man.load("Anvil_LowPoly.obj", ModelScene.class, parm);
		// man.load("monkey.obj", ModelScene.class, parm);
		man.load("IronMan.obj", ModelScene.class, parm);

		super.setMainCamera("playerView");
		// super.addChild(spotLightObject);
		// super.addChild(new Entity()
		// .addComponent(new MeshRenderer(AIMeshLoader.loadModel("plane3.obj", "Cube",
		// 0), new BricksOne())));
		super.addChild(directionalLightObject);
		// super.addChild(monkey2);
		// super.addChild(ironMan);
		super.addChild(cameraObject);
	}

	float temp = 0.0f;
	boolean done = false, done2 = false;

	@Override
	public void update(float delta) {
		super.update(delta);
		// System.out.println(man.getQueuedAssets());
		man.update();
		temp = temp + delta;
		float angle = (float) Math.toRadians(temp * 180);
		// monkey.getTransform().getRotation().rotate(Transform.X_AXIS, (float)
		// Math.toRadians(angle * 20));
	}

	GLFWMouseAndKeyboardInput in = (GLFWMouseAndKeyboardInput) super.getInputController();

	@Override
	public void input(float delta) {
		in = (GLFWMouseAndKeyboardInput) super.getInputController();
		super.input(delta);
		if (in.isKeyDown(GLFWInput.GLFW_KEY_ESCAPE)) {
			in.setCursorMode(GLFW.GLFW_CURSOR_NORMAL);
		}

		if (in.isKeyDown(GLFWInput.GLFW_KEY_V)) {
			super.getParentWindow().toggleVSync();
		}

		if (in.isKeyDown(GLFWInput.GLFW_KEY_F)) {
			// super.getParentWindow().toggleFullScreen();
		}

		if (in.isMouseButtonDown(GLFWInput.GLFW_MOUSE_BUTTON_RIGHT)) {
			MeshRenderer.toggleWireFrames();
		}

		if (in.isKeyDown(GLFWInput.GLFW_KEY_N)) {
			MeshRenderer.drawNormals = !MeshRenderer.drawNormals;
		}

		if (in.isMouseButtonDown(GLFWInput.GLFW_MOUSE_BUTTON_LEFT)) {
			RenderingEngine.depth_test = !RenderingEngine.depth_test;
		}

		if (in.isKeyDown(GLFWInput.GLFW_KEY_C)) {
			super.getParentWindow().getSceneManager().setCurrentScene("MainMenu");
		}
	}

}
