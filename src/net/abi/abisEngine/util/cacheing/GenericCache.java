package net.abi.abisEngine.util.cacheing;

import java.util.HashMap;

import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;

public class GenericCache<K, V> implements GenericCacheI<K, V> {

	private static Logger logger = LogManager.getLogger(GenericCache.class);

	private HashMap<K, V> cache;

	Class keyType;
	Class valueType;

	public GenericCache(Class k, Class v) {
		cache = new HashMap<K, V>();
		keyType = k;
		valueType = v;
	}

	@Override
	public V get(K key) {

		V val = cache.get(key);

		//printStatistics();

		return val;
	}

	@Override
	public void put(K key, V value) {
		
		if (!cache.containsKey(key)) {
			cache.put(key, value);
		}
		printStatistics();
	}

	@Override
	public boolean containsValue(V value) {
		return cache.containsValue(value);
	}

	@Override
	public boolean containsKey(K key) {
		return cache.containsKey(key);
	}

	@Override
	public int getSize() {
		return cache.size();
	}

	@Override
	public void printStatistics() {
		logger.debug("Cache Size: " + getSize() + " Cache Type: Key: " + keyType + " Value: " + valueType);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
