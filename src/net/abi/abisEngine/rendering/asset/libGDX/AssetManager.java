/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package net.abi.abisEngine.rendering.asset.libGDX;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;
import net.abi.abisEngine.rendering.asset.loaders.AssetLoader;
import net.abi.abisEngine.rendering.asset.loaders.ModelSceneLoader;
import net.abi.abisEngine.rendering.meshLoading.ModelScene;
import net.abi.abisEngine.util.AERuntimeException;
import net.abi.abisEngine.util.Expendable;
import net.abi.abisEngine.util.ThreadUtils;
import net.abi.abisEngine.util.async.AsyncThreadDispatcher;

/**
 * This class was adopted form LibGDX, Because I was Too Lazy to make my own.
 */
/**
 * Loads and stores assets like textures, bitmapfonts, tile maps, sounds, music
 * and so on.
 * 
 * @author mzechner
 */
public class AssetManager implements Expendable {

	private static Logger logger = LogManager.getLogger(AssetManager.class);

	final HashMap<Class, HashMap<String, AssetClassifier>> assets = new HashMap();
	final HashMap<String, Class> assetTypes = new HashMap();
	final HashMap<String, ArrayList<String>> assetDependencies = new HashMap();
	final HashSet<String> injected = new HashSet();

	final HashMap<Class, HashMap<String, AssetLoader>> loaders = new HashMap();
	final ArrayList<AssetClassifier> loadQueue = new ArrayList<AssetClassifier>();
	final AsyncThreadDispatcher executor;

	final Stack<AssetLoadingTask> tasks = new Stack();
	AssetErrorListener listener = null;
	int loaded = 0;
	int toLoad = 0;
	int peakTasks = 0;

	private String assetsDir = "./res/";

	/** Creates a new AssetManager with all default loaders. */
	public AssetManager() {
		this(null, true);
	}

	/**
	 * Creates a new AssetManager with optionally all default loaders. If you don't
	 * add the default loaders then you do have to manually add the loaders you
	 * need, including any loaders they might depend on.
	 * 
	 * @param defaultLoaders whether to add the default loaders
	 */
	public AssetManager(String assetsDir, boolean defaultLoaders) {

		if (assetsDir != null) {
			this.assetsDir = assetsDir;
		}

		if (defaultLoaders) {
			setLoader(ModelScene.class, new ModelSceneLoader(assetsDir + "models/"));
		}
		executor = new AsyncThreadDispatcher(1, "AssetManager");
	}

	public String getAssetsDir() {
		return assetsDir;
	}

	/**
	 * @param fileName the asset file name
	 * @return the asset
	 */
	public synchronized <T> T get(String fileName) {
		Class<T> type = assetTypes.get(fileName);
		if (type == null)
			throw new AERuntimeException("Asset not loaded: " + fileName);
		HashMap<String, AssetClassifier> assetsByType = assets.get(type);
		if (assetsByType == null)
			throw new AERuntimeException("Asset not loaded: " + fileName);
		AssetClassifier assetContainer = assetsByType.get(fileName);
		if (assetContainer == null)
			throw new AERuntimeException("Asset not loaded: " + fileName);
		T asset = assetContainer.getObject(type);
		if (asset == null)
			throw new AERuntimeException("Asset not loaded: " + fileName);

		return asset;
	}

	/**
	 * @param fileName the asset file name
	 * @param type     the asset type
	 * @return the asset
	 */
	public synchronized <T> T get(String fileName, Class<T> type) {
		HashMap<String, AssetClassifier> assetsByType = assets.get(type);
		if (assetsByType == null)
			throw new AERuntimeException("Asset not loaded: " + fileName);
		AssetClassifier assetContainer = assetsByType.get(fileName);
		if (assetContainer == null)
			throw new AERuntimeException("Asset not loaded: " + fileName);
		T asset = assetContainer.getObject(type);
		if (asset == null)
			throw new AERuntimeException("Asset not loaded: " + fileName);
		return asset;
	}

	/**
	 * @param type the asset type
	 * @return all the assets matching the specified type
	 */
	public synchronized <T> ArrayList<T> getAll(Class<T> type, ArrayList<T> out) {
		HashMap<String, AssetClassifier> assetsByType = assets.get(type);
		if (assetsByType != null) {
			for (HashMap.Entry<String, AssetClassifier> asset : assetsByType.entrySet()) {
				out.add(asset.getValue().getObject(type));
			}
		}
		return out;
	}

	/**
	 * @param assetDescriptor the asset descriptor
	 * @return the asset
	 */
	public synchronized <T> T get(AssetClassifier<T> assetDescriptor) {
		return get(assetDescriptor.fileName, assetDescriptor.type);
	}

	/**
	 * Returns true if an asset with the specified name is loading, queued to be
	 * loaded, or has been loaded.
	 */
	public synchronized boolean contains(String fileName) {
		if (tasks.size() > 0 && tasks.firstElement().assetDesc.fileName.equals(fileName))
			return true;

		for (int i = 0; i < loadQueue.size(); i++)
			if (loadQueue.get(i).fileName.equals(fileName))
				return true;

		return isLoaded(fileName);
	}

	/**
	 * Returns true if an asset with the specified name and type is loading, queued
	 * to be loaded, or has been loaded.
	 */
	public synchronized boolean contains(String fileName, Class type) {
		if (tasks.size() > 0) {
			AssetClassifier assetDesc = tasks.firstElement().assetDesc;
			if (assetDesc.type == type && assetDesc.fileName.equals(fileName))
				return true;
		}

		for (int i = 0; i < loadQueue.size(); i++) {
			AssetClassifier assetDesc = loadQueue.get(i);
			if (assetDesc.type == type && assetDesc.fileName.equals(fileName))
				return true;
		}

		return isLoaded(fileName, type);
	}

	/**
	 * Removes the asset and all its dependencies, if they are not used by other
	 * assets.
	 * 
	 * @param fileName the file name
	 */
	public synchronized void unload(String fileName) {
		// check if it's currently processed (and the first element in the stack, thus
		// not a dependency)
		// and cancel if necessary
		if (tasks.size() > 0) {
			AssetLoadingTask currAsset = tasks.firstElement();
			if (currAsset.assetDesc.fileName.equals(fileName)) {
				currAsset.cancel = true;
				logger.info("Unload (from tasks): " + fileName);
				return;
			}
		}

		// check if it's in the queue
		int foundIndex = -1;
		for (int i = 0; i < loadQueue.size(); i++) {
			if (loadQueue.get(i).fileName.equals(fileName)) {
				foundIndex = i;
				break;
			}
		}
		if (foundIndex != -1) {
			toLoad--;
			loadQueue.remove(foundIndex);
			logger.info("Unload (from queue): " + fileName);
			return;
		}

		// get the asset and its type
		Class type = assetTypes.get(fileName);
		if (type == null)
			throw new AERuntimeException("Asset not loaded: " + fileName);

		AssetClassifier assetRef = assets.get(type).get(fileName);

		// if it is reference counted, decrement ref count and check if we can really
		// get rid of it.
		assetRef.decRefCount();
		if (assetRef.getRefCount() <= 0) {
			logger.info("Unload (dispose): " + fileName);

			// if it is disposable dispose it
			if (assetRef.getObject(Object.class) instanceof Expendable)
				((Expendable) assetRef.getObject(Object.class)).dispose();

			// remove the asset from the manager.
			assetTypes.remove(fileName);
			assets.get(type).remove(fileName);
		} else {
			logger.info("Unload (decrement): " + fileName);
		}

		// remove any dependencies (or just decrement their ref count).
		ArrayList<String> dependencies = assetDependencies.get(fileName);
		if (dependencies != null) {
			for (String dependency : dependencies) {
				if (isLoaded(dependency))
					unload(dependency);
			}
		}
		// remove dependencies if ref count < 0
		if (assetRef.getRefCount() <= 0) {
			assetDependencies.remove(fileName);
		}
	}

	/**
	 * @param asset the asset
	 * @return whether the asset is contained in this manager
	 */
	public synchronized <T> boolean containsAsset(T asset) {
		HashMap<String, AssetClassifier> assetsByType = assets.get(asset.getClass());
		if (assetsByType == null)
			return false;
		for (String fileName : assetsByType.keySet()) {
			T otherAsset = (T) assetsByType.get(fileName).getObject(Object.class);
			if (otherAsset == asset || asset.equals(otherAsset))
				return true;
		}
		return false;
	}

	/**
	 * @param asset the asset
	 * @return the filename of the asset or null
	 */
	public synchronized <T> String getAssetFileName(T asset) {
		for (Class assetType : assets.keySet()) {
			HashMap<String, AssetClassifier> assetsByType = assets.get(assetType);
			for (String fileName : assetsByType.keySet()) {
				T otherAsset = (T) assetsByType.get(fileName).getObject(Object.class);
				if (otherAsset == asset || asset.equals(otherAsset))
					return fileName;
			}
		}
		return null;
	}

	/**
	 * @param assetDesc the AssetClassifier of the asset
	 * @return whether the asset is loaded
	 */
	public synchronized boolean isLoaded(AssetClassifier assetDesc) {
		return isLoaded(assetDesc.fileName);
	}

	/**
	 * @param fileName the file name of the asset
	 * @return whether the asset is loaded
	 */
	public synchronized boolean isLoaded(String fileName) {
		if (fileName == null)
			return false;
		return assetTypes.containsKey(fileName);
	}

	/**
	 * @param fileName the file name of the asset
	 * @return whether the asset is loaded
	 */
	public synchronized boolean isLoaded(String fileName, Class type) {
		HashMap<String, AssetClassifier> assetsByType = assets.get(type);
		if (assetsByType == null)
			return false;
		AssetClassifier assetContainer = assetsByType.get(fileName);
		if (assetContainer == null)
			return false;
		return assetContainer.getObject(type) != null;
	}

	/**
	 * Returns the default loader for the given type
	 * 
	 * @param type The type of the loader to get
	 * @return The loader capable of loading the type, or null if none exists
	 */
	public <T> AssetLoader getLoader(final Class<T> type) {
		return getLoader(type, null);
	}

	/**
	 * Returns the loader for the given type and the specified filename. If no
	 * loader exists for the specific filename, the default loader for that type is
	 * returned.
	 * 
	 * @param type     The type of the loader to get
	 * @param fileName The filename of the asset to get a loader for, or null to get
	 *                 the default loader
	 * @return The loader capable of loading the type and filename, or null if none
	 *         exists
	 */
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

	/**
	 * Adds the given asset to the loading queue of the AssetManager.
	 * 
	 * @param fileName the file name (interpretation depends on {@link AssetLoader})
	 * @param type     the type of the asset.
	 */
	public synchronized <T> void load(String fileName, Class<T> type) {
		load(fileName, type, null);
	}

	/**
	 * Adds the given asset to the loading queue of the AssetManager.
	 * 
	 * @param fileName  the file name (interpretation depends on
	 *                  {@link AssetLoader})
	 * @param type      the type of the asset.
	 * @param parameter parameters for the AssetLoader.
	 */
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
			if (desc.fileName.equals(fileName) && !desc.type.equals(type))
				throw new AERuntimeException("Asset with name '" + fileName
						+ "' already in preload queue, but has different type (expected: " + type.getSimpleName()
						+ ", found: " + desc.type.getSimpleName() + ")");
		}

		// check task list
		for (int i = 0; i < tasks.size(); i++) {
			AssetClassifier desc = tasks.get(i).assetDesc;
			if (desc.fileName.equals(fileName) && !desc.type.equals(type))
				throw new AERuntimeException(
						"Asset with name '" + fileName + "' already in task list, but has different type (expected: "
								+ type.getSimpleName() + ", found: " + desc.type.getSimpleName() + ")");
		}

		// check loaded assets
		Class otherType = assetTypes.get(fileName);
		if (otherType != null && !otherType.equals(type))
			throw new AERuntimeException(
					"Asset with name '" + fileName + "' already loaded, but has different type (expected: "
							+ type.getSimpleName() + ", found: " + otherType.getSimpleName() + ")");

		toLoad++;
		AssetClassifier assetDesc = new AssetClassifier(fileName, type, parameter);
		loadQueue.add(assetDesc);
		logger.debug("Queued: " + assetDesc);
	}

	/**
	 * Adds the given asset to the loading queue of the AssetManager.
	 * 
	 * @param desc the {@link AssetClassifier}
	 */
	public synchronized void load(AssetClassifier desc) {
		load(desc.fileName, desc.type, desc.params);
	}

	/**
	 * Updates the AssetManager for a single task. Returns if the current task is
	 * still being processed or there arew no tasks, otherwise it finishes the
	 * current task and starts the next task.
	 * 
	 * @return true if all loading is finished.
	 */
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
		} catch (Throwable t) {
			handleTaskError(t);
			return loadQueue.size() == 0;
		}
	}

	/**
	 * Returns true when all assets are loaded. Can be called from any thread but
	 * note {@link #update()} or related methods must be called to process tasks.
	 */
	public synchronized boolean isFinished() {
		return loadQueue.size() == 0 && tasks.size() == 0;
	}

	/** Blocks until all assets are loaded. */
	public void finishLoading() {
		logger.debug("Waiting for loading to complete...");
		while (!update())
			ThreadUtils.yield();
		logger.debug("Loading complete.");
	}

	/**
	 * Blocks until the specified asset is loaded.
	 * 
	 * @param assetDesc the AssetClassifier of the asset
	 */
	public <T> T finishLoadingAsset(AssetClassifier assetDesc) {
		return finishLoadingAsset(assetDesc.fileName);
	}

	/**
	 * Blocks until the specified asset is loaded.
	 * 
	 * @param fileName the file name (interpretation depends on {@link AssetLoader})
	 */
	public <T> T finishLoadingAsset(String fileName) {
		logger.debug("Waiting for asset to be loaded: " + fileName);
		while (true) {
			synchronized (this) {
				Class<T> type = assetTypes.get(fileName);
				if (type != null) {
					HashMap<String, AssetClassifier> assetsByType = assets.get(type);
					if (assetsByType != null) {
						AssetClassifier assetContainer = assetsByType.get(fileName);
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
			ThreadUtils.yield();
		}
	}

	synchronized void injectDependencies(String parentAssetFilename, ArrayList<AssetClassifier> dependendAssetDescs) {
		HashSet<String> injected = this.injected;
		for (AssetClassifier desc : dependendAssetDescs) {
			if (injected.contains(desc.fileName))
				continue; // Ignore subsequent dependencies if there are duplicates.
			injected.add(desc.fileName);
			injectDependency(parentAssetFilename, desc);
		}
		injected.clear();
	}

	private synchronized void injectDependency(String parentAssetFilename, AssetClassifier dependendAssetDesc) {
		// add the asset as a dependency of the parent asset
		ArrayList<String> dependencies = assetDependencies.get(parentAssetFilename);
		if (dependencies == null) {
			dependencies = new ArrayList<>();
			assetDependencies.put(parentAssetFilename, dependencies);
		}
		dependencies.add(dependendAssetDesc.fileName);

		// if the asset is already loaded, increase its reference count.
		if (isLoaded(dependendAssetDesc.fileName)) {
			logger.debug("Dependency already loaded: " + dependendAssetDesc);
			Class type = assetTypes.get(dependendAssetDesc.fileName);
			AssetClassifier assetRef = assets.get(type).get(dependendAssetDesc.fileName);
			assetRef.incRefCount();
			incrementRefCountedDependencies(dependendAssetDesc.fileName);
		}
		// else add a new task for the asset.
		else {
			logger.info("Loading dependency: " + dependendAssetDesc);
			addTask(dependendAssetDesc);
		}
	}

	/**
	 * Removes a task from the loadQueue and adds it to the task stack. If the asset
	 * is already loaded (which can happen if it was a dependency of a previously
	 * loaded asset) its reference count will be increased.
	 */
	private void nextTask() {
		AssetClassifier assetDesc = loadQueue.remove(0);

		// if the asset not meant to be reloaded and is already loaded, increase its
		// reference count
		if (isLoaded(assetDesc.fileName)) {
			logger.debug("Already loaded: " + assetDesc);
			Class type = assetTypes.get(assetDesc.fileName);
			AssetClassifier assetRef = assets.get(type).get(assetDesc.fileName);
			assetRef.incRefCount();
			incrementRefCountedDependencies(assetDesc.fileName);
			if (assetDesc.params != null && assetDesc.params.loadedCallback != null) {
				assetDesc.params.loadedCallback.finishedLoading(this, assetDesc.fileName, assetDesc.type);
			}
			loaded++;
		} else {
			// else add a new task for the asset.
			logger.info("Loading: " + assetDesc);
			addTask(assetDesc);
		}
	}

	/**
	 * Adds a {@link AssetLoadingTask} to the task stack for the given asset.
	 * 
	 * @param assetDesc
	 */
	private void addTask(AssetClassifier assetDesc) {
		AssetLoader loader = getLoader(assetDesc.type, assetDesc.fileName);
		if (loader == null)
			throw new AERuntimeException("No loader for type: " + assetDesc.type.getSimpleName());
		tasks.push(new AssetLoadingTask(this, assetDesc, loader, executor));
		peakTasks++;
	}

	/** Adds an asset to this AssetManager */
	protected <T> void addAsset(final String fileName, Class<T> type, T asset) {
		// add the asset to the filename lookup
		assetTypes.put(fileName, type);

		// add the asset to the type lookup
		HashMap<String, AssetClassifier> typeToAssets = assets.get(type);
		if (typeToAssets == null) {
			typeToAssets = new HashMap<String, AssetClassifier>();
			assets.put(type, typeToAssets);
		}
		typeToAssets.put(fileName, new AssetClassifier(asset));
	}

	/**
	 * Updates the current task on the top of the task stack.
	 * 
	 * @return true if the asset is loaded or the task was cancelled.
	 */
	private boolean updateTask() {
		AssetLoadingTask task = tasks.peek();

		boolean complete = true;
		try {
			complete = task.cancel || task.update();
		} catch (RuntimeException ex) {
			task.cancel = true;
			taskFailed(task.assetDesc, ex);
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

			addAsset(task.assetDesc.fileName, task.assetDesc.type, task.getAsset());

			// otherwise, if a listener was found in the parameter invoke it
			if (task.assetDesc.params != null && task.assetDesc.params.loadedCallback != null) {
				task.assetDesc.params.loadedCallback.finishedLoading(this, task.assetDesc.fileName,
						task.assetDesc.type);
			}

			return true;
		}
		return false;
	}

	/**
	 * Called when a task throws an exception during loading. The default
	 * implementation rethrows the exception. A subclass may supress the default
	 * implementation when loading assets where loading failure is recoverable.
	 */
	protected void taskFailed(AssetClassifier assetDesc, RuntimeException ex) {
		throw ex;
	}

	private void incrementRefCountedDependencies(String parent) {
		ArrayList<String> dependencies = assetDependencies.get(parent);
		if (dependencies == null)
			return;

		for (String dependency : dependencies) {
			Class type = assetTypes.get(dependency);
			AssetClassifier assetRef = assets.get(type).get(dependency);
			assetRef.incRefCount();
			incrementRefCountedDependencies(dependency);
		}
	}

	/**
	 * Handles a runtime/loading error in {@link #update()} by optionally invoking
	 * the {@link AssetErrorListener}.
	 * 
	 * @param t
	 */
	private void handleTaskError(Throwable t) {
		logger.error("Error loading asset.", t);

		if (tasks.isEmpty())
			throw new AERuntimeException(t);

		// pop the faulty task from the stack
		AssetLoadingTask task = tasks.pop();
		AssetClassifier assetDesc = task.assetDesc;

		// remove all dependencies
		if (task.dependenciesLoaded && task.dependencies != null) {
			for (AssetClassifier desc : task.dependencies) {
				unload(desc.fileName);
			}
		}

		// clear the rest of the stack
		tasks.clear();

		// inform the listener that something bad happened
		if (listener != null) {
			listener.error(assetDesc, t);
		} else {
			throw new AERuntimeException(t);
		}
	}

	/**
	 * Sets a new {@link AssetLoader} for the given type.
	 * 
	 * @param type   the type of the asset
	 * @param loader the loader
	 */
	public synchronized <T, P extends AssetLoaderParameters<T>> void setLoader(Class<T> type,
			AssetLoader<T, P> loader) {
		setLoader(type, null, loader);
	}

	/**
	 * Sets a new {@link AssetLoader} for the given type.
	 * 
	 * @param type   the type of the asset
	 * @param suffix the suffix the filename must have for this loader to be used or
	 *               null to specify the default loader.
	 * @param loader the loader
	 */
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

	/** @return the number of loaded assets */
	public synchronized int getLoadedAssets() {
		return assetTypes.size();
	}

	/** @return the number of currently queued assets */
	public synchronized int getQueuedAssets() {
		return loadQueue.size() + tasks.size();
	}

	/** @return the progress in percent of completion. */
	public synchronized float getProgress() {
		if (toLoad == 0)
			return 1;
		float fractionalLoaded = (float) loaded;
		if (peakTasks > 0) {
			fractionalLoaded += ((peakTasks - tasks.size()) / (float) peakTasks);
		}
		return Math.min(1, fractionalLoaded / (float) toLoad);
	}

	/**
	 * Sets an {@link AssetErrorListener} to be invoked in case loading an asset
	 * failed.
	 * 
	 * @param listener the listener or null
	 */
	public synchronized void setErrorListener(AssetErrorListener listener) {
		this.listener = listener;
	}

	/** Disposes all assets in the manager and stops all asynchronous loading. */
	@Override
	public synchronized void dispose() {
		logger.debug("Disposing.");
		clear();
		executor.dispose();
	}

	/** Clears and disposes all assets and the preloading queue. */
	public synchronized void clear() {
		loadQueue.clear();
		while (!update())
			;

		HashMap<String, Integer> dependencyCount = new HashMap<String, Integer>();
		while (assetTypes.size() > 0) {
			// for each asset, figure out how often it was referenced
			dependencyCount.clear();
			ArrayList<String> assets = (ArrayList<String>) assetTypes.keySet();
			for (String asset : assets) {
				dependencyCount.put(asset, 0);
			}

			for (String asset : assets) {
				ArrayList<String> dependencies = assetDependencies.get(asset);
				if (dependencies == null)
					continue;
				for (String dependency : dependencies) {
					int count = dependencyCount.get(dependency);
					count++;
					dependencyCount.put(dependency, count);
				}
			}

			// only dispose of assets that are root assets (not referenced)
			for (String asset : assets) {
				if (dependencyCount.get(asset) == 0) {
					unload(asset);
				}
			}
		}

		this.assets.clear();
		this.assetTypes.clear();
		this.assetDependencies.clear();
		this.loaded = 0;
		this.toLoad = 0;
		this.peakTasks = 0;
		this.loadQueue.clear();
		this.tasks.clear();
	}

	/** @return the {@link Logger} used by the {@link AssetManager} */
	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		logger = logger;
	}

	/**
	 * Returns the reference count of an asset.
	 * 
	 * @param fileName
	 */
	public synchronized int getReferenceCount(String fileName) {
		Class type = assetTypes.get(fileName);
		if (type == null)
			throw new AERuntimeException("Asset not loaded: " + fileName);
		return assets.get(type).get(fileName).getRefCount();
	}

	/**
	 * Sets the reference count of an asset.
	 * 
	 * @param fileName
	 */
	public synchronized void setReferenceCount(String fileName, int refCount) {
		Class type = assetTypes.get(fileName);
		if (type == null)
			throw new AERuntimeException("Asset not loaded: " + fileName);
		assets.get(type).get(fileName).setRefCount(refCount);
	}

	/**
	 * @return a string containing ref count and dependency information for all
	 *         assets.
	 */
	public synchronized String getDiagnostics() {
		StringBuilder sb = new StringBuilder(256);
		for (String fileName : assetTypes.keySet()) {
			if (sb.length() > 0)
				sb.append("\n");
			sb.append(fileName);
			sb.append(", ");

			Class type = assetTypes.get(fileName);
			AssetClassifier assetRef = assets.get(type).get(fileName);
			ArrayList<String> dependencies = assetDependencies.get(fileName);

			sb.append(type.getSimpleName());

			sb.append(", refs: ");
			sb.append(assetRef.getRefCount());

			if (dependencies != null) {
				sb.append(", deps: [");
				for (String dep : dependencies) {
					sb.append(dep);
					sb.append(",");
				}
				sb.append("]");
			}
		}
		return sb.toString();
	}

	/** @return the file names of all loaded assets. */
	public synchronized ArrayList<String> getAssetNames() {
		return (ArrayList<String>) assetTypes.keySet();
	}

	/**
	 * @return the dependencies of an asset or null if the asset has no
	 *         dependencies.
	 */
	public synchronized ArrayList<String> getDependencies(String fileName) {
		return assetDependencies.get(fileName);
	}

	/** @return the type of a loaded asset. */
	public synchronized Class getAssetType(String fileName) {
		return assetTypes.get(fileName);
	}

}
