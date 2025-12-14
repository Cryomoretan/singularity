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

/**
 * This task calls the given list of tasks in order
 *
 * @author Benjamin Schiller
 */
public class SequentialTask implements Task
{

	@SuppressWarnings("unused")
	private final static Logger log = LogManager.getLogger(SequentialTask.class.getName());

	protected final Task[] tasks;
	protected final TaskGroup group;

	public SequentialTask(TaskGroup group, Task... tasks)
	{
		this.group = group;
		this.tasks = tasks;
	}

	/**
	 * Execute all tasks sequential in order. between each task the group is checked if it is ending then execution is
	 * not continued.
	 */
	@Override
	public void execute()
	{
		log.debug("execute:enter");
		log.start("execute");

		for (Task task : tasks) {

			String taskLog = task.getClass().getName() + ".execute";

			log.debug(taskLog + ":enter");
			log.start(taskLog);

			task.execute();

			log.stopDebug(taskLog);
			log.debug(taskLog + ":exit");

			// End early if the group is ending
			if (group.isEnding()) {
				break;
			}
		}

		log.stopDebug("execute");
		log.debug("execute:exit");
	}
}
