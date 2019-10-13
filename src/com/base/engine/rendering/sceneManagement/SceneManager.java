package com.base.engine.rendering.sceneManagement;

import java.util.HashMap;
import java.util.Map;

import com.base.engine.core.CoreEngine;
import com.base.engine.handlers.logging.LogManager;
import com.base.engine.handlers.logging.Logger;
import com.base.engine.rendering.RenderingEngine;

public class SceneManager {

	private static Logger logger = LogManager.getLogger(SceneManager.class.getName());
	private static Map<String, Scene> scenes = new HashMap<String, Scene>();
	private static Scene currentScene = null;
	private static CoreEngine coreEngine;

	public SceneManager() {
	}

	public Scene getScene(String sceneName) {
		if (!scenes.containsKey(sceneName)) {
			new Exception("Scene Not Found!").printStackTrace();
		}
		return scenes.get(sceneName);
	}

	private static void setCoreEngine() {
		coreEngine.getRenderEngine().clearLights();
		currentScene.setCoreEngine(coreEngine);
		// scenes.forEach((k, v) -> v.setCoreEngine(SceneManager.coreEngine));
	}

	public static boolean isCurrentScene(Scene scene) {
		if (currentScene.getId() == scene.getId())
			return true;
		return false;
	}

	public static void addScene(Scene scene) {
		if (scenes.containsValue(scene)) {
			new Exception("Scene Already Exists With The Same Name!").printStackTrace();
			return;
		}
		scene.setCoreEngine(coreEngine);
		// scene.init();
		scenes.put(scene.getName(), scene);
		if (currentScene == null) {
			setCurrentScene(scene.getName());
		}
		logger.debug("Current Scene: " + currentScene);
	}

	public static void init(CoreEngine coreEngine) {
		SceneManager.coreEngine = coreEngine;
		setCoreEngine();
		// scenes.forEach((k, v) -> v.init());
	}

	public static void update(float delta) {
		currentScene.update(delta);
	}

	public static void input(float delta) {
		currentScene.input(delta);
	}

	public static void render(RenderingEngine engine) {
		if (currentScene == null) {
			new Exception("No Current Scene!").printStackTrace();
		}
		currentScene.render(engine);
	}

	public static Map<String, Scene> getScenes() {
		return SceneManager.scenes;
	}

	public static void setScenes(Map<String, Scene> scenes) {
		SceneManager.scenes = scenes;
	}

	public static Scene getCurrentScene() {
		return currentScene;
	}

	public static void setCurrentScene(String sceneName) {
		currentScene = scenes.get(sceneName);
		logger.debug("Setting Scene: " + sceneName);
		if (!currentScene.isInitialized()) {
			currentScene.init();
		}
		// setCoreEngine();
	}

}
