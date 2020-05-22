package net.abi.abisEngine.util.cacheing;

import net.abi.abisEngine.util.Expendable;

public interface GenericCacheI<K, V> extends Expendable {

	public V get(K key);
	
	public void put(K key, V value);
	
	public boolean containsValue(V value);
	
	public int getSize();

	public boolean containsKey(K key);
	
	public void printStatistics();
	
}
