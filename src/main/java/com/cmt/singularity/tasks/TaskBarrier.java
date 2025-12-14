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

import java.util.concurrent.TimeUnit;

/**
 * The TaskBarrier represents a concurrency barrier for awaiting task execution to have been finished (task left the
 * execute() method). await(...) is used to wait for arrival. arrive() signal an arrival. The guarantuee is that all
 * necessary arrivals have happened before the awaiting return.
 *
 * @author Benjamin Schiller
 */
public interface TaskBarrier
{

	/**
	 * Awaits this barrier to be arrived.
	 */
	void await();

	/**
	 * Awaits this barrier to be arrived up to timeOut units then returns. The timeout shall not exceed the timeOut
	 * units.
	 *
	 * @param timeOut
	 * @param unit
	 */
	void await(long timeOut, TimeUnit unit);

	/**
	 * Signals that it was arrived at this barrier. Will potentially cause all awating to be returning.
	 */
	void arrive();
}
