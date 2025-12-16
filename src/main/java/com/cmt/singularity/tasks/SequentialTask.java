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

import com.cmt.singularity.assertion.Assert;
import com.cmt.singularity.tasks.StandardTaskGroup.StandardTaskWrapperTask;

/**
 * This task calls the given list of tasks in order
 *
 * @author Benjamin Schiller
 */
public class SequentialTask implements Task
{

	private final static Assert assertion = Assert.getAssert(SequentialTask.class.getName());

	protected final Task[] tasks;
	protected final TaskGroup group;
	protected final boolean logTasks;

	public SequentialTask(TaskGroup group, boolean logTasks, Task... tasks)
	{
		assertion.assertNotNull(group, "group != 0");
		assertion.assertNotEmpty(tasks, "tasks not empty");

		this.group = group;
		this.logTasks = logTasks;
		this.tasks = tasks;
	}

	/**
	 * Execute all tasks sequential in order. between each task the group is checked if it is ending then execution is
	 * not continued.
	 */
	@Override
	public void execute()
	{
		for (Task task : tasks) {

			// Create and execute wrapper (allowing logging)
			StandardTaskWrapperTask wrapper = new StandardTaskGroup.StandardTaskWrapperTask(task, null, null, logTasks);
			wrapper.execute();

			// End early if the group is ending
			if (group.isEnding()) {
				break;
			}
		}
	}
}
