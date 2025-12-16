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
import org.testng.Assert;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class SequentialTaskTest
{

	private static class Counter
	{

		public int count;
	}

	/**
	 * Test if the given sub tasks are properly called sequential and in order
	 */
	@Test
	public void testExecuteSequentialInOrder()
	{
		Configuration configuration = Configuration.create();

		TaskGroup group = new StandardTaskGroup(configuration, "SequentialTaskTest.testExecuteSequentialInOrder", 2, 10, true);

		Counter counter = new Counter();

		Task task1 = group.asTask(() -> {
			assertEquals(counter.count, 0);
			counter.count++;
		});

		Task task2 = group.asTask(() -> {
			assertEquals(counter.count, 1);
			counter.count++;
		});

		SequentialTask sequential = new SequentialTask(group, false, task1, task2);

		group.parallel(sequential);

		group.join();

		assertEquals(counter.count, 2);
	}

	/**
	 * Test if the group ending properly stops the sequential to stop early
	 */
	@Test
	public void testExecuteEndingEarlyCorrectly()
	{
		Configuration configuration = Configuration.create();

		TaskGroup group = new StandardTaskGroup(configuration, "SequentialTaskTest.testExecuteSequentialInOrder", 2, 10, true);

		Counter counter = new Counter();

		Task task1 = group.asTask(() -> {
			assertEquals(counter.count, 0);
			counter.count++;
			synchronized (Thread.currentThread()) {
				try {
					Thread.currentThread().wait(10);
				} catch (InterruptedException ex) {
					throw new RuntimeException(ex);
				}
			}
		});

		Task task2 = group.asTask(() -> {
			counter.count++;
			Assert.fail();
		});

		SequentialTask sequential = new SequentialTask(group, false, task1, task2);

		group.parallel(sequential);

		group.endGracefully();

		group.join();

		assertEquals(counter.count, 1);
	}
}
