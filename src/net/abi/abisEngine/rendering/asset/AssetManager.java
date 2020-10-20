/*******************************************************************************
 * Copyright 2020 Abinash Singh | ABI INC.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.abi.abisEngine.rendering.asset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;
import net.abi.abisEngine.rendering.asset.loaders.AssetLoader;
import net.abi.abisEngine.rendering.asset.loaders.ModelSceneLoader;
import net.abi.abisEngine.rendering.mesh.ModelScene;
import net.abi.abisEngine.util.Expendable;
import net.abi.abisEngine.util.ThreadUtils;
import net.abi.abisEngine.util.async.AsyncThreadDispatcher;
import net.abi.abisEngine.util.exceptions.AERuntimeException;

public class AssetManager implements Expendable {

	private static Logger logger = LogManager.getLogger(AssetManager.class);
	private AssetStore store;

	private final HashMap<Class, HashMap<String, AssetLoader>> loaders = new HashMap();
	private final ArrayList<AssetClassifier> loadQueue = new ArrayList<AssetClassifier>();
	private final AsyncThreadDispatcher executor;

	private final Stack<AssetLoadTask> tasks = new Stack();
	private int loaded = 0;
	private int toLoad = 0;
	private int peakTasks = 0;

	private String assetsDir = "./res/";

	private long glfw_handle;

	/** Creates a new AssetManager with all default loaders. */
	public AssetManager(long context) {
		this(null, true);
		this.glfw_handle = context;
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
		executor = new AsyncThreadDispatcher(1, "AssetManagerChildThread");
		store = new AssetStore();
	}

	public String getAssetsDir() {
		return assetsDir;
	}

	/**
	 * @param fileName the asset file name
	 * @return the asset
	 */
	public synchronized <T> T get(String fileName) {

		return store.get(fileName);
	}

	/**
	 * @param fileName the asset file name
	 * @param type     the asset type
	 * @return the asset
	 */
	public synchronized <T> T get(String fileName, Class<T> type) {
		return store.get(type, fileName);
	}

	/**
	 * @param type the asset type
	 * @return all the assets matching the specified type
	 */
	public synchronized <T> ArrayList<T> getAll(Class<T> type) {
		return store.getAll(type);
	}

	/**
	 * @param assetDescriptor the asset descriptor
	 * @return the asset
	 */
	public synchronized <T> T get(AssetClassifier<T> assetDescriptor) {
		return get(assetDescriptor.getFileName(), assetDescriptor.getType());
	}

	/**
	 * Returns true if an asset with the specified name is loading, queued to be
	 * loaded, or has been loaded.
	 */
	public synchronized boolean contains(String fileName) {
		if (tasks.size() > 0 && tasks.firstElement().assetDesc.getFileName().equals(fileName))
			return true;

		for (int i = 0; i < loadQueue.size(); i++)
			if (loadQueue.get(i).getFileName().equals(fileName))
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
			if (assetDesc.getType() == type && assetDesc.getFileName().equals(fileName))
				return true;
		}

		for (int i = 0; i < loadQueue.size(); i++) {
			AssetClassifier assetDesc = loadQueue.get(i);
			if (assetDesc.getType() == type && assetDesc.getFileName().equals(fileName))
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
			AssetLoadTask currAsset = tasks.firstElement();
			if (currAsset.assetDesc.getFileName().equals(fileName)) {
				currAsset.cancel = true;
				logger.info("Unload (from tasks): " + fileName);
				return;
			}
		}

		// check if it's in the queue
		int foundIndex = -1;
		for (int i = 0; i < loadQueue.size(); i++) {
			if (loadQueue.get(i).getFileName().equals(fileName)) {
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

		/*
		 * Remove the asset from the store.
		 */
		store.removeAsset(fileName);

	}

	/**
	 * @param asset the asset
	 * @return whether the asset is contained in this manager
	 */
	public synchronized <T> boolean containsAsset(String name) {
		return store.contains(name);
	}

//	/**
//	 * @param asset the asset
//	 * @return the filename of the asset or null
//	 */
//	public synchronized <T> String getAssetFileName(T asset) {
//		for (Class assetType : assets.keySet()) {
//			HashMap<String, AssetContainer> assetsByType = assets.get(assetType);
//			for (String fileName : assetsByType.keySet()) {
//				T otherAsset = (T) assetsByType.get(fileName).getObject(Object.class);
//				if (otherAsset == asset || asset.equals(otherAsset))
//					return fileName;
//			}
//		}
//		return null;
//	}

	/**
	 * @param assetDesc the AssetClassifier of the asset
	 * @return whether the asset is loaded
	 */
	public synchronized boolean isLoaded(AssetClassifier assetDesc) {
		return isLoaded(assetDesc.getFileName());
	}

	/**
	 * @param fileName the file name of the asset
	 * @return whether the asset is loaded
	 */
	public synchronized boolean isLoaded(String fileName) {
		return store.contains(fileName);
	}

	/**
	 * @param fileName the file name of the asset
	 * @return whether the asset is loaded
	 */
	public synchronized boolean isLoaded(String fileName, Class type) {
		return store.contains(type, fileName);
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
			if (desc.getFileName().equals(fileName) && !desc.getType().equals(type))
				throw new AERuntimeException("AssetI with name '" + fileName
						+ "' already in preload queue, but has different type (expected: " + type.getSimpleName()
						+ ", found: " + desc.getType().getSimpleName() + ")");
		}

		// check task list
		for (int i = 0; i < tasks.size(); i++) {
			AssetClassifier desc = tasks.get(i).assetDesc;
			if (desc.getFileName().equals(fileName) && !desc.getType().equals(type))
				throw new AERuntimeException(
						"AssetI with name '" + fileName + "' already in task list, but has different type (expected: "
								+ type.getSimpleName() + ", found: " + desc.getType().getSimpleName() + ")");
		}

		// check loaded assets
		if (store.contains(type, fileName)) {
			throw new AERuntimeException(
					"AssetI with name '" + fileName + "' already loaded : " + type.getSimpleName());
		}
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
		load(desc.getFileName(), desc.getType(), desc.getParameter());
	}

	/**
	 * Updates the AssetManager for a single task. Returns if the current task is
	 * still being processed or there are no tasks, otherwise it finishes the
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
				if (tasks.size() == 0) {
					executor.dispose();
					return true;
				}
			}
			return updateTask() && loadQueue.size() == 0 && tasks.size() == 0;
		} catch (Exception t) {
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
		return finishLoadingAsset(assetDesc.getFileName());
	}

	/**
	 * Blocks until the specified asset is loaded.
	 * 
	 * @param fileName the file name (interpretation depends on {@link AssetLoader})
	 */
	public synchronized <T> T finishLoadingAsset(String fileName) {
		logger.debug("Waiting for asset to be loaded: " + fileName);
		while (true) {
			synchronized (this) {
				Class<T> type = store.assetTypes.get(fileName);
				if (type != null) {
					HashMap<String, AssetContainer> assetsByType = store.get(type);
					if (assetsByType != null) {
						AssetContainer assetContainer = assetsByType.get(fileName);
						if (assetContainer != null) {
							T asset = assetContainer.getObject(type);
							if (asset != null) {
								logger.debug("AssetI loaded: " + fileName);
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

	/**
	 * Removes a task from the loadQueue and adds it to the task stack. If the asset
	 * is already loaded (which can happen if it was a dependency of a previously
	 * loaded asset) its reference count will be increased.
	 */
	private void nextTask() {
		AssetClassifier assetDesc = loadQueue.remove(0);

		// if the asset not meant to be reloaded and is already loaded, increase its
		// reference count
		if (isLoaded(assetDesc.getFileName())) {
			logger.debug("Already loaded: " + assetDesc);
			Class type = store.assetTypes.get(assetDesc.getFileName());
			AssetContainer assetRef = store.assets.get(type).get(assetDesc.getFileName());

//			if (assetRef.getObject(type) instanceof AssetI) {
//				logger.info("Adding Asset To The Store (new): " + assetDesc.getFileName());
//				((AssetI) assetRef.getObject(type)).incAndGetRef();
//			} else {
//				/*
//				 * Else if its not managed we increment the containers references.
//				 */
//				assetRef.incAndGetRef();
//			}

			assetRef.incRef();
			System.out.println(assetRef.getRefs());

			if (assetDesc.getParameter() != null && assetDesc.getParameter().loadedCallback != null) {
				assetDesc.getParameter().loadedCallback.finishedLoading(this, assetDesc.getFileName(), assetRef);
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
		AssetLoader loader = getLoader(assetDesc.getType(), assetDesc.getFileName());
		if (loader == null)
			throw new AERuntimeException("No loader for type: " + assetDesc.getType().getSimpleName());
		tasks.push(new AssetLoadTask(this, assetDesc, loader, executor));
		peakTasks++;
	}

	/** Adds an asset to this AssetManager */
	protected <T> AssetContainer addAsset(final String fileName, Class<T> type, T asset) {
//		// add the asset to the filename lookup
//		assetTypes.put(fileName, type);
//
//		// add the asset to the type lookup
//		HashMap<String, AssetContainer> typeToAssets = assets.get(type);
//		if (typeToAssets == null) {
//			typeToAssets = new HashMap<String, AssetContainer>();
//			assets.put(type, typeToAssets);
//		}
//		typeToAssets.put(fileName, new AssetContainer(asset));

		store.addAsset(type, fileName, asset);
		return store.getContainer(type, fileName);
	}

	/**
	 * Updates the current task on the top of the task stack.
	 * 
	 * @return true if the asset is loaded or the task was cancelled.
	 */
	private boolean updateTask() {
		AssetLoadTask task = tasks.peek();

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

			AssetContainer c = addAsset(task.assetDesc.getFileName(), task.assetDesc.getType(), task.getAsset());

			// otherwise, if a listener was found in the parameter invoke it
			if (task.assetDesc.getParameter() != null && task.assetDesc.getParameter().loadedCallback != null) {
				task.assetDesc.getParameter().loadedCallback.finishedLoading(this, task.assetDesc.getFileName(), c);
			}

			return true;
		}
		return false;
	}

	public long getGlfw_handle() {
		return glfw_handle;
	}

	/**
	 * Called when a task throws an exception during loading. The default
	 * implementation rethrows the exception. A subclass may supress the default
	 * implementation when loading assets where loading failure is recoverable.
	 */
	protected void taskFailed(AssetClassifier assetDesc, RuntimeException ex) {
		throw ex;
	}

	/**
	 * Handles a runtime/loading error in {@link #update()} by optionally invoking
	 * the {@link AssetErrorListener}.
	 * 
	 * @param t
	 */
	private void handleTaskError(Exception t) {
		logger.error("Error loading asset.", t);

		if (tasks.isEmpty())
			throw new AERuntimeException(t);

		// pop the faulty task from the stack
		AssetLoadTask task = tasks.pop();
		AssetClassifier assetDesc = task.assetDesc;

		// clear the rest of the stack
		tasks.clear();

		throw new AERuntimeException(t);

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
		return store.assetTypes.size();
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

		this.store.assets.clear();
		this.store.assetTypes.clear();
		this.loaded = 0;
		this.toLoad = 0;
		this.peakTasks = 0;
		this.loadQueue.clear();
		this.tasks.clear();
	}

	/**
	 * Returns the reference count of an asset.
	 * 
	 * @param fileName
	 */
	public synchronized int getReferenceCount(String fileName) {

		AssetContainer assetRef = store.get(fileName);

		return assetRef.getRefs();
	}

//	/**
//	 * Sets the reference count of an asset.
//	 * 
//	 * @param fileName
//	 */
//	public synchronized void setReferenceCount(String fileName, int refCount) {
//		Class type = assetTypes.get(fileName);
//		if (type == null)
//			throw new AERuntimeException("AssetI not loaded: " + fileName);
//		assets.get(type).get(fileName).setRefCount(refCount);
//	}

//	/**
//	 * @return a string containing ref count and dependency information for all
//	 *         assets.
//	 */
//	public synchronized String getDiagnostics() {
//		StringBuilder sb = new StringBuilder(256);
//		for (String fileName : assetTypes.keySet()) {
//			if (sb.length() > 0)
//				sb.append("\n");
//			sb.append(fileName);
//			sb.append(", ");
//
//			Class type = assetTypes.get(fileName);
//			AssetContainer assetRef = assets.get(type).get(fileName);
//
//			sb.append(type.getSimpleName());
//
//			sb.append(", refs: ");
//			sb.append(assetRef.getRefCount());
//
//		}
//		return sb.toString();
//	}

//	/** @return the file names of all loaded assets. */
//	public synchronized ArrayList<String> getAssetNames() {
//		return (ArrayList<String>) assetTypes.keySet();
//	}
//
//	/** @return the type of a loaded asset. */
//	public synchronized Class getAssetType(String fileName) {
//		return assetTypes.get(fileName);
//	}

}
