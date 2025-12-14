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

	protected final Configuration configuration;

	public StandardTasks(Configuration configuration)
	{
		this.configuration = configuration;
	}

	@Override
	public TaskGroup createTaskGroup(String name, int poolSize, int queueSize, boolean daemon)
	{
		log.debug("createTaskGroup:enter");

		StandardTaskGroup group = new StandardTaskGroup(configuration, name, poolSize, queueSize, daemon);

		groups.add(group);

		log.debug("createTaskGroup:exit");

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
	public TaskBarrier endGracefully()
	{
		log.debug("endGracefully:enter");

		// Create copy to make sure the list does not change while iterating to make behavior easier to reason
		List<StandardTaskGroup> g = new ArrayList<>(groups);

		TaskBarrier[] barriers = new TaskBarrier[g.size()];

		int i = 0;
		for (StandardTaskGroup group : g) {
			barriers[i] = group.endGracefully();
			i++;
		}

		GroupedTaskBarrier terminationBarrier = new GroupedTaskBarrier(barriers);

		log.debug("endGracefully:exit");

		return terminationBarrier;
	}

	@Override
	public Optional<TaskGroup> getTaskGroupByName(String name)
	{
		return Optional.ofNullable((StandardTaskGroup) groups.stream().filter((tg) -> tg.getName().equals(name)).findAny().orElse(null));
	}

	@Override
	public Set<TaskGroup> getTaskGroups()
	{
		return Collections.unmodifiableSet(new HashSet<>(groups));
	}

	/**
	 * Determines if all contained task groups are ending which are contained at the moment of this call.
	 *
	 * @return
	 */
	@Override
	public boolean isEnding()
	{
		Set<TaskGroup> g = getTaskGroups();

		// If 1 is not ending -> return false
		for (TaskGroup group : g) {
			if (!group.isEnding()) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Determines if all contained task groups are ended which are contained at the moment of this call.
	 *
	 * @return
	 */
	@Override
	public boolean isEnded()
	{
		Set<TaskGroup> g = getTaskGroups();

		// If 1 is not ending -> return false
		for (TaskGroup group : g) {
			if (!group.isEnded()) {
				return false;
			}
		}

		return true;
	}
}
