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

import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class StandardConfigurationTest
{

	@Test
	public void testSimpleUsage()
	{
		Configuration configuration = Configuration.create();

		// Try to get a not set property
		assertEquals(configuration.get("testSimpleUsage.NotSet"), null);

		// Set a string and get it in different ways
		configuration.set("testSimpleUsage.obj", "Test");
		assertEquals(configuration.get("testSimpleUsage.obj"), "Test");
		assertEquals(configuration.get("testSimpleUsage.obj", "Fail"), "Test");

		// Set an int and get it in different ways
		configuration.set("testSimpleUsage.string", "Test");
		assertEquals(configuration.getString("testSimpleUsage.string"), "Test");
		assertEquals(configuration.getString("testSimpleUsage.string", "Fail"), "Test");

		// Set an int and get it in different ways
		configuration.set("testSimpleUsage.int", 1);
		assertEquals(configuration.get("testSimpleUsage.int"), 1);
		assertEquals(configuration.getInt("testSimpleUsage.int"), 1);
		assertEquals(configuration.getInt("testSimpleUsage.int", 2), 1);

		// Set a long and get it in different ways
		configuration.set("testSimpleUsage.long", 1L);
		assertEquals(configuration.get("testSimpleUsage.long"), 1L);
		assertEquals(configuration.getLong("testSimpleUsage.long"), 1L);
		assertEquals(configuration.getLong("testSimpleUsage.long", 2L), 1L);

	}
}
