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

import com.cmt.singularity.Configuration;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Benjamin Schiller
 */
public class StandardTaskGroup implements TaskGroup, Comparable
{

	@SuppressWarnings("unused")
	private final static Logger log = LogManager.getLogger(StandardTasks.class.getName());

	protected class Worker extends Thread
	{

		protected final UUID id;

		protected TaskBarrier terminationBarrier;

		protected boolean ended;

		public Worker(String name, boolean daemon)
		{
			super();

			id = UUID.randomUUID();
			setName(name + " " + id);
			setDaemon(daemon);
		}

		public void terminate(TaskBarrier terminationBarrier)
		{
			log.info("Terminating", getName());

			this.terminationBarrier = terminationBarrier;
		}

		@Override
		public void run()
		{
			log.info("Starting", getName());

			while (terminationBarrier == null) {

				Task task = null;
				try {
					task = queue.poll(100, TimeUnit.MILLISECONDS);
				} catch (InterruptedException ex) {
					// do nothing
				}

				if (task != null) {
					runningWorkerTasks.incrementAndGet();

					try {
						task.execute();
					} catch (Throwable ex) {
						// @todo what to do with those ex?
						log.error(ex);
					}

					runningWorkerTasks.decrementAndGet();

					if (workerMonitor != null) {
						synchronized (workerMonitor) {
							workerMonitor.notifyAll();
						}
					}
				}
			}

			ended = true;

			// Check if all workers are ended -> set global ended to true
			boolean allEnded = true;
			if (workers != null) {
				for (Worker worker : workers) {
					if (!worker.ended) {
						allEnded = false;
					}
				}
			}

			if (allEnded) {
				StandardTaskGroup.this.ended = true;
			}

			log.info("Exiting", getName());

			terminationBarrier.arrive();
		}
	}

	protected final Configuration configuration;
	protected final String name;
	protected final Worker[] workers;
	protected final BlockingQueue<Task> queue;
	protected final AtomicInteger runningWorkerTasks;
	protected final Object workerMonitor;
	protected boolean ending;
	protected boolean ended;

	@SuppressWarnings("CallToThreadStartDuringObjectConstruction")
	public StandardTaskGroup(Configuration configuration, String name, int poolSize, int queueSize, boolean daemon)
	{
		this.configuration = configuration;

		this.name = name;

		// Assign an object to allow waiting
		workerMonitor = "";

		runningWorkerTasks = new AtomicInteger();

		queue = new ArrayBlockingQueue(queueSize, false);

		workers = new Worker[poolSize];

		for (int i = 0; i < poolSize; ++i) {

			workers[i] = new Worker(name, daemon);
			workers[i].start();
		}
	}

	@Override
	public Task asTask(Runnable runnable)
	{
		return new RunnableTask(runnable);
	}

	@Override
	public Task asTask(Callable callable)
	{
		return new CallableTask(callable);
	}

	@Override
	public TaskBarrier parallelBefore(Task... tasks)
	{
		TaskBarrier barrier = new StandardTaskBarrier(tasks.length);

		for (Task task : tasks) {
			queue.add(() -> {

				String taskLog = task.getClass().getName() + ".execute";

				log.debug(taskLog + ":enter");
				log.start(taskLog);

				task.execute();

				barrier.arrive();

				log.stopDebug(taskLog);
				log.debug(taskLog + ":exit");
			});
		}

		return barrier;
	}

	@Override
	public TaskGroup parallelAfter(TaskBarrier barrier, Task... tasks)
	{
		for (Task task : tasks) {
			queue.add(() -> {
				String taskLog = task.getClass().getName() + ".execute";

				log.debug(taskLog + ":enter");
				log.start(taskLog);

				barrier.await();

				task.execute();

				log.stopDebug(taskLog);
				log.debug(taskLog + ":exit");
			});
		}

		return this;
	}

	@Override
	public TaskGroup parallel(Task... tasks)
	{
		for (Task task : tasks) {
			queue.add(() -> {
				String taskLog = task.getClass().getName() + ".execute";

				log.debug(taskLog + ":enter");
				log.start(taskLog);

				task.execute();

				log.stopDebug(taskLog);
				log.debug(taskLog + ":exit");
			});
		}

		return this;
	}

	/**
	 * Runs the given tasks sequential in order. It uses the SequentialTask for it.
	 *
	 * @param tasks
	 * @return
	 */
	@Override
	public TaskGroup sequential(Task... tasks)
	{
		queue.add(new SequentialTask(this, tasks));

		return this;
	}

	/**
	 * Makes sure the queue is empty and all workers have processed their tasks. Uses suspended waiting
	 *
	 * @return
	 */
	@Override
	public TaskGroup join()
	{
		log.debug("join:enter");

		synchronized (workerMonitor) {
			while (!queue.isEmpty() || runningWorkerTasks.get() > 0) {
				try {
					workerMonitor.wait();
				} catch (InterruptedException ex) {
					log.error(ex);
					return this;
				}
			}
		}

		log.debug("join:exit");

		return this;
	}

	@Override
	public TaskBarrier endGracefully()
	{
		log.debug("endGracefully:enter");

		ending = true;

		TaskBarrier terminationBarrier = new StandardTaskBarrier(workers.length);

		for (Worker worker : workers) {
			worker.terminate(terminationBarrier);
		}

		log.debug("endGracefully:exit");

		return terminationBarrier;
	}

	@Override
	public int compareTo(Object o)
	{
		if (o instanceof TaskGroup taskGroup) {
			return name.compareTo(taskGroup.getName());
		}

		return -1;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public boolean isEnding()
	{
		return ending;
	}

	@Override
	public boolean isEnded()
	{
		return ended;
	}
}
