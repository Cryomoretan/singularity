// <editor-fold desc="The MIT License" defaultstate="collapsed">
/*
 * The MIT License
 *
 * Copyright 2025 Studio 42 GmbH ( https://www.s42m.de ).
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
package com.cmt.singularity.assertion;

import java.util.Objects;

/**
 *
 * @author Benjamin Schiller
 */
public final class Assert
{

	public final static Assert getAssert(String module)
	{
		// @todo Work on assertion pattern in combination with Configuration
		return new Assert(true);
	}

	private final boolean active;

	public Assert(boolean active)
	{
		this.active = active;
	}

	public void assertTrue(boolean assertion, String message)
	{
		if (!active) {
			return;
		}

		if (!assertion) {
			throw new RuntimeException(message);
		}
	}

	public void assertNotNull(Object assertion, String message)
	{
		if (!active) {
			return;
		}

		if (assertion == null) {
			throw new RuntimeException(message);
		}
	}

	public void assertNotEmpty(Object[] assertion, String message)
	{
		if (!active) {
			return;
		}

		if (assertion == null) {
			throw new RuntimeException(message);
		}

		if (assertion.length == 0) {
			throw new RuntimeException(message);
		}
	}

	public void assertFalse(boolean assertion, String message)
	{
		if (!active) {
			return;
		}

		if (assertion) {
			throw new RuntimeException(message);
		}
	}

	public void assertEquals(boolean assertion, boolean expected, String message)
	{
		if (!active) {
			return;
		}

		if (assertion != expected) {
			throw new RuntimeException(message);
		}
	}

	public void assertEquals(int assertion, int expected, String message)
	{
		if (!active) {
			return;
		}

		if (assertion != expected) {
			throw new RuntimeException(message);
		}
	}

	public void assertEquals(float assertion, float expected, String message)
	{
		if (!active) {
			return;
		}

		if (assertion != expected) {
			throw new RuntimeException(message);
		}
	}

	public void assertEquals(double assertion, double expected, String message)
	{
		if (!active) {
			return;
		}

		if (assertion != expected) {
			throw new RuntimeException(message);
		}
	}

	public void assertEquals(Object assertion, Object expected, String message)
	{
		if (!active) {
			return;
		}

		if (Objects.equals(assertion, expected)) {
			throw new RuntimeException(message);
		}
	}
}
