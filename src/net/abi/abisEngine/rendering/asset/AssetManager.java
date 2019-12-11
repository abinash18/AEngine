package net.abi.abisEngine.rendering.asset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;
import net.abi.abisEngine.rendering.asset.loaders.ModelSceneLoader;
import net.abi.abisEngine.rendering.meshLoading.ModelScene;
import net.abi.abisEngine.util.AERuntimeException;

/**
 * 
 */

/**
 * @author abinash
 *
 */
public class AssetManager {

	private static Logger logger = LogManager.getLogger(AssetManager.class);

	final HashMap<Class, HashMap<String, AssetLoader>> loaders = new HashMap();
	final ArrayList<AssetClassifier> loadQueue = new ArrayList<AssetClassifier>();
	final AsyncThreadDispatcher executor;

	final Stack<AssetLoadTask> tasks = new Stack();

	public AssetStore store;

	int loaded = 0;
	int toLoad = 0;
	int peakTasks = 0;

	private String assetsDir = "./res/";

	long glfw_window_handle;

	/** Creates a new AssetManager with all default loaders. */
	public AssetManager(long window) {
		this(null, true, window);
	}

	/**
	 * Creates a new AssetManager with optionally all default loaders. If you don't
	 * add the default loaders then you do have to manually add the loaders you
	 * need, including any loaders they might depend on.
	 * 
	 * @param defaultLoaders whether to add the default loaders
	 */
	public AssetManager(String assetsDir, boolean defaultLoaders, long window) {

		this.glfw_window_handle = window;

		if (assetsDir != null) {
			this.assetsDir = assetsDir;
		}

		if (defaultLoaders) {
			setLoader(ModelScene.class, new ModelSceneLoader(assetsDir + "models/"));
		}
		executor = new AsyncThreadDispatcher(1, "AssetManager");
		this.store = new AssetStore();
	}

	public String getAssetsDir() {
		return assetsDir;
	}

	/**
	 * @param fileName the asset file name
	 * @return the asset
	 */
	public synchronized <T> T get(String fileName) {
		return store.getAsset(fileName);
	}

	public synchronized <T> T get(String fileName, Class<T> type) {
		return store.getAsset(type, fileName);
	}

	public synchronized boolean contains(String fileName, Class type) {

		boolean _contains = false;

		_contains = store.contains(type, fileName);

		for (int i = 0; i < loadQueue.size(); i++) {
			AssetClassifier assetDesc = loadQueue.get(i);
			if (assetDesc.getType() == type && assetDesc.getFileName().equals(fileName))
				return true;
		}

		_contains = isLoaded(fileName, type);
		return _contains;
	}

	public synchronized boolean isLoaded(String fileName) {
		if (fileName == null)
			return false;
		return store.getAssetTypes().containsKey(fileName);
	}

	public synchronized boolean isLoaded(String fileName, Class type) {
		ConcurrentHashMap<String, AssetContainer> assetsByType = store.get(type);
		if (assetsByType == null)
			return false;
		AssetContainer assetContainer = assetsByType.get(fileName);
		if (assetContainer == null)
			return false;
		return assetContainer.getObject(type) != null;
	}

	public <T> AssetLoader getLoader(final Class<T> type, final String fileName) {
		final HashMap<String, AssetLoader> loaders = this.loaders.get(type);
		if (loaders == null || loaders.size() < 1)
			return null;
		if (fileName == null)
			return loaders.get("");
		AssetLoader result = null;
		int l = -1;
		for (HashMap.Entry<String, AssetLoader> entry : loaders.entrySet()) {
			if (entry.getKey().length() > l && fileName.endsWith(entry.getKey())) {
				result = entry.getValue();
				l = entry.getKey().length();
			}
		}
		return result;
	}

	public synchronized <T> void load(String fileName, Class<T> type, AssetLoaderParameters<T> parameter) {
		AssetLoader loader = getLoader(type, fileName);
		if (loader == null)
			throw new AERuntimeException("No loader for type: " + type.getSimpleName());

		// reset stats
		if (loadQueue.size() == 0) {
			loaded = 0;
			toLoad = 0;
			peakTasks = 0;
		}

		// check if an asset with the same name but a different type has already been
		// added.

		// check preload queue
		for (int i = 0; i < loadQueue.size(); i++) {
			AssetClassifier desc = loadQueue.get(i);
			if (desc.getFileName().equals(fileName) && !desc.getType().equals(type))
				throw new AERuntimeException("Asset with name '" + fileName
						+ "' already in preload queue, but has different type (expected: " + type.getSimpleName()
						+ ", found: " + desc.getType().getSimpleName() + ")");
		}

		// check task list
		for (int i = 0; i < tasks.size(); i++) {
			AssetClassifier desc = tasks.get(i).getAssetClassifier();
			if (desc.getFileName().equals(fileName) && !desc.getType().equals(type))
				throw new AERuntimeException(
						"Asset with name '" + fileName + "' already in task list, but has different type (expected: "
								+ type.getSimpleName() + ", found: " + desc.getType().getSimpleName() + ")");
		}

		// check loaded assets
		Class otherType = store.getAssetTypes().get(fileName);
		if (otherType != null && !otherType.equals(type))
			throw new AERuntimeException(
					"Asset with name '" + fileName + "' already loaded, but has different type (expected: "
							+ type.getSimpleName() + ", found: " + otherType.getSimpleName() + ")");

		toLoad++;
		AssetClassifier assetDesc = new AssetClassifier(fileName, type, parameter);
		loadQueue.add(assetDesc);
		logger.debug("Queued: " + assetDesc);
	}

	public synchronized boolean update() {
		try {
			if (tasks.size() == 0) {
				// loop until we have a new task ready to be processed
				while (loadQueue.size() != 0 && tasks.size() == 0) {
					nextTask();
				}
				// have we not found a task? We are done!
				if (tasks.size() == 0)
					return true;
			}
			return updateTask() && loadQueue.size() == 0 && tasks.size() == 0;
		} catch (Exception t) {
			handleTaskError(t);
			return loadQueue.size() == 0;
		}
	}

	public synchronized boolean isFinished() {
		return loadQueue.size() == 0 && tasks.size() == 0;
	}

	/**
	 * Finishes loading the asset specified. It has to be added to the load queue
	 * 
	 * @param fileName
	 * @return
	 */
	public <T> T finishLoadingAsset(String fileName) {
		logger.debug("Waiting for asset to be loaded: " + fileName);
		while (true) {
			synchronized (this) {
				Class<T> type = store.assetTypes.get(fileName);
				if (type != null) {
					ConcurrentHashMap<String, AssetContainer> assetsByType = store.get(type);
					if (assetsByType != null) {
						AssetContainer assetContainer = assetsByType.get(fileName);
						if (assetContainer != null) {
							T asset = assetContainer.getObject(type);
							if (asset != null) {
								logger.debug("Asset loaded: " + fileName);
								return asset;
							}
						}
					}
				}
				update();
			}
			Thread.yield();
		}
	}

	private void nextTask() {
		AssetClassifier assetDesc = loadQueue.remove(0);

		// if the asset not meant to be reloaded and is already loaded, increase its
		// reference count
		if (isLoaded(assetDesc.getFileName())) {
			logger.debug("Already loaded: " + assetDesc);
			Class type = store.assetTypes.get(assetDesc.getFileName());
			AssetContainer assetRef = store.get(type).get(assetDesc.getFileName());
			assetRef.incRefCount();
			if (assetDesc.getParameter() != null && assetDesc.getParameter().loadedCallback != null) {
				assetDesc.getParameter().loadedCallback.finishedLoading(this, assetDesc.getFileName(),
						assetDesc.getType());
			}
			loaded++;
		} else {
			// else add a new task for the asset.
			logger.info("Loading: " + assetDesc);
			addTask(assetDesc);
		}
	}

	private void addTask(AssetClassifier assetDesc) {
		AssetLoader loader = getLoader(assetDesc.getType(), assetDesc.getFileName());
		if (loader == null)
			throw new AERuntimeException("No loader for type: " + assetDesc.getType().getSimpleName());
		tasks.push(new AssetLoadTask(assetDesc, loader, this, executor));
		peakTasks++;
	}

	protected <T> void addAsset(final String fileName, Class<T> type, T asset) {
		store.addAsset(type, fileName, asset);
	}

	private boolean updateTask() {
		AssetLoadTask task = tasks.peek();

		boolean complete = true;
		try {
			complete = task.cancel || task.update();
		} catch (RuntimeException ex) {
			task.cancel = true;
			taskFailed(task.getAssetClassifier(), ex);
		}

		// if the task has been cancelled or has finished loading
		if (complete) {
			// increase the number of loaded assets and pop the task from the stack
			if (tasks.size() == 1) {
				loaded++;
				peakTasks = 0;
			}
			tasks.pop();

			if (task.cancel)
				return true;

			addAsset(task.getAssetClassifier().getFileName(), task.getAssetClassifier().getType(),
					task.getAsset(task.getAssetClassifier().getType()));

			// otherwise, if a listener was found in the parameter invoke it
			if (task.getAssetClassifier().getParameter() != null
					&& task.getAssetClassifier().getParameter().loadedCallback != null) {
				task.getAssetClassifier().getParameter().loadedCallback.finishedLoading(this,
						task.getAssetClassifier().getFileName(), task.getAssetClassifier().getType());
			}

			return true;
		}
		return false;
	}

	protected void taskFailed(AssetClassifier assetDesc, RuntimeException ex) {
		throw ex;
	}

	private void handleTaskError(Exception t) {
		logger.error("Error loading asset.", t);

		if (tasks.isEmpty())
			throw new AERuntimeException(t);

		// pop the faulty task from the stack
		AssetLoadTask task = tasks.pop();
		AssetClassifier assetDesc = task.getAssetClassifier();

		// clear the rest of the stack
		tasks.clear();

		// inform the listener that something bad happened

		throw new AERuntimeException(t);

	}

	public synchronized <T, P extends AssetLoaderParameters<T>> void setLoader(Class<T> type,
			AssetLoader<T, P> loader) {
		setLoader(type, null, loader);
	}

	public synchronized <T, P extends AssetLoaderParameters<T>> void setLoader(Class<T> type, String suffix,
			AssetLoader<T, P> loader) {
		if (type == null)
			throw new IllegalArgumentException("type cannot be null.");
		if (loader == null)
			throw new IllegalArgumentException("loader cannot be null.");
		logger.debug("Loader set: " + type.getSimpleName() + " -> " + loader.getClass().getSimpleName());
		HashMap<String, AssetLoader> loaders = this.loaders.get(type);
		if (loaders == null)
			this.loaders.put(type, loaders = new HashMap<String, AssetLoader>());
		loaders.put(suffix == null ? "" : suffix, loader);
	}

	public synchronized int getLoadedAssets() {
		return store.assetTypes.size();
	}

	public synchronized int getQueuedAssets() {
		return loadQueue.size() + tasks.size();
	}

	public synchronized float getProgress() {
		if (toLoad == 0)
			return 1;
		float fractionalLoaded = (float) loaded;
		if (peakTasks > 0) {
			fractionalLoaded += ((peakTasks - tasks.size()) / (float) peakTasks);
		}
		return Math.min(1, fractionalLoaded / (float) toLoad);
	}

	public synchronized void dispose() {
		logger.debug("Disposing.");
		executor.dispose();
	}

	public synchronized int getReferenceCount(String fileName) {
		Class type = store.assetTypes.get(fileName);
		if (type == null)
			throw new AERuntimeException("Asset not loaded: " + fileName);
		return store.assets.get(type).get(fileName).getRefCount();
	}

	/**
	 * @return
	 */
	public long getGLFW_HANDLE() {
		return glfw_window_handle;
	}
}
