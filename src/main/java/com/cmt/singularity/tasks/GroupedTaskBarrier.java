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
import java.util.concurrent.TimeUnit;

/**
 * The grouped task barrier allows to group other task barriers to act as a single barrier. This is useful when awaiting
 * the exit of multiple task groups etc.
 *
 * @author Benjamin Schiller
 */
public class GroupedTaskBarrier implements TaskBarrier
{

	private final static Assert assertion = Assert.getAssert(GroupedTaskBarrier.class.getName());

	/**
	 * The given task barriers to treat as one
	 */
	protected final TaskBarrier[] barriers;

	public GroupedTaskBarrier(TaskBarrier... barriers)
	{
		assertion.assertNotEmpty(barriers, "barriers not empty");

		this.barriers = barriers;
	}

	/**
	 * Will await() each grouped barrier.
	 */
	@Override
	public void await()
	{
		for (TaskBarrier barrier : barriers) {
			barrier.await();
		}
	}

	/**
	 * Will await the grouped barriers handling that the total timeout is not exceeded. Will call each grouped barrier
	 * with the delta timeout left. So it might not call the await(timeOut, unit) of some of the contained barriers if
	 * the timeout is already reached before.
	 *
	 * @param timeOut
	 * @param unit
	 */
	@Override
	public void await(long timeOut, TimeUnit unit)
	{
		assertion.assertTrue(timeOut >= 0, "timeOut >= 0");
		assertion.assertNotNull(unit, "unit != null");

		// Make sure to hold the timeOut contract by subtracting the used timeout duration of each contained barrier.
		long tout = timeOut;

		for (TaskBarrier barrier : barriers) {

			long before = System.nanoTime();

			barrier.await(tout, unit);

			// Get the delta time converted in correct units and exit the loop if all time is used up.
			long delta = unit.convert(System.nanoTime() - before, TimeUnit.NANOSECONDS);

			tout -= delta;

			if (tout <= 0) {
				break;
			}
		}
	}

	/**
	 * Will call arrive() for all grouped barriers.
	 */
	@Override
	public void arrive()
	{
		for (TaskBarrier barrier : barriers) {
			barrier.arrive();
		}
	}
}
