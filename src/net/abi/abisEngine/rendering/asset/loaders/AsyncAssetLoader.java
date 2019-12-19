package net.abi.abisEngine.rendering.asset.loaders;

import net.abi.abisEngine.rendering.asset.AssetLoaderParameters;
import net.abi.abisEngine.rendering.asset.AssetManager;

public abstract class AsyncAssetLoader<T, P extends AssetLoaderParameters<T>> extends AssetLoader<T, P> {

	public AsyncAssetLoader(String assetsDir) {
		super(assetsDir);
	}

	public abstract void loadAsync(String fileName, AssetManager manager);

	public abstract T loadSync(String fileName, AssetManager manager);

}
