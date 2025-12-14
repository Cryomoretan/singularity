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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Benjamin Schiller
 */
public class StandardConfiguration implements Configuration
{

	public final static String KEY_SINGULARITY_CLASS = "singularityClass";
	public final static Class<? extends Singularity> DEFAULT_SINGULARITY_CLASS = StandardSingularity.class;

	protected final Map<String, Object> properties;

	public StandardConfiguration()
	{
		properties = new ConcurrentHashMap<>();
	}

	@Override
	public void init(String... args)
	{
		// @todo What to be done with the args?
	}

	@Override
	public void set(String key, Object value)
	{
		properties.put(key, value);
	}

	@Override
	public void setFixed(String key, Object value)
	{
		properties.put(key, value);
	}

	@Override
	public Object get(String key)
	{
		return properties.get(key);
	}

	@Override
	public Object get(String key, Object defaultValue)
	{
		Object value = properties.get(key);

		if (value == null) {
			return defaultValue;
		}

		return value;
	}

	@Override
	public String getString(String key)
	{
		return (String) get(key);
	}

	@Override
	public String getString(String key, String defaultValue)
	{
		return (String) get(key, defaultValue);
	}

	@Override
	public boolean getBoolean(String key)
	{
		return (Boolean) get(key);
	}

	@Override
	public boolean getBoolean(String key, boolean defaultValue)
	{
		return (Boolean) get(key, defaultValue);
	}

	@Override
	public int getInt(String key)
	{
		return (Integer) get(key);
	}

	@Override
	public int getInt(String key, int defaultValue)
	{
		return (Integer) get(key, defaultValue);
	}

	@Override
	public long getLong(String key)
	{
		return (Long) get(key);
	}

	@Override
	public long getLong(String key, long defaultValue)
	{
		return (Long) get(key, defaultValue);
	}

	@Override
	public float getFloat(String key)
	{
		return (Float) get(key);
	}

	@Override
	public float getFloat(String key, float defaultValue)
	{
		return (Float) get(key, defaultValue);
	}

	@Override
	public double getDouble(String key)
	{
		return (Integer) get(key);
	}

	@Override
	public double getDouble(String key, double defaultValue)
	{
		return (Integer) get(key, defaultValue);
	}

	@Override
	public short getShort(String key)
	{
		return (Short) get(key);
	}

	@Override
	public short getShort(String key, short defaultValue)
	{
		return (Short) get(key, defaultValue);
	}

	@Override
	public <ResultType> ResultType getAs(String key, Class<ResultType> type)
	{
		return (ResultType) get(key);
	}

	@Override
	public <ResultType> ResultType getAs(String key, ResultType defaultValue, Class<ResultType> type)
	{
		return (ResultType) get(key, defaultValue);
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	@Override
	public Class<? extends Singularity> getSingularityClass()
	{
		return getAs(KEY_SINGULARITY_CLASS, DEFAULT_SINGULARITY_CLASS, Class.class);
	}

	public void setSingularityClass(Class<? extends Singularity> singularityClass)
	{
		set(KEY_SINGULARITY_CLASS, singularityClass);
	}
	// "Getters/Setters" </editor-fold>

}
