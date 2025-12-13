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
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
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

				try {
					Task task = queue.poll(100, TimeUnit.MILLISECONDS);

					if (task != null) {
						runningWorkerTasks.incrementAndGet();

						task.execute();

						runningWorkerTasks.decrementAndGet();
					}

				} catch (InterruptedException ex) {

					log.error(ex);

					return;
				}
			}

			log.info("Exiting", getName());

			synchronized (this) {
				try {
					this.wait(1);
				} catch (InterruptedException ex) {
					log.error(ex);
				}
			}

			terminationBarrier.arrive();
		}
	}

	protected final String name;
	protected final Worker[] workers;
	protected final BlockingQueue<Task> queue;
	protected final AtomicInteger runningWorkerTasks;

	@SuppressWarnings("CallToThreadStartDuringObjectConstruction")
	public StandardTaskGroup(String name, int poolSize, int queueSize, boolean daemon)
	{
		this.name = name;

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

	@Override
	public TaskGroup sequential(Task... tasks)
	{
		queue.add(new SequentialTask(tasks));

		return this;
	}

	@Override
	public TaskGroup join()
	{
		log.debug("join:enter");

		synchronized (Thread.currentThread()) {
			while (!queue.isEmpty() || runningWorkerTasks.get() > 0) {
				try {
					Thread.currentThread().wait(100);
				} catch (InterruptedException ex) {
					log.error(ex);
					return this;
				}
			}
		}

		log.debug("join:exit");

		return this;
	}

	public void endGracefully(TaskBarrier barrier)
	{
		log.debug("endGracefully:enter");

		join();

		for (Worker worker : workers) {
			worker.terminate(barrier);
		}

		log.debug("endGracefully:exit");
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

	public AtomicInteger getRunningWorkerTasks()
	{
		return runningWorkerTasks;
	}
}
