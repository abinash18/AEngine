
package net.abi.abisEngine.rendering.asset;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import net.abi.abisEngine.rendering.asset.AssetLoader;
import net.abi.abisEngine.rendering.asset.AsyncAssetLoader;
import net.abi.abisEngine.rendering.asset.SynchronousLoader;
import net.abi.abisEngine.util.AERuntimeException;

/**
 * Responsible for loading an asset through an {@link AssetLoader} based on an
 * {@link AssetDescriptor}.
 * 
 * @author mzechner
 */
class AssetLoadTask implements AsyncTask<Void> {
	AssetManager manager;
	AssetClassifier assetDesc;
	AssetLoader loader;
	AsyncThreadDispatcher executor;

	volatile boolean asyncDone = false;
	volatile boolean dependenciesLoaded = false;
	volatile ArrayList<AssetClassifier> dependencies;
	volatile AsyncResult<Void> depsFuture = null;
	volatile AsyncResult<Void> loadFuture = null;
	volatile Object asset = null;

	int ticks = 0;
	volatile boolean cancel = false;

	public AssetLoadTask(AssetManager manager, AssetClassifier assetDesc, AssetLoader loader,
			AsyncThreadDispatcher threadPool) {
		this.manager = manager;
		this.assetDesc = assetDesc;
		this.loader = loader;
		this.executor = threadPool;
	}

	/**
	 * Loads parts of the asset asynchronously if the loader is an
	 * {@link AsynchronousAssetLoader}.
	 */
	public Void call() throws ExecutionException {
		AsyncAssetLoader asyncLoader = (AsyncAssetLoader) loader;
		asyncLoader.loadAsync(assetDesc.getFileName(), manager);
		asyncDone = true;
		return null;
	}

	/**
	 * Updates the loading of the asset. In case the asset is loaded with an
	 * {@link AsynchronousAssetLoader}, the loaders
	 * {@link AsynchronousAssetLoader#loadAsync(AssetManager, String, FileHandle, AssetLoaderParameters)}
	 * method is first called on a worker thread. Once this method returns, the rest
	 * of the asset is loaded on the rendering thread via
	 * {@link AsynchronousAssetLoader#loadSync(AssetManager, String, FileHandle, AssetLoaderParameters)}.
	 * 
	 * @return true in case the asset was fully loaded, false otherwise
	 * @throws GdxRuntimeException
	 */
	public boolean update() {
		ticks++;
		if (loader instanceof SynchronousLoader) {
			handleSyncLoader();
		} else {
			handleAsyncLoader();
		}
		return asset != null;
	}

	private void handleSyncLoader() {
		SynchronousLoader syncLoader = (SynchronousLoader) loader;

		asset = syncLoader.loadSync(assetDesc.getFileName(), manager);

	}

	private void handleAsyncLoader() {
		AsyncAssetLoader asyncLoader = (AsyncAssetLoader) loader;

		if (loadFuture == null && !asyncDone) {
			loadFuture = executor.submit(this);
		} else {
			if (asyncDone) {
				asset = asyncLoader.loadSync(assetDesc.getFileName(), manager);
			} else if (loadFuture.isDone()) {
				try {
					loadFuture.get();
				} catch (Exception e) {
					throw new AERuntimeException("Couldn't load asset: " + assetDesc.getFileName(), e);
				}
				asset = asyncLoader.loadSync(assetDesc.getFileName(), manager);
			}
		}

	}

	public Object getAsset() {
		return asset;
	}

	private void removeDuplicates(ArrayList<AssetClassifier> array) {
		for (int i = 0; i < array.size(); ++i) {
			final String fn = array.get(i).getFileName();
			final Class type = array.get(i).getType();
			for (int j = array.size() - 1; j > i; --j) {
				if (type == array.get(j).getType() && fn.equals(array.get(j).getFileName()))
					array.remove(j);
			}
		}
	}
}
