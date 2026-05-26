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

/**
 * Ends the given tasks gracefully
 *
 * @author Benjamin Schiller
 */
public class EndGracefully implements Task
{

	@SuppressWarnings("unused")
	private final static Logger log = LogManager.getLogger(EndGracefully.class.getName());

	@SuppressWarnings("unused")
	private final static Assert assertion = Assert.getAssert(EndGracefully.class.getName());

	protected final Compute tasks;

	public EndGracefully(Compute tasks)
	{
		assertion.assertNotNull(tasks, "tasks != null");

		this.tasks = tasks;
	}

	@Override
	public void execute()
	{
		tasks.endGracefully();
	}
}
