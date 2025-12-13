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
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class TasksTest
{

	@SuppressWarnings("unused")
	private final static Logger log = LogManager.getLogger(TasksTest.class.getName());

	// Few lines for an own task implementation - but provides more options than using Runnable ad hoc tasks
	protected final static class TestTask implements Task
	{

		@Override
		public void execute()
		{
			log.debug("TestTask.execute");
		}
	}

	@Test
	public void runSimpleTask()
	{
		log.debug("runSimpleTask:enter");
		log.start("runSimpleTask");

		// Easily create a Tasks managing task execution
		Tasks tasks = new StandardTasks();

		TaskGroup group = tasks.createTaskGroup("Main", 2, 100, true);

		// Ad hoc task from Runnable lambda
		Task task = group.asTask(() -> {
			log.debug("RunnableTask.run");
		});

		// Create own task implementation
		Task task2 = new TestTask();

		// Simple execution of 2 tasks - order and parallelism decided by Tasks
		group.parallel(task, task2);

		// Wait for all tasks to be done
		group.join();

		log.stopDebug("runSimpleTask");
	}

	/**
	 * Creates a worker group of 2 and a main group of 1. Processes 2 datasets in workers parallel. After the the main
	 * group processes both data sets into a final result.
	 */
	@Test
	public void runStructuredTasks()
	{
		log.debug("runStructuredTasks");
		log.start("runStructuredTasks");

		// Easily create a Tasks managing task execution
		Tasks tasks = new StandardTasks();

		// Create groups
		TaskGroup mainGroup = tasks.createTaskGroup("Main", 1, 100, false);
		TaskGroup workerGroup = tasks.createTaskGroup("Workers", 2, 100, true);

		float[] data1 = new float[]{0.0f, 1.0f, 2.0f, 3.0f};
		float[] data2 = new float[]{0.0f, 2.0f, 4.0f, 6.0f};
		float[] dataFinal = new float[4];

		TaskBarrier barrier = workerGroup.parallelBefore(
			// Process data1
			() -> {

				log.debug("runStructuredTasks:workerGroup1:parallelBefore:start");

				for (int i = 0; i < data1.length; ++i) {
					data1[i] = data1[i] + 1.25f;
				}

				log.debug("runStructuredTasks:workerGroup1:parallelBefore:done");
			},
			// Process data2
			() -> {

				log.debug("runStructuredTasks:workerGroup2:parallelBefore:start");

				for (int i = 0; i < data2.length; ++i) {
					data2[i] = data2[i] * data2[i];
				}

				log.debug("runStructuredTasks:workerGroup2:parallelBefore:done");
			}
		);

		mainGroup.parallelAfter(
			barrier,
			// Process data1 and data 2
			mainGroup.asTask(() -> {

				log.debug("runStructuredTasks:mainGroup:parallelAfter:start");

				for (int i = 0; i < data1.length; ++i) {
					dataFinal[i] = data1[i] * data2[i];

					log.debug("Final", i, ":", dataFinal[i]);
				}

				log.debug("runStructuredTasks:mainGroup:parallelAfter:done");
			}));

		// Wait for all tasks to be done and end properly
		tasks.endGracefully();

		log.stopDebug("runStructuredTasks");
	}

}
