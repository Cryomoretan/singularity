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

import com.cmt.singularity.tasks.Task;
import com.cmt.singularity.tasks.TaskContext;
import com.cmt.singularity.tasks.StandardTasks;
import com.cmt.singularity.tasks.Tasks;
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
		public void execute(TaskContext context)
		{
			log.debug("TestTask.execute");
		}
	}

	@Test
	public void runSimpleTask()
	{
		log.debug("runSimpleTask");
		log.start("runSimpleTask");

		// Easily create a Tasks managing task execution
		Tasks tasks = new StandardTasks(2, 2);

		// Ad hoc task from Runnable lambda
		Task task = tasks.asTask(() -> {
			log.debug("RunnableTask.run");
		});

		// Create own task implementation
		Task task2 = new TestTask();

		// Simple execution of 2 tasks - order and parallelism decided by Tasks
		tasks.parallel(task, task2);

		// Wait for all tasks to be done
		tasks.join();

		log.stopDebug("runSimpleTask");
	}
}
