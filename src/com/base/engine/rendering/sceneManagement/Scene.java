package com.base.engine.rendering.sceneManagement;

import java.util.UUID;

import com.base.engine.components.GameComponent;
import com.base.engine.core.GameObject;
import com.base.engine.rendering.RenderingEngine;

public abstract class Scene {

	private RenderingEngine renderEngine;

	private GameObject root;

	private UUID id = UUID.randomUUID();
	private String name;
	private boolean initialized = false;

	public Scene(String name) {
		this.name = name;
		getRenderEngine();
		setRenderingEngine();
		this.addToSceneManager();
	}

	public Scene(String name, RenderingEngine rendEng) {
		this.name = name;
		renderEngine = rendEng;
		setRenderingEngine();
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
			getRootObject().init();
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
		getRootObject().updateAll(delta);
	}

	public void input(float delta) {
		getRootObject().inputAll(delta);
	}

	public void render(RenderingEngine engine) {
		engine.render(getRootObject());
	}

	public void addChild(GameObject child) {
		getRootObject().addChild(child);
	}

	public void addComponent(GameComponent component) {
		getRootObject().addComponent(component);
	}

	private GameObject getRootObject() {
		if (root == null) {
			root = new GameObject();
			// root.setCoreEngine(coreEngine);
		}
		return root;

	}

	public RenderingEngine getRenderEngine() {
		if (renderEngine == null) {
			renderEngine = new RenderingEngine();
		}
		return renderEngine;
	}

	public GameObject getRoot() {
		return root;
	}

	public void setRoot(GameObject root) {
		this.root = root;
	}

	public void setRenderingEngine() {
		this.getRootObject().setRenderingEngine(renderEngine);
	}

	@Override
	public void finalize() throws Throwable {
		super.finalize();
	}
}
