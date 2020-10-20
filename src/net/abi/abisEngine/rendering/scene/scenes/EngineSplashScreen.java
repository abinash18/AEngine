/*******************************************************************************
 * Copyright 2020 Abinash Singh | ABI INC.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
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
import net.abi.abisEngine.handlers.file.PathHandle;
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
import net.abi.abisEngine.rendering.pipeline.RenderingEngine;
import net.abi.abisEngine.rendering.scene.Scene;
import net.abi.abisEngine.rendering.shader.AEShader;
import net.abi.abisEngine.rendering.shader.compiler.AEShaderCompiler;
import net.abi.abisEngine.rendering.shader.compiler.parser.AEShaderParserYAML;
import net.abi.abisEngine.rendering.texture.Texture;
import net.abi.abisEngine.rendering.window.GLFWWindow;
import net.abi.abisEngine.util.Attenuation;

public class EngineSplashScreen extends Scene {

	public EngineSplashScreen(GLFWWindow prnt) {
		super("EngineSplash", prnt);
	}

	private Entity monkey, monkey2, cameraObject, anvil, test;
	private Camera cam;

	Material _m;

	private AssetManager man;

	public void init() {
		super.init();

		test = super.getRootObject();
		man = new AssetManager(super.getParentWindow().getGlfw_Handle());
		// man = super.getParentWindow().getAssetManager();
		man.setLoader(ModelScene.class, "", new ModelSceneLoader("./res/models/"));

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
		cameraObject.addComponent(new FreeLook(0.35f)).addComponent(new FreeMove(5f));
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
		_m = new Material();
		_m.addTexture("diffuse", new Texture("defaultTexture.png").load());
		_m.addTexture("normal_map", new Texture("Normal_Map_Anvil.png").load());
		_m.addFloat("specularIntensity", 1);
		_m.addFloat("specularPower", 8);
		ModelSceneLoader.Parameter parm = new ModelSceneLoader.Parameter();
		parm.loadedCallback = new LoadedCallback() {
			@Override
			public void finishedLoading(AssetManager assetManager, String fileName, AssetContainer container) {
				ModelScene m = assetManager.get(fileName, ModelScene.class);
				Mesh s = m.getMesh("monkey").bindModel();
				monkey = new Entity().addComponent(new MeshRenderer(s, _m));
				monkey.getTransform().setTranslation(new Vector3f(0f, 0f, 5f)).setScale(3);// .getRotation().rotate(Transform.X_AXIS,
				monkey.getTransform().rotate(Transform.Y_AXIS, (float) Math.toRadians(180));
				// (float) Math.toRadians(-100));
				// monkey.getTransform().getRotation().rotate(Transform.Y_AXIS, (float)
				// Math.toRadians(-180));
				// monkey.getTransform().setScale(0.025f, 0.025f, 0.025f);
				// monkey.getTransform().getRotation().rotate(Transform.Y_AXIS, (float)
				// Math.toRadians(180));
				test.addChild(monkey);

				// monkey2 = new Entity().addComponent(new MeshRenderer(s, anvilmat));
				// monkey2.getTransform().setTranslation(0, 0, 10.0f);
			}

		};

		man.load("monkey.obj", ModelScene.class, parm);

		PathHandle p = AEShader.DEFAULT_SHADER_ASSET_DIRECTORY_PATH.resolveChild("frameworkTest.ae-shader");
		AEShaderCompiler c = new AEShaderCompiler(System.out);
		// c.compile(AEShaderParserYAML.parse(p), p);
		super.setMainCamera("playerView");
		// super.addChild(spotLightObject);
		// super.addChild(new Entity()
		// .addComponent(new MeshRenderer(AIMeshLoader.loadModel("plane3.obj", "Cube",
		// 0), new BricksOne())));
		super.addChild(directionalLightObject);
		super.addChild(cameraObject);
	}

	float temp = 0.0f;
	boolean done = false, done2 = false;

	@Override
	public void update(float delta) {
		super.update(delta);
		// System.out.println(man.getQueuedAssets() + " " + man.getProgress() * 100);
		man.update();
		// temp = temp + delta;
		// float angle = (float) Math.toRadians(temp * 180);
		// monkey.getTransform().getRotation().rotate(Transform.X_AXIS, (float)
		// Math.toRadians(angle * 20));
	}

	GLFWMouseAndKeyboardInput in = (GLFWMouseAndKeyboardInput) super.getInputController();

	@Override
	public void input(float delta) {
		// in = (GLFWMouseAndKeyboardInput) super.getInputController();
		super.input(delta);
		if (in.isKeyDown(GLFWInput.GLFW_KEY_ESCAPE)) {
			in.setCursorMode(GLFW.GLFW_CURSOR_NORMAL);
		}

		if (in.isKeyDown(GLFWInput.GLFW_KEY_V)) {
			super.getParentWindow().toggleVSync();
		}

		if (in.isKeyDown(GLFWInput.GLFW_KEY_F)) {
			// super.getParentWindow().toggleFullScreen();
			ModelSceneLoader.Parameter parm = new ModelSceneLoader.Parameter();
			parm.loadedCallback = new LoadedCallback() {
				@Override
				public void finishedLoading(AssetManager assetManager, String fileName, AssetContainer container) {
					ModelScene m = assetManager.get(fileName, ModelScene.class);
					Mesh s = ((Mesh) m.getMeshes().values().toArray()[0]).bindModel();
					monkey = new Entity().addComponent(new MeshRenderer(s, _m));
					monkey.getTransform().setTranslation(new Vector3f(0f, 0f, 50f)).setScale(10);// .getRotation().rotate(Transform.X_AXIS,
					// (float) Math.toRadians(-100));
					// monkey.getTransform().getRotation().rotate(Transform.Y_AXIS, (float)
					// Math.toRadians(-180));
					// monkey.getTransform().setScale(0.025f, 0.025f, 0.025f);
					// monkey.getTransform().getRotation().rotate(Transform.Y_AXIS, (float)
					// Math.toRadians(180));
					test.addChild(monkey);

					// monkey2 = new Entity().addComponent(new MeshRenderer(s, anvilmat));
					// monkey2.getTransform().setTranslation(0, 0, 10.0f);
				}
			};
			man.load("bunny.obj", ModelScene.class, parm);
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
