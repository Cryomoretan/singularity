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

import java.util.Optional;
import java.util.Set;

/**
 * The Tasks provides the global management of running asynchronous tasks. The task running is handled through
 * TaskGroups.
 *
 * @author Benjamin Schiller
 */
public interface Tasks
{

	/**
	 * Creates and registers the task group in this tasks.
	 *
	 * @param name
	 * @param poolSize
	 * @param queueSize
	 * @param daemon
	 * @return
	 */
	TaskGroup createTaskGroup(String name, int poolSize, int queueSize, boolean daemon);

	/**
	 * Returns a Set of the task groups at call time. Changes of tasks groups are not reflected.
	 *
	 * @return
	 */
	Set<TaskGroup> getTaskGroups();

	/**
	 * Optionally returns the first task group with the the given name.
	 *
	 * @param name
	 * @return
	 */
	Optional<TaskGroup> getTaskGroupByName(String name);

	/**
	 * Waits till ALL tasks in that group have been processed - means no tasks in queue and all tasks that were
	 * processed exited their execute() method.
	 */
	void join();

	/**
	 * Requests ALL task groups to end and returns a barrier to wait for all tasks to have ended (like join).
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
