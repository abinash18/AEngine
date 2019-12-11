package net.abi.abisEngine.rendering.asset;

import java.util.concurrent.ExecutionException;

public class AssetLoadTask<T> implements AsyncTask<Void> {

	private AssetManager manager;
	private AsyncThreadDispatcher dispatch;
	private boolean asyncDone;
	private Object asset;
	private AssetLoader loader;
	private AsyncResult<Void> result;
	volatile boolean cancel = false;
	private AssetClassifier assetClassifier;

	public AssetLoadTask(AssetClassifier ac, AssetLoader loader, AssetManager manager, AsyncThreadDispatcher ad) {
		this.assetClassifier = ac;
		this.loader = loader;
		this.manager = manager;
		this.dispatch = ad;
	}

	public boolean update() {
		if (loader instanceof AsyncAssetLoader) {
			processAsynchronously();
		} else {
			processSynchronously();
		}

		if (asset == null) {
			return false;
		} else {
			return true;
		}

	}

	public <T> T getAsset(Class<T> type) {
		return (T) asset;
	}

	private void processSynchronously() {
		SynchronousLoader loader = (SynchronousLoader) this.loader;

		asset = loader.loadSync(assetClassifier.getFileName(), manager);
	}

	private void processAsynchronously() {
		AsyncAssetLoader loader = (AsyncAssetLoader) this.loader;
		if (!asyncDone) {
			if (result == null && !asyncDone) {
				result = dispatch.submit(this);
			} else {
				if (asyncDone) {
					result.get();
					asset = loader.loadSync(assetClassifier.getFileLocation(), manager);
				} else if (result.isDone()) {
					result.get();
					asset = loader.loadSync(assetClassifier.getFileName(), manager);
				}
			}
		}
	}

	@Override
	public Void call() throws ExecutionException {
		AsyncAssetLoader loader = (AsyncAssetLoader) this.loader;
		if (!asyncDone) {
			loader.loadAsync(assetClassifier.getFileName(), manager);
			asyncDone = true;
		}
		return null;
	}

	public AssetClassifier getAssetClassifier() {
		return assetClassifier;
	}

}
