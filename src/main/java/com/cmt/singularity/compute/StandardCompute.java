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
package com.cmt.singularity.compute;

import com.cmt.singularity.assertion.Assert;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 *
 * @author Benjamin Schiller
 */
public final class StandardCompute implements Compute
{

	@SuppressWarnings("unused")
	private final static Logger log = LogManager.getLogger(StandardCompute.class.getName());

	private final static Assert assertion = Assert.getAssert(StandardCompute.class.getName());

	private final Set<StandardComputeGroup> groups = new ConcurrentSkipListSet<>();

	private final ComputeGroup mainGroup;

	public StandardCompute()
	{
		// Create initial main group
		mainGroup = createComputeGroup("Main", 1, 100, false);
	}

	@Override
	public ComputeGroup getMainGroup()
	{
		return mainGroup;
	}

	@Override
	public ComputeGroup createComputeGroup(String name, int poolSize, int queueSize, boolean daemon)
	{
		assertion.assertNotNull(name, "name != null");
		assertion.assertTrue(poolSize > 0, "poolSize > 0");
		assertion.assertTrue(queueSize > 0, "queueSize > 0");

		log.trace("createTaskGroup:enter");

		StandardComputeGroup group = new StandardComputeGroup(name, poolSize, queueSize, daemon);

		groups.add(group);

		log.trace("createTaskGroup:exit");

		return group;
	}

	@Override
	public void join()
	{
		log.trace("join:enter");

		// Create copy to make sure the list does not change while iterating to make behavior easier to reason
		for (ComputeGroup group : new ArrayList<>(groups)) {
			group.join();
		}

		log.trace("join:exit");
	}

	@Override
	public TaskBarrier endGracefully()
	{
		assertion.assertFalse(isEnding(), "isEnding() == false");
		assertion.assertFalse(isEnded(), "isEnded() == false");

		log.trace("endGracefully:enter");

		// Create copy to make sure the list does not change while iterating to make behavior easier to reason
		List<StandardComputeGroup> g = new ArrayList<>(groups);

		TaskBarrier[] barriers = new TaskBarrier[g.size()];

		int i = 0;
		for (StandardComputeGroup group : g) {
			barriers[i] = group.endGracefully();
			i++;
		}

		GroupedTaskBarrier terminationBarrier = new GroupedTaskBarrier(barriers);

		log.trace("endGracefully:exit");

		return terminationBarrier;
	}

	@Override
	public Optional<ComputeGroup> getComputeGroupByName(String name)
	{
		assertion.assertNotNull(name, "name != null");

		return Optional.ofNullable(groups.stream().filter((tg) -> tg.getName().equals(name)).findAny().orElse(null));
	}

	@Override
	public Set<ComputeGroup> getComputeGroups()
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
		Set<ComputeGroup> g = getComputeGroups();

		// If 1 is not ending -> return false
		for (ComputeGroup group : g) {
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
		Set<ComputeGroup> g = getComputeGroups();

		// If 1 is not ending -> return false
		for (ComputeGroup group : g) {
			if (!group.isEnded()) {
				return false;
			}
		}

		return true;
	}
}
