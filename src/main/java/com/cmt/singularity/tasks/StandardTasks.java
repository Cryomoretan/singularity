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
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 *
 * @author Benjamin Schiller
 */
public class StandardTasks implements Tasks
{

	@SuppressWarnings("unused")
	private final static Logger log = LogManager.getLogger(StandardTasks.class.getName());

	protected final Set<StandardTaskGroup> groups = new ConcurrentSkipListSet<>();

	@Override
	public TaskGroup createTaskGroup(String name, int poolSize, int queueSize, boolean daemon)
	{
		StandardTaskGroup group = new StandardTaskGroup(name, poolSize, queueSize, daemon);

		groups.add(group);

		return group;
	}

	@Override
	public void join()
	{
		log.debug("join:enter");

		// Create copy to make sure the list does not change while iterating to make behavior easier to reason
		for (TaskGroup group : new ArrayList<>(groups)) {
			group.join();
		}

		log.debug("join:exit");
	}

	@Override
	public void endGracefully()
	{
		log.debug("endGracefully:enter");

		join();

		// Create copy to make sure the list does not change while iterating to make behavior easier to reason
		List<StandardTaskGroup> g = new ArrayList<>(groups);

		TaskBarrier terminationBarrier = new StandardTaskBarrier(g.size());

		for (StandardTaskGroup group : g) {
			group.endGracefully(terminationBarrier);
		}

		terminationBarrier.await();

		log.debug("endGracefully:exit");
	}

	@Override
	public Optional<TaskGroup> getTaskGroupByName(String name)
	{
		return Optional.ofNullable((StandardTaskGroup) groups.stream().filter((tg) -> tg.getName().equals(name)).findAny().orElse(null));
	}

	@Override
	public Set<TaskGroup> getTaskGroups()
	{
		return Collections.unmodifiableSet(groups);
	}
}
