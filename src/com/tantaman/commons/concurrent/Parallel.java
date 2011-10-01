/*
*   Copyright 2011 Tantaman LLC
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*/

package com.tantaman.commons.concurrent;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Parallel {
	private static final int NUM_CORES = Runtime.getRuntime().availableProcessors();
	
	// TODO: replace with custom cached thread pool.
	private static final ExecutorService forPool = Executors.newFixedThreadPool(NUM_CORES * 2, new NamedThreadFactory("Parallel.For"));
	
	public static <T> void For(final Iterable<T> pElements, final Operation<T> pOperation) {
		ExecutorService executor = forPool;
		List<Future<?>> futures = new LinkedList<Future<?>>();
		for (final T element : pElements) {
			Future<?> future = executor.submit(new Runnable() {
				@Override
				public void run() {
					pOperation.perform(element);
				}
			});
			
			futures.add(future);
		}
		
		for (Future<?> f : futures) {
			try {
				f.get();
			} catch (InterruptedException e) {
			} catch (ExecutionException e) {
			}
		}
		executor.shutdown();
	}
	
	public static interface Operation<T> {
		public void perform(T pParameter);
	}
}
