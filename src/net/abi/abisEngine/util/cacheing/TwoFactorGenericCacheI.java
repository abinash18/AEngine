package net.abi.abisEngine.util.cacheing;

import java.util.HashMap;

import net.abi.abisEngine.util.Expendable;

public interface TwoFactorGenericCacheI<F, K, V> extends Expendable {
	public V get(F factorOne, K factorTwo);

	public HashMap<K, V> get(F factorOne);

	public V put(F factorOne, K factorTwo, V value);

	public HashMap<K, V> put(F factorOne);

	public boolean contains(F factor);

	public boolean contains(F factor, K key);

	public boolean contains(F factor, K key, V value);

	public void remove(F factor, K key);

	public int getSize(F factor);

	public int getSize();

	public void printStatistics();

}
