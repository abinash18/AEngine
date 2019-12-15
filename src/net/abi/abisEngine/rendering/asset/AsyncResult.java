package net.abi.abisEngine.rendering.asset;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AsyncResult<T> {

	private Future<T> future;

	public AsyncResult(Future<T> submit) {
		this.future = submit;
	}

	public boolean isDone() {
		return future.isDone();
	}

	public boolean isCancelled() {
		return future.isCancelled();
	}

	public boolean cancel(boolean mayInterruptIfRunning) {
		return future.cancel(mayInterruptIfRunning);
	}

	public T get() throws InterruptedException, ExecutionException, CancellationException {
		return future.get();
	}

}
