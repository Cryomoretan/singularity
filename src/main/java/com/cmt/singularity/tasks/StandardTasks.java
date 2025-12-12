// <editor-fold desc="The MIT License" defaultstate="collapsed">
/*
 * The MIT License
 *
 * Copyright 2025 Cryomoretan GmbH.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
//</editor-fold>
package com.cmt.singularity.tasks;

import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Benjamin Schiller
 */
public class StandardTasks implements Tasks, TaskContext
{

	@SuppressWarnings("unused")
	private final static Logger log = LogManager.getLogger(StandardTasks.class.getName());

	public final static int DEFAULT_POOL_SIZE = 8;
	public final static int DEFAULT_QUEUE_SIZE = 100;

	protected class Worker extends Thread
	{

		protected final UUID id;

		public Worker()
		{
			super();

			id = UUID.randomUUID();
			setName("Worker " + id);
		}

		@Override
		public void run()
		{
			log.debug("Starting " + getName());

			while (true) {

				try {
					Task task = queue.poll(100, TimeUnit.MILLISECONDS);

					task.execute(StandardTasks.this);

				} catch (InterruptedException ex) {

					log.error(ex);

					return;
				}
			}
		}
	}

	protected final Worker[] workers;
	protected final BlockingQueue<Task> queue;

	public StandardTasks()
	{
		this(DEFAULT_POOL_SIZE, DEFAULT_QUEUE_SIZE);
	}

	@SuppressWarnings("CallToThreadStartDuringObjectConstruction")
	public StandardTasks(int poolSize, int queueSize)
	{
		queue = new ArrayBlockingQueue(queueSize, false);

		workers = new Worker[poolSize];

		for (int i = 0; i < poolSize; ++i) {

			workers[i] = new Worker();
			workers[i].start();
		}
	}

	@Override
	public Task asTask(Runnable runnable)
	{
		return new RunnableTask(runnable);
	}

	@Override
	public Tasks parallel(Task... tasks)
	{
		queue.addAll(Arrays.asList(tasks));

		return this;
	}

	@Override
	public Tasks sequential(Task... tasks)
	{
		queue.add(new SequentialTask(tasks));

		return this;
	}

	@Override
	public Tasks join()
	{
		synchronized (Thread.currentThread()) {
			while (!queue.isEmpty()) {
				try {
					Thread.currentThread().wait(10);
				} catch (InterruptedException ex) {
					log.error(ex);
					return this;
				}
			}
		}

		return this;
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	// "Getters/Setters" </editor-fold>
}
