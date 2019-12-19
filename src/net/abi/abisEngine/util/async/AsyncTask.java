package net.abi.abisEngine.util.async;

import java.util.concurrent.ExecutionException;

public interface AsyncTask<T> {

	public T call() throws ExecutionException;

}