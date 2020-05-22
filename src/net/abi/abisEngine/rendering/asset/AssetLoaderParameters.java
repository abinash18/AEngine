package net.abi.abisEngine.rendering.asset;
public class AssetLoaderParameters<T> {
	public interface LoadedCallback {
		public void finishedLoading(AssetManager assetManager, String fileName, AssetContainer container);
	}

	public LoadedCallback loadedCallback;
}
