/*******************************************************************************
 * Copyright 2020 Abinash Singh | ABI INC.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.abi.abisEngine.util.async;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class AsyncThreadDispatcher {

	private ExecutorService dispatch;
	private int maxThreads = 1;
	private String threadNameSuffix;

	public AsyncThreadDispatcher(int maxThreads, String threadNameSuffix) {
		this.maxThreads = maxThreads;
		this.threadNameSuffix = threadNameSuffix;
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
			dispatch = Executors.newFixedThreadPool(maxThreads, new ThreadFactory() {
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
