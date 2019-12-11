package net.abi.abisEngine.rendering.asset;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class AsyncThreadDispatcher {

	ExecutorService dispatch;

	public AsyncThreadDispatcher(int maxThreads, final String threadNameSuffix) {

		this.dispatch = Executors.newFixedThreadPool(maxThreads, new ThreadFactory() {
			String threadSuffix = threadNameSuffix;
			int index = 0;

			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r, threadSuffix + "-" + index);
				index++;
				return t;
			}
		});
	}

	public <T> AsyncResult<T> submit(AsyncTask<T> task) {

		if (dispatch.isShutdown()) {
			// TODO: Throw AERuntime Exception.
		}

		return new AsyncResult<T>(dispatch.submit(new Callable<T>() {
			@Override
			public T call() throws Exception {
				return task.call();
			}
		}));
	}

	public void dispose() {

		if (dispatch.isShutdown()) {
			return;
		}

		dispatch.shutdown();

		// TODO: Log that dispatch is being shut down.

		try {
			dispatch.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO: handle exception
		}

		// TODO: Log that dispatch has been shut down.

	}

}
