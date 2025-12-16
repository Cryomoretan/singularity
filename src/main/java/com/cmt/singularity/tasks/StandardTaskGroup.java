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
import com.cmt.singularity.assertion.Assert;
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

	private final static Logger log = LogManager.getLogger(StandardTaskGroup.class.getName());

	private final static Assert assertion = Assert.getAssert(StandardTaskGroup.class.getName());

	public static class StandardTaskWrapperTask implements Task
	{

		protected final Task task;
		protected final TaskBarrier await;
		protected final TaskBarrier arrive;
		protected final String taskLog;
		protected final boolean logTasks;

		public StandardTaskWrapperTask(Task task, boolean logTasks)
		{
			this(task, null, null, logTasks);
		}

		public StandardTaskWrapperTask(Task task, TaskBarrier await, TaskBarrier arrive, boolean logTasks)
		{
			assertion.assertNotNull(task, "task != null");

			this.task = task;
			this.await = await;
			this.arrive = arrive;
			this.logTasks = logTasks;
			taskLog = task.getClass().getName() + ".execute";
		}

		@Override
		public void execute()
		{
			if (logTasks) {
				log.debug(taskLog + ":enter");
				log.start(taskLog);
			}

			if (await != null) {
				await.arrive();
			}

			task.execute();

			if (arrive != null) {
				arrive.arrive();
			}

			if (logTasks) {
				log.stopDebug(taskLog);
				log.debug(taskLog + ":exit");
			}
		}
	}

	protected final class Worker extends Thread
	{

		protected final UUID id;

		protected TaskBarrier terminationBarrier;

		protected boolean ended;

		public Worker(String name, boolean daemon)
		{
			super();

			assertion.assertNotNull(name, "name != null");

			id = UUID.randomUUID();
			setName(name + " " + id);
			setDaemon(daemon);
		}

		public void terminate(TaskBarrier terminationBarrier)
		{
			assertion.assertNotNull(terminationBarrier, "terminationBarrier != null");

			log.trace("Terminating", getName());

			this.terminationBarrier = terminationBarrier;
		}

		@Override
		public void run()
		{
			log.trace("Starting", getName());

			while (terminationBarrier == null) {

				Task task = null;
				try {
					// @todo What is the right value here for poll time out?
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

			log.trace("Exiting", getName());

			terminationBarrier.arrive();
		}
	}

	protected final String name;
	protected final Worker[] workers;
	protected final BlockingQueue<Task> queue;
	protected final AtomicInteger runningWorkerTasks;
	protected final Object workerMonitor;
	protected final boolean logTasks;
	protected boolean ending;
	protected boolean ended;

	@SuppressWarnings("CallToThreadStartDuringObjectConstruction")
	public StandardTaskGroup(Configuration configuration, String name, int poolSize, int queueSize, boolean daemon)
	{
		assertion.assertNotNull(configuration, "configuration != null");
		assertion.assertNotNull(name, "name != null");
		assertion.assertTrue(poolSize > 0, "poolSize > 0");
		assertion.assertTrue(queueSize > 0, "queueSize > 0");

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

		logTasks = configuration.getBoolean(COFIGURATION_TASK_GROUP_LOG_KEY, COFIGURATION_TASK_GROUP_LOG_DEFAULT);
	}

	@Override
	public Task asTask(Runnable runnable)
	{
		assertion.assertNotNull(runnable, "runnable != null");

		return new RunnableTask(runnable);
	}

	@Override
	public Task asTask(Callable callable)
	{
		assertion.assertNotNull(callable, "callable != null");

		return new CallableTask(callable);
	}

	@Override
	public TaskBarrier parallelBefore(Task... tasks)
	{
		assertion.assertNotEmpty(tasks, "tasks not empty");

		TaskBarrier arrive = new StandardTaskBarrier(tasks.length);

		for (Task task : tasks) {
			queue.add(new StandardTaskWrapperTask(task, null, arrive, logTasks));
		}

		return arrive;
	}

	@Override
	public TaskGroup parallelAfter(TaskBarrier await, Task... tasks)
	{
		assertion.assertNotNull(await, "await != null");
		assertion.assertNotEmpty(tasks, "tasks not empty");

		for (Task task : tasks) {
			queue.add(new StandardTaskWrapperTask(task, await, null, logTasks));
		}

		return this;
	}

	@Override
	public TaskGroup parallel(Task... tasks)
	{
		assertion.assertNotEmpty(tasks, "tasks not empty");

		for (Task task : tasks) {
			queue.add(new StandardTaskWrapperTask(task, null, null, logTasks));
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
		assertion.assertNotEmpty(tasks, "tasks not empty");

		queue.add(
			new StandardTaskWrapperTask(
				new SequentialTask(this, logTasks, tasks), null, null, logTasks)
		);

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
		log.trace("join:enter");

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

		log.trace("join:exit");

		return this;
	}

	/**
	 * Ends this task group gracefully (terminates workers) allowing waiting for its workers to terminate.
	 *
	 * @return
	 */
	@Override
	public TaskBarrier endGracefully()
	{
		assertion.assertFalse(ending, "ending == false");
		assertion.assertFalse(ended, "ended == false");

		log.trace("endGracefully:enter");

		ending = true;

		TaskBarrier terminationBarrier = new StandardTaskBarrier(workers.length);

		for (Worker worker : workers) {
			worker.terminate(terminationBarrier);
		}

		log.trace("endGracefully:exit");

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
