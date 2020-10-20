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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;
import net.abi.abisEngine.util.exceptions.AERuntimeException;

public class AssetStore {

	private static Logger logger = LogManager.getLogger(AssetStore.class);

	/**
	 * The type of asset mapped to the list of suffix' mapped to their associate
	 * reference counted containers.
	 */
	public Map<Class, HashMap<String, AssetContainer>> assets;

	public HashMap<String, Class> assetTypes;

	/**
	 * 
	 */
	public AssetStore() {
		assets = new HashMap<Class, HashMap<String, AssetContainer>>();
		assetTypes = new HashMap<String, Class>();
	}

	/**
	 * This adds the asset to the static list. It also protects from duplicates
	 * since there can be only one type. And when loading ModelScenes There can be
	 * many assets there.
	 * 
	 * @param <T>
	 * 
	 * @param type
	 * @param fileName
	 * @param asset
	 */
	public synchronized <T> void addAsset(Class type, String name, T asset) {
		HashMap<String, AssetContainer> tempMap = assets.get(type);

		/*
		 * If the type doesn't exist then we add it to the map this saves us code
		 * repetition later on in this function.
		 */
		if (!assetTypes.containsKey(name)) {
			assetTypes.put(name, type);
		}

		/*
		 * Default value for the container that initializes it later in the check it
		 * will be set.
		 */
		AssetContainer _asset = null;

		/*
		 * If the type doesn't exist in the map then we make one and add it to the map.
		 */
		if (tempMap == null) {
			tempMap = new HashMap<String, AssetContainer>();
			tempMap.put(name, (_asset = new AssetContainer(asset)));
			assets.put(type, tempMap);
		} else {
			/*
			 * If the map dose exist we get the container and if the container doesn't exist
			 * we make one and add it to the tempMap.
			 */
			if ((_asset = tempMap.get(name)) == null) {
				tempMap.put(name, new AssetContainer(asset));
			} else {
				/*
				 * If the asset exists then we just add a reference.
				 */
				_asset.incRef();

//				if (_asset.getObject(type) instanceof AssetI) {
//					logger.info("Adding Asset To The Store (new): " + name);
//					((AssetI) _asset.getObject(type)).incAndGetRef();
//				} else {
//					/*
//					 * Else if its not managed we increment the containers references.
//					 */
//					_asset.incAndGetRef();
//				}
			}
		}
	}

	public synchronized <T> T get(String fileName) {
		Class<T> type = assetTypes.get(fileName);
		if (type == null)
			throw new AERuntimeException("AssetI not loaded: " + fileName);
		HashMap<String, AssetContainer> assetsByType = assets.get(type);
		if (assetsByType == null)
			throw new AERuntimeException("AssetI not loaded: " + fileName);
		AssetContainer assetContainer = assetsByType.get(fileName);
		if (assetContainer == null)
			throw new AERuntimeException("AssetI not loaded: " + fileName);
		T asset = assetContainer.getObject(type);
		if (asset == null)
			throw new AERuntimeException("AssetI not loaded: " + fileName);

		return asset;
	}

	public synchronized <T> Class<T> getType(String fileName) {
		Class<T> type = assetTypes.get(fileName);
		if (type == null)
			throw new AERuntimeException("AssetI not loaded: " + fileName);

		return type;
	}

	/**
	 * Gets the asset from the store using the name, either the file's name or the
	 * asset name defined in the file. i.e. Model uses the ModelSceneLoader wich
	 * loads all assets from the file which there can be multiple of. So the loader
	 * assigns the names as keys to find them.
	 * 
	 * @param <T>
	 * 
	 * @param type
	 * @param assetName
	 * @return
	 */
	public synchronized <T> T get(Class<T> type, String assetName) {

		AssetContainer _assCont = null;

		HashMap<String, AssetContainer> _assets = assets.get(type);

		if (_assets == null) {
			throw new AERuntimeException("AssetI Not Loaded: " + assetName);
		}

		_assCont = _assets.get(assetName);

		if (_assCont == null) {
			throw new AERuntimeException("AssetI Not Loaded: " + assetName);
		}

		T ass = _assCont.getObject(type);

		if (ass == null) {
			throw new AERuntimeException("AssetI Not Loaded: " + assetName);
		}

		return ass;
	}

	public synchronized HashMap<String, AssetContainer> get(Class type) {

		HashMap<String, AssetContainer> _assets = assets.get(type);

		if (_assets == null) {
			throw new AERuntimeException("AssetI Type Not Loaded: " + type);
		}

		return _assets;
	}

	public synchronized <T> ArrayList<T> getAll(Class<T> type) {

		HashMap<String, AssetContainer> _assets = assets.get(type);

		if (_assets == null) {
			throw new AERuntimeException("AssetI Type Dosnt Exist.");
		}

		ArrayList<T> _assLis = new ArrayList<T>();

		for (ConcurrentHashMap.Entry<String, AssetContainer> _ass : _assets.entrySet()) {
			_assLis.add((T) _ass.getValue().getObject(type));
		}

		return _assLis;
	}

	public synchronized boolean contains(String assetName) {
		return contains(assetTypes.get(assetName), assetName);
	}

	public boolean contains(Class type, String assetName) {

		HashMap<String, AssetContainer> _assets = assets.get(type);

		/*
		 * If the type has not been loaded.
		 */
		if (_assets == null) {
			return false;
		}

		/*
		 * If the asset dose not exist in the type.
		 */
		if (_assets.get(assetName) == null) {
			return false;
		}

		return true;
	}

	public synchronized <T> void removeAsset(String assetName) {
		removeAsset(assetTypes.get(assetName), assetName);
	}

	public synchronized <T> void removeAsset(Class type, String assetName) {
		HashMap<String, AssetContainer> tempMap = assets.get(type);

		if (tempMap == null) {
			throw new AERuntimeException("AssetI Not Found : " + assetName + " : " + type.getSimpleName());
		}

		AssetContainer assetRef = assets.get(type).get(assetName);

		/**
		 * If the asset has management support then we cast it to the management
		 * interface and decrement its references.
		 */

		if (assetRef.decAndGetRef() <= 0) {
			tempMap.remove(assetName);
			assetTypes.remove(assetName);
		} else {
			logger.info("Unload (decrement): " + assetName);
		}

//		if (assetRef.getObject(type) instanceof AssetI) {
//			logger.info("Unload (dispose): " + assetName);
//			if (((AssetI) assetRef.getObject(type)).decAndGetRef() <= 0) {
//				tempMap.remove(assetName);
//				assetTypes.remove(assetName);
//			} else {
//				logger.info("Unload (decrement): " + assetName);
//			}
//		} else {
//			logger.info("Unload (dispose): " + assetName);
//			if (assetRef.decAndGetRef() <= 0) {
//				tempMap.remove(assetName);
//				assetTypes.remove(assetName);
//			} else {
//				logger.info("Unload (decrement): " + assetName);
//			}
//		}
	}

	public HashMap<String, Class> getAssetTypes() {
		return assetTypes;
	}

	public <T> AssetContainer getContainer(Class<T> type, String assetName) {
		AssetContainer _assCont = null;

		HashMap<String, AssetContainer> _assets = assets.get(type);

		if (_assets == null) {
			throw new AERuntimeException("AssetI Not Loaded: " + assetName);
		}

		_assCont = _assets.get(assetName);

		if (_assCont == null) {
			throw new AERuntimeException("AssetI Not Loaded: " + assetName);
		}

		return _assCont;
	}

}
