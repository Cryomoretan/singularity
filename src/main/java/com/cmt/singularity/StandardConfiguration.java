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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Benjamin Schiller
 */
public class StandardConfiguration implements Configuration
{

	public final static Assert assertion = Assert.getAssert(StandardConfiguration.class.getName());

	protected final Map<String, Object> properties;

	public StandardConfiguration()
	{
		properties = new ConcurrentHashMap<>();
	}

	@Override
	public void init(String... args)
	{
		if (args != null) {
			for (String arg : args) {
				String[] argParts = arg.split("=");

				// Handle -X=value
				if (argParts.length > 0 && argParts.length < 3) {

					String key = argParts[0].substring(1).trim();

					String value = "true";

					if (argParts.length == 2) {
						value = argParts[1].trim();
					}

					properties.put(key, value);
				}
			}
		}
	}

	@Override
	public void set(String key, Object value)
	{
		assertion.assertNotNull(key, "key != null");
		assertion.assertNotNull(value, "value != null");

		properties.put(key, value);
	}

	@Override
	public void setFixed(String key, Object value)
	{
		assertion.assertNotNull(key, "key != null");
		assertion.assertNotNull(value, "value != null");

		// @todo handle fixed config values
		properties.put(key, value);
	}

	@Override
	public void setIfAbsent(String key, Object value)
	{
		assertion.assertNotNull(key, "key != null");
		assertion.assertNotNull(value, "value != null");

		properties.putIfAbsent(key, value);
	}

	@Override
	public void setFixedIfAbsent(String key, Object value)
	{
		assertion.assertNotNull(key, "key != null");
		assertion.assertNotNull(value, "value != null");

		// @todo handle fixed config values
		properties.putIfAbsent(key, value);
	}

	@Override
	public Object get(String key)
	{
		assertion.assertNotNull(key, "key != null");

		return properties.get(key);
	}

	@Override
	public Object get(String key, Object defaultValue)
	{
		assertion.assertNotNull(key, "key != null");

		Object value = properties.get(key);

		if (value == null) {
			return defaultValue;
		}

		return value;
	}

	protected <TargetType> TargetType getConvert(String key, Class<TargetType> targetClass, TargetType defaultValue)
	{
		assertion.assertNotNull(key, "key != null");
		assertion.assertNotNull(targetClass, "targetClass != null");

		Object value = properties.get(key);

		if (value == null) {
			return defaultValue;
		}

		if (targetClass.equals(Object.class)) {
			return (TargetType) value;
		}

		if (targetClass.equals(String.class)) {
			return (TargetType) value.toString();
		}

		// Support string value in config being converted to Boolean
		if (targetClass.equals(Boolean.class) && value instanceof String string) {
			return (TargetType) Boolean.valueOf(string);
		}

		// Support string value in config being converted to Integer
		if (targetClass.equals(Integer.class) && value instanceof String string) {
			return (TargetType) Integer.valueOf(string);
		}

		// Support string value in config being converted to Float
		if (targetClass.equals(Float.class) && value instanceof String string) {
			return (TargetType) Float.valueOf(string);
		}

		// Support string value in config being converted to Long
		if (targetClass.equals(Long.class) && value instanceof String string) {
			return (TargetType) Long.valueOf(string);
		}

		// Support string value in config being converted to Double
		if (targetClass.equals(Double.class) && value instanceof String string) {
			return (TargetType) Double.valueOf(string);
		}

		return (TargetType) value;
	}

	@Override
	public String getString(String key)
	{
		return getConvert(key, String.class, null);
	}

	@Override
	public String getString(String key, String defaultValue)
	{
		return getConvert(key, String.class, defaultValue);
	}

	@Override
	public boolean getBoolean(String key)
	{
		return getConvert(key, Boolean.class, null);
	}

	@Override
	public boolean getBoolean(String key, boolean defaultValue)
	{
		return getConvert(key, Boolean.class, defaultValue);
	}

	@Override
	public int getInt(String key)
	{
		return getConvert(key, Integer.class, null);
	}

	@Override
	public int getInt(String key, int defaultValue)
	{
		return getConvert(key, Integer.class, defaultValue);
	}

	@Override
	public long getLong(String key)
	{
		return getConvert(key, Long.class, null);
	}

	@Override
	public long getLong(String key, long defaultValue)
	{
		return getConvert(key, Long.class, defaultValue);
	}

	@Override
	public float getFloat(String key)
	{
		return getConvert(key, Float.class, null);
	}

	@Override
	public float getFloat(String key, float defaultValue)
	{
		return getConvert(key, Float.class, defaultValue);
	}

	@Override
	public double getDouble(String key)
	{
		return getConvert(key, Double.class, null);
	}

	@Override
	public double getDouble(String key, double defaultValue)
	{
		return getConvert(key, Double.class, defaultValue);
	}

	@Override
	public short getShort(String key)
	{
		return getConvert(key, Short.class, null);
	}

	@Override
	public short getShort(String key, short defaultValue)
	{
		return getConvert(key, Short.class, defaultValue);
	}

	@Override
	public <ResultType> ResultType getAs(String key, Class<ResultType> type)
	{
		return getConvert(key, type, null);
	}

	@Override
	public <ResultType> ResultType getAs(String key, ResultType defaultValue, Class<ResultType> type)
	{
		return getConvert(key, type, defaultValue);
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	@Override
	public Class<? extends Singularity> getSingularityClass()
	{
		return getAs(CONFIGURATION_SINGULARITY_CLASS_KEY, CONFIGURATION_SINGULARITY_CLASS_DEFAULT, Class.class);
	}

	public void setSingularityClass(Class<? extends Singularity> singularityClass)
	{
		assertion.assertNotNull(singularityClass, "singularityClass != null");

		set(CONFIGURATION_SINGULARITY_CLASS_KEY, singularityClass);
	}
	// "Getters/Setters" </editor-fold>´
}
