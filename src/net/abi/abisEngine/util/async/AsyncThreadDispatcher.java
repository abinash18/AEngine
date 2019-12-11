package net.abi.abisEngine.util.async;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import net.abi.abisEngine.util.AERuntimeException;
import net.abi.abisEngine.util.Expendable;

public class AsyncThreadDispatcher implements Expendable {

	protected ExecutorService dispatch;

	public AsyncThreadDispatcher(int maxThreads, String name) {
		dispatch = Executors.newFixedThreadPool(maxThreads, new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r, name);
				thread.setDaemon(true);
				return thread;
			}
		});
	}

	public <T> AsyncResult<T> submit(AsyncTask<T> task) {

		if (dispatch.isShutdown()) {
			throw new AERuntimeException("Executor Shutdown Already.");
		}
		return new AsyncResult<T>(dispatch.submit(new Callable<T>() {
			@Override
			public T call() throws Exception {
				return task.execute();
			}
		}));
	}

	@Override
	public void dispose() {
		dispatch.shutdown();
		try {
			dispatch.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			throw new AERuntimeException("Dispatch Cannot Be Shut Down.", e);
		}
	}

	public interface AsyncTask<T> {
		public T execute() throws ExecutionException;
	}

}
