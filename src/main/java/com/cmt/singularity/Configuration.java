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

/**
 *
 * @author Benjamin Schiller
 */
public interface Configuration
{

	/**
	 * Key in config for singularityClass
	 */
	public final static String CONFIGURATION_SINGULARITY_CLASS_KEY = "com.cmt.singularity.Configuration.singularityClass";

	/**
	 * Default in config for singularityClass
	 */
	public final static Class<? extends Singularity> CONFIGURATION_SINGULARITY_CLASS_DEFAULT = StandardSingularity.class;

	/**
	 * Returns a newly created and inited Configuration.
	 *
	 * @param args
	 * @return
	 */
	public static Configuration create(String... args)
	{
		Configuration configuration = new StandardConfiguration();

		configuration.init(args);

		return configuration;
	}

	void set(String key, Object value);

	void setFixed(String key, Object value);

	void setIfAbsent(String key, Object value);

	void setFixedIfAbsent(String key, Object value);

	Object get(String key);

	Object get(String key, Object defaultValue);

	String getString(String key);

	String getString(String key, String defaultValue);

	boolean getBoolean(String key);

	boolean getBoolean(String key, boolean defaultValue);

	int getInt(String key, int defaultValue);

	int getInt(String key);

	long getLong(String key);

	long getLong(String key, long defaultValue);

	float getFloat(String key);

	float getFloat(String key, float defaultValue);

	double getDouble(String key);

	double getDouble(String key, double defaultValue);

	short getShort(String key);

	short getShort(String key, short defaultValue);

	<ResultType> ResultType getAs(String key, Class<ResultType> type);

	<ResultType> ResultType getAs(String key, ResultType defaultValue, Class<ResultType> type);

	/**
	 * Inits this configuration with the given String args (usually coming from command line).
	 *
	 * @param args
	 */
	void init(String... args);

	/**
	 * Returns the configured singularityClass.
	 *
	 * @return
	 */
	Class<? extends Singularity> getSingularityClass();
}
