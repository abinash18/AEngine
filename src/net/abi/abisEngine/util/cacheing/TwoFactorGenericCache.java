package net.abi.abisEngine.util.cacheing;

import java.util.HashMap;

import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;
import net.abi.abisEngine.rendering.asset.AssetI;

public class TwoFactorGenericCache<F, K, V> implements TwoFactorGenericCacheI<F, K, V> {

	private static Logger logger = LogManager.getLogger(TwoFactorGenericCache.class);
	private HashMap<F, HashMap<K, V>> cache;

	private Class factorType, keyType, valueType;

	public TwoFactorGenericCache(Class factor, Class factorTwo, Class value) {
		this.factorType = factor;
		this.keyType = factorTwo;
		this.valueType = value;

		this.cache = new HashMap<F, HashMap<K, V>>();
	}

	@Override
	public V get(F factorOne, K factorTwo) {
		HashMap<K, V> resource;
		V value;

		if ((resource = cache.get(factorOne)) == null) {
			return null;
		} else {
			value = resource.get(factorTwo);
		}

		return value;
	}

	@Override
	public V put(F factorOne, K factorTwo, V value) {
		HashMap<K, V> resource;

		if ((resource = cache.get(factorOne)) == null) {
			resource = new HashMap<K, V>();
			cache.put(factorOne, resource);
		}

		V _resource;

		if ((_resource = resource.get(factorTwo)) == null) {
			resource.put(factorTwo, value);
		} else {

			if (_resource instanceof AssetI) {
				((AssetI) _resource).incRef();
			}
		}

		//printStatistics();
		
		return _resource;
	}

	@Override
	public HashMap<K, V> put(F factorOne) {
		HashMap<K, V> resource = new HashMap<K, V>();

		if ((resource = cache.get(factorOne)) == null) {
			resource = new HashMap<K, V>();
			cache.put(factorOne, resource);
		}

		return resource;
	}

	@Override
	public boolean contains(F factor) {
		return cache.containsKey(factor);
	}

	@Override
	public boolean contains(F factor, K key) {
		boolean result = contains(factor);
		if (result) {
			result = cache.get(factor).containsKey(key);
		}
		return result;
	}

	@Override
	public boolean contains(F factor, K key, V value) {

		boolean result = contains(factor, key);

		if (result) {
			cache.get(factor).containsValue(value);
		}

		return false;
	}

	@Override
	public int getSize(F factor) {
		return cache.get(factor).size();
	}

	@Override
	public int getSize() {
		return cache.size();
	}

	@Override
	public void printStatistics() {
		String o = "Cache Size: " + getSize() + " | Cache Factor One Type: " + factorType + " | Cache Factor Two Type: "
				+ keyType + " | Cache Value Type: " + valueType + " | Factor One Size : " + getSize();
		logger.debug(o);
	}

	@Override
	public HashMap<K, V> get(F factorOne) {
		return cache.get(factorOne);
	}

	@Override
	public void dispose() {
	}

	@Override
	public void remove(F factor, K key) {
		if (contains(factor, key)) {
			V _v;
			if ((_v = get(factor, key)) instanceof AssetI) {
				((AssetI) _v).decRef();
			}
			cache.get(factor).remove(key);
		}
	}

}
