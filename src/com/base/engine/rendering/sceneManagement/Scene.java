package com.base.engine.rendering.sceneManagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.base.engine.components.Camera;
import com.base.engine.components.Light;
import com.base.engine.components.SceneComponent;
import com.base.engine.core.Entity;
import com.base.engine.core.input.GLFWInput;
import com.base.engine.rendering.RenderingEngine;
import com.base.engine.rendering.windowManagement.GLFWWindow;

public abstract class Scene {

	private Entity root;
	private ArrayList<Light> lights;
	private Map<String, Camera> cameras;
	private Camera mainCamera;
	private UUID id = UUID.randomUUID();
	private String name;
	private boolean initialized = false;
	private GLFWWindow parentWindow;

	public Scene(String name) {
		this.name = name;
		this.lights = new ArrayList<Light>();
		this.cameras = new HashMap<String, Camera>();
		this.setAsParentScene();
		this.addToSceneManager();
	}

	public GLFWInput getInputController() {
		return parentWindow.getInput();
	}

	public UUID getId() {
		return id;
	}

	public void addToSceneManager() {
		parentWindow.getSceneManager().addScene(this);
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Scene [root=" + root + ", id=" + id + ", name=" + name + "]";
	}

	public void setName(String name) {
		this.name = name;
	}

	public void init() {
		if (!initialized) {
			this.getRootObject().init();
			this.initialized = true;
			return;
		}
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	public void update(float delta) {
		this.getRootObject().updateAll(delta);
	}

	public void input(float delta) {
		this.getRootObject().inputAll(delta);
	}

	@Deprecated
	public void render(RenderingEngine rndEng) {
		rndEng.render(this);
	}

	public void addChild(Entity child) {
		this.getRootObject().addChild(child);
	}

	public void addComponent(SceneComponent component) {
		this.getRootObject().addComponent(component);
	}

	public Entity getRootObject() {
		if (this.root == null) {
			this.root = new Entity();
			// root.setCoreEngine(coreEngine);
		}
		return root;

	}

	public Entity getRoot() {
		return root;
	}

	public void setRoot(Entity root) {
		this.root = root;
	}

	public void setAsParentScene() {
		this.getRootObject().setParentScene(this);
	}

	@Override
	public void finalize() throws Throwable {
		super.finalize();
	}

	public ArrayList<Light> getLights() {
		return lights;
	}

	public void setLights(ArrayList<Light> lights) {
		this.lights = lights;
	}

	public Camera getMainCamera() {
		return mainCamera;
	}

	public Camera getCamera(String name) {
		return cameras.get(name);
	}

	public void addCamera(Camera cam) {
		this.cameras.put(cam.getName(), cam);
		if (this.mainCamera == null) {
			this.mainCamera = cam;
		}
	}

	public void addLight(Light light) {
		this.lights.add(light);
	}

	public Map<String, Camera> getCameras() {
		return cameras;
	}

	public void setCameras(Map<String, Camera> cameras) {
		this.cameras = cameras;
	}

	public void setMainCamera(String name) {
		this.mainCamera = cameras.get(name);
	}

	public GLFWWindow getParentWindow() {
		return parentWindow;
	}

	public void setParentWindow(GLFWWindow parentWindow) {
		this.parentWindow = parentWindow;
	}

	public void setMainCamera(Camera mainCamera) {
		this.mainCamera = mainCamera;
	}
}
