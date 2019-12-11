package net.abi.abisEngine.rendering.asset;
import java.util.concurrent.ExecutionException;

interface AsyncTask<T> {

	public T call() throws ExecutionException;

}