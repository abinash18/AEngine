package net.abi.abisEngine.rendering.asset.loaders;

import org.lwjgl.opengl.GL15;

import net.abi.abisEngine.rendering.asset.AssetLoaderParameters;
import net.abi.abisEngine.rendering.asset.AssetManager;
import net.abi.abisEngine.rendering.mesh.AIMeshLoader;
import net.abi.abisEngine.rendering.mesh.ModelScene;

public class ModelSceneLoader extends AsyncAssetLoader<ModelScene, ModelSceneLoader.Parameter> {

	public ModelSceneLoader(String assetsDir) {
		super(assetsDir);
	}

	ModelScene scene = null;

	static public class Parameter extends AssetLoaderParameters<ModelScene> {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.abi.abisEngine.rendering.asset.AsyncAssetLoader#loadAsync(java.lang.
	 * String, net.abi.abisEngine.rendering.asset.AssetManager)
	 */
	@Override
	public void loadAsync(String fileName, AssetManager manager) {
		scene = AIMeshLoader.loadModelScene(super.resolve(fileName), 0, manager);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.abi.abisEngine.rendering.asset.AsyncAssetLoader#loadSync(java.lang.
	 * String, net.abi.abisEngine.rendering.asset.AssetManager)
	 */
	@Override
	public ModelScene loadSync(String fileName, AssetManager manager) {
		scene.bindModels(GL15.GL_STATIC_DRAW);
		return scene;
	}
}
