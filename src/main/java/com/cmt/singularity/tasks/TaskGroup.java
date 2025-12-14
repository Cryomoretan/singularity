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

import java.util.concurrent.Callable;

/**
 * The TaskGroup represents an executor scope. Tasks can get added with certain contracts (parallel, sequential, ...)
 * and will get executed accordingly.
 *
 * @author Benjamin Schiller
 */
public interface TaskGroup
{

	/**
	 * The name of this task group.
	 *
	 * @return
	 */
	String getName();

	/**
	 * Returns a task implementation of the given Runnable that can be executed in this task group.
	 *
	 * @param runnable
	 * @return
	 */
	Task asTask(Runnable runnable);

	/**
	 * Returns a task implementation of the given Callable that can be executed in this task group.
	 *
	 * @param callable
	 * @return
	 */
	Task asTask(Callable callable);

	/**
	 * Add a list of tasks to be executed in parallel by this task group.
	 *
	 * @param tasks
	 * @return
	 */
	TaskGroup parallel(Task... tasks);

	/**
	 * Add a list of tasks to be executed in sequentially in order by this task group.
	 *
	 * @param tasks
	 * @return
	 */
	TaskGroup sequential(Task... tasks);

	/**
	 * Add a list of tasks to be executed in parallel by this task group before the returned barrier is arrived. All
	 * tasks in this list have returned from their execute() method before the barrier is arriving.
	 *
	 * @param tasks
	 * @return
	 */
	TaskBarrier parallelBefore(Task... tasks);

	/**
	 * Executes the given tasks guarantueed after the arrival of the given barrier. The tasks may not block the
	 * execution of this task group until then.
	 *
	 * @param barrier
	 * @param tasks
	 * @return
	 */
	TaskGroup parallelAfter(TaskBarrier barrier, Task... tasks);

	/**
	 * Waits till ALL tasks in that group have been processed - means no tasks in queue and all tasks exited their
	 * execute() method.
	 *
	 * @return
	 */
	TaskGroup join();

	/**
	 * Ends this group returning a barrier that is arrived after the group has ended.
	 *
	 * @return
	 */
	TaskBarrier endGracefully();

	/**
	 * Signals if the tasks are ending or ended
	 *
	 * @return
	 */
	boolean isEnding();

	/**
	 * Signals if the tasks are ended
	 *
	 * @return
	 */
	boolean isEnded();
}
