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
import java.util.concurrent.ExecutionException;

import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;
import net.abi.abisEngine.rendering.asset.loaders.AssetLoader;
import net.abi.abisEngine.rendering.asset.loaders.AsyncAssetLoader;
import net.abi.abisEngine.rendering.asset.loaders.SyncAssetLoader;
import net.abi.abisEngine.util.async.AsyncResult;
import net.abi.abisEngine.util.async.AsyncTask;
import net.abi.abisEngine.util.async.AsyncThreadDispatcher;
import net.abi.abisEngine.util.exceptions.AERuntimeException;

public class AssetLoadTask implements AsyncTask<Void> {
	private final Logger logger = LogManager.getLogger(AssetLoadTask.class);
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

	public Void call() throws ExecutionException {
		AsyncAssetLoader asyncLoader = (AsyncAssetLoader) loader;
		asyncLoader.loadAsync(assetDesc.getFileName(), manager);
		asyncDone = true;
		return null;
	}

	public boolean update() {
		ticks++;
		if (loader instanceof SyncAssetLoader) {
			handleSyncLoader();
		} else {
			handleAsyncLoader();
		}
		return asset != null;
	}

	private void handleSyncLoader() {
		SyncAssetLoader syncLoader = (SyncAssetLoader) loader;

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
}
