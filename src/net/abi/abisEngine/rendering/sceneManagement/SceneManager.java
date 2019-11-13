package net.abi.abisEngine.rendering.sceneManagement;

import java.util.HashMap;
import java.util.Map;

import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;
import net.abi.abisEngine.rendering.windowManagement.GLFWWindow;

public class SceneManager {

	private static Logger logger = LogManager.getLogger(SceneManager.class.getName());
	private Map<String, Scene> scenes;
	private Scene currentScene;

	private GLFWWindow parentWindow;

	public SceneManager(GLFWWindow prnt) {
		parentWindow = prnt;
		scenes = new HashMap<String, Scene>();
	}

	public Scene getScene(String sceneName) {
		if (!scenes.containsKey(sceneName)) {
			new Exception("Scene Not Found!").printStackTrace();
		}
		return scenes.get(sceneName);
	}

//	public void setCoreEngine(CoreEngine coreEng) {
//		SceneManager.coreEngine = coreEng;
//		// currentScene.getRenderEngine().clearLights();
//		// currentScene.setRenderingEngine();
//		// scenes.forEach((k, v) -> v.setRenderingEngine());
//	}

	public boolean isCurrentScene(Scene scene) {
		if (getCurrentScene().getId() == scene.getId())
			return true;
		return false;
	}

	public void addScene(Scene scene) {
		if (scenes.containsValue(scene)) {
			new Exception("Scene Already Exists With The Same Name!").printStackTrace();
			return;
		}
		scene.setParentWindow(this.parentWindow);
		// scene.init();
		scenes.put(scene.getName(), scene);
		if (currentScene == null) {
			setCurrentScene(scene.getName());
		}
		logger.debug("Current Scene: " + currentScene);
	}

	public void update(float delta) {
		getCurrentScene().update(delta);
	}

	public void input(float delta) {
		getCurrentScene().input(delta);
	}

	public void initScenes() {
		scenes.forEach((k, v) -> {
			v.init();
		});
	}

	public void render() {
		if (currentScene == null) {
			new Exception("No Current Scene!");
		}
		parentWindow.getRenderEngine().render(currentScene);
	}

	public Map<String, Scene> getScenes() {
		return this.scenes;
	}

	public void setScenes(Map<String, Scene> scenes) {
		this.scenes = scenes;
	}

	public Scene getCurrentScene() {
		if (currentScene != null)
			return currentScene;

		throw new IllegalStateException("There Seems To Be No Current Scene, Cant Continue.");
	}

	public void setCurrentScene(String sceneName) {
		currentScene = scenes.get(sceneName);
		logger.debug("Setting Scene: " + sceneName);
		if (!currentScene.isInitialized()) {
			currentScene.init(); // Maybe Change this to have the user initialize the scene instead of doing it
									// Automatically.
		}
	}

	public void setCurrentScene(Scene currentScene) {
		this.currentScene = currentScene;
	}

}
