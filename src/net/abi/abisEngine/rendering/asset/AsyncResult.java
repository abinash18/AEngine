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

	public T get() {
		try {
			return future.get();
		} catch (CancellationException e) {
			// TODO: Log out that it was canceled.
			return null;
		} catch (ExecutionException e) {
			// TODO: Throw AERuntime Exception.
		} catch (InterruptedException e) {
			// TODO: Throw AERuntime Exception.
		}
		return null;
	}

}
