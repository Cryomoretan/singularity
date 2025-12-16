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
package com.cmt.singularity;

import com.cmt.singularity.assertion.Assert;
import com.cmt.singularity.tasks.Tasks;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Benjamin Schiller
 */
public interface Singularity
{

	public final static Assert assertion = Assert.getAssert(Singularity.class.getName());

	public static Singularity create(Configuration configuration)
	{
		assertion.assertNotNull(configuration, "configuration != null");
		assertion.assertNotNull(configuration.getSingularityClass(), "configuration.getSingularityClass() != null");

		try {

			Singularity singularity = configuration.getSingularityClass().getConstructor().newInstance();

			singularity.init(configuration);

			return singularity;
		} catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | InvocationTargetException ex) {
			throw new RuntimeException(ex);
		}
	}

	void init(Configuration configuration);

	Tasks getTasks();

	Configuration getConfiguration();
}
