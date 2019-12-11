package net.abi.abisEngine.rendering.asset;

public abstract class AsyncAssetLoader<T, P extends AssetLoaderParameters<T>> extends AssetLoader<T, P> {

	public AsyncAssetLoader(String assetsDir) {
		super(assetsDir);
	}

	public abstract void loadAsync(String fileName, AssetManager manager);

	public abstract T loadSync(String fileName, AssetManager manager);

}
