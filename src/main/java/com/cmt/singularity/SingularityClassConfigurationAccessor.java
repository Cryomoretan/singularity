// <editor-fold desc="The MIT License" defaultstate="collapsed">
/*
 * The MIT License
 *
 * Copyright 2026 Cryomoretan GmbH.
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

/**
 *
 * @author Benjamin Schiller
 */
public final class SingularityClassConfigurationAccessor implements ConfigurationAccessor
{

	public final static Assert assertion = Assert.getAssert(SingularityClassConfigurationAccessor.class.getName());

	/**
	 * Key in config for singularityClass
	 */
	public final static String KEY = "com.cmt.singularity.singularityClass";

	/**
	 * Default in config for singularityClass
	 */
	public final static Class<? extends Singularity> DEFAULT = StandardSingularity.class;

	public static Class<? extends Singularity> getSingularityClass(Configuration configuration)
	{
		assertion.assertNotNull(configuration, "configuration != null");

		return configuration.getAs(KEY, DEFAULT, Class.class);
	}

	public static void setSingularityClass(Configuration configuration, Class<? extends Singularity> singularityClass)
	{
		assertion.assertNotNull(configuration, "configuration != null");
		assertion.assertNotNull(singularityClass, "singularityClass != null");

		configuration.set(KEY, singularityClass);
	}

	@SuppressWarnings("unused")
	private SingularityClassConfigurationAccessor()
	{
		// NEVER INSTANTIATED
	}
}
