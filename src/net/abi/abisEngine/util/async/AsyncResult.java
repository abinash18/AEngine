package net.abi.abisEngine.util.async;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import net.abi.abisEngine.util.AERuntimeException;

public class AsyncResult<T> {
	protected Future<T> future;

	public AsyncResult(Future<T> f) {
		this.future = f;
	}

	public boolean isDone() {
		return future.isDone();
	}

	public T get() {
		try {
			return future.get();
		} catch (InterruptedException e) {
			return null;
		} catch (ExecutionException e) {
			throw new AERuntimeException(e.getCause());
		}
	}
}