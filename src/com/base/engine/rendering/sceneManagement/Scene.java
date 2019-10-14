package com.base.engine.rendering.sceneManagement;

import java.util.ArrayList;
import java.util.UUID;

import com.base.engine.components.BaseLight;
import com.base.engine.components.Camera;
import com.base.engine.components.GameComponent;
import com.base.engine.core.GameObject;
import com.base.engine.rendering.RenderingEngine;

public abstract class Scene {

	private GameObject root;
	private ArrayList<BaseLight> lights;
	private Camera mainCamera;
	private UUID id = UUID.randomUUID();
	private String name;
	private boolean initialized = false;

	public Scene(String name) {
		this.name = name;
		this.lights = new ArrayList<BaseLight>();
		this.setAsParentScene();
		this.addToSceneManager();
	}

	public UUID getId() {
		return id;
	}

	public void addToSceneManager() {
		SceneManager.addScene(this);
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

	public void addChild(GameObject child) {
		this.getRootObject().addChild(child);
	}

	public void addComponent(GameComponent component) {
		this.getRootObject().addComponent(component);
	}

	public GameObject getRootObject() {
		if (root == null) {
			root = new GameObject();
			// root.setCoreEngine(coreEngine);
		}
		return root;

	}

	public GameObject getRoot() {
		return root;
	}

	public void setRoot(GameObject root) {
		this.root = root;
	}

	public void setAsParentScene() {
		this.getRootObject().setParentScene(this);
	}

	@Override
	public void finalize() throws Throwable {
		super.finalize();
	}

	public ArrayList<BaseLight> getLights() {
		return lights;
	}

	public void setLights(ArrayList<BaseLight> lights) {
		this.lights = lights;
	}

	public Camera getMainCamera() {
		return mainCamera;
	}

	public void setMainCamera(Camera mainCamera) {
		this.mainCamera = mainCamera;
	}

	public void addLight(BaseLight light) {
		this.lights.add(light);
	}
}
