package net.abi.abisEngine.rendering.asset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.abi.abisEngine.util.AERuntimeException;

public class AssetStore {

	/**
	 * The type of asset mapped to the list of suffix' mapped to their associate
	 * reference counted containers.
	 */
	public Map<Class, ConcurrentHashMap<String, AssetContainer>> assets;

	public HashMap<String, Class> assetTypes;

	/**
	 * 
	 */
	public AssetStore() {
		assets = new ConcurrentHashMap<Class, ConcurrentHashMap<String, AssetContainer>>();
		assetTypes = new HashMap<String, Class>();
	}

	/**
	 * This adds the asset to the static list. It also protects from duplicates
	 * since there can be only one type. And when loading ModelScenes There can be
	 * many assets there.
	 * 
	 * @param          <T>
	 * 
	 * @param type
	 * @param fileName
	 * @param asset
	 */
	public synchronized <T> void addAsset(Class type, String name, T asset) {
		ConcurrentHashMap<String, AssetContainer> tempMap = null;
		/*
		 * If there is an entry that is mapped to the type we set temp map to that
		 * entry.
		 */
		if (assets.containsKey(type)) {
			tempMap = assets.get(type);
		} else {
			/* Else we make a new entry and add a new Map to that and with the asset. */
			tempMap = new ConcurrentHashMap<String, AssetContainer>();
			tempMap.put(name, new AssetContainer(asset));
			assets.put(type, tempMap);
			return;
		}
		/**
		 * We reach this statement only if there is a suffix already registered in the
		 * assets store.
		 */
		if (tempMap.containsKey(name)) {
			/**
			 * Add a reference to it.
			 */
			tempMap.get(name).incRefCount();
		}
	}

	public synchronized <T> T getAsset(String fileName) {
		Class<T> type = assetTypes.get(fileName);
		if (type == null)
			throw new AERuntimeException("Asset not loaded: " + fileName);
		ConcurrentHashMap<String, AssetContainer> assetsByType = assets.get(type);
		if (assetsByType == null)
			throw new AERuntimeException("Asset not loaded: " + fileName);
		AssetContainer assetContainer = assetsByType.get(fileName);
		if (assetContainer == null)
			throw new AERuntimeException("Asset not loaded: " + fileName);
		T asset = assetContainer.getObject(type);
		if (asset == null)
			throw new AERuntimeException("Asset not loaded: " + fileName);

		return asset;
	}

	/**
	 * Gets the asset from the store using the name, either the file's name or the
	 * asset name defined in the file. i.e. Model uses the ModelSceneLoader wich
	 * loads all assets from the file which there can be multiple of. So the loader
	 * assigns the names as keys to find them.
	 * 
	 * @param           <T>
	 * 
	 * @param type
	 * @param assetName
	 * @return
	 */
	public synchronized <T> T getAsset(Class<T> type, String assetName) {

		AssetContainer _assCont = null;

		ConcurrentHashMap<String, AssetContainer> _assets = assets.get(type);

		if (_assets == null) {
			throw new AERuntimeException("Asset Not Loaded: " + assetName);
		}

		_assCont = _assets.get(assetName);

		if (_assCont == null) {
			throw new AERuntimeException("Asset Not Loaded: " + assetName);
		}

		T ass = _assCont.getObject(type);

		if (ass == null) {
			throw new AERuntimeException("Asset Not Loaded: " + assetName);
		}

		return ass;
	}

	public synchronized ConcurrentHashMap<String, AssetContainer> get(Class type) {

		ConcurrentHashMap<String, AssetContainer> _assets = assets.get(type);

		if (_assets == null) {
			throw new AERuntimeException("Asset Type Not Loaded: " + type);
		}

		return _assets;
	}

	public synchronized <T> ArrayList<T> getAllAssets(Class<T> type) {

		ConcurrentHashMap<String, AssetContainer> _assets = assets.get(type);

		if (_assets == null) {
			throw new AERuntimeException("Asset Type Dosnt Exist.");
		}

		ArrayList<T> _assLis = new ArrayList<T>();

		for (ConcurrentHashMap.Entry<String, AssetContainer> _ass : _assets.entrySet()) {
			_assLis.add((T) _ass.getValue().getObject(type));
		}

		return _assLis;
	}

	public synchronized boolean contains(Class type, String assetName) {

		ConcurrentHashMap<String, AssetContainer> _assets = assets.get(type);

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

	public synchronized <T> void removeAsset(Class type, String suffix, T asset) {
		ConcurrentHashMap<String, AssetContainer> tempMap = assets.get(type);
		/**
		 * We reach this statement only if there is a suffix already registered in the
		 * assets store.
		 */
		if (tempMap.containsKey(suffix)) {
			AssetContainer _ac = tempMap.get(suffix);
			/**
			 * It either destroys it or removes a reference from it.
			 */
			_ac.decRefCount();

			if (_ac.getRefCount() <= 0) {
				// _ac.dispose();
			}

		}
	}

	public HashMap<String, Class> getAssetTypes() {
		return assetTypes;
	}

}
