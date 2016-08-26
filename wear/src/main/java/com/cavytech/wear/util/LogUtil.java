/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cavytech.wear.util;


import android.util.Log;

import com.cavytech.wear.BuildConfig;


public class LogUtil {
	private final static String tag = "cavytech.wear";
	public static int logLevel = Log.VERBOSE;
	private static LogUtil logger = new LogUtil();

	public static LogUtil getLogger() {

		return logger;
	}

	private LogUtil() {

	}

	private String getFunctionName() {

		StackTraceElement[] sts = Thread.currentThread().getStackTrace();
		if (sts == null) {
			return null;
		}

		for (StackTraceElement st : sts) {

			if (st.isNativeMethod()) {
				continue;
			}
			if (st.getClassName().equals(Thread.class.getName())) {
				continue;
			}

			if (st.getClassName().equals(this.getClass().getName())) {
				continue;
			}
			return "[ " + Thread.currentThread().getName() + ": "
					+ st.getFileName() + ":" + st.getLineNumber() + " ]";
		}
		return null;
	}

	public void i(Object str) {
		if (!BuildConfig.LOG_DEBUG)
			return;
		if (logLevel <= Log.INFO) {
			String name = getFunctionName();
			if (name != null) {
				Log.i(tag, name + " - " + str);
			} else {
				Log.i(tag, str.toString());
			}
		}
	}

	public void v(Object str) {
		if (!BuildConfig.LOG_DEBUG)
			return;
		if (logLevel <= Log.VERBOSE) {
			String name = getFunctionName();
			if (name != null) {
				LogUtil.getLogger().i( name + " - " + str);
			} else {
				LogUtil.getLogger().i( str.toString());
			}
		}
	}

	public void w(Object str) {
		if (!BuildConfig.LOG_DEBUG)
			return;
		if (logLevel <= Log.WARN) {
			String name = getFunctionName();
			if (name != null) {
				Log.w(tag, name + " - " + str);
			} else {
				Log.w(tag, str.toString());
			}
		}
	}

	public void e(Object str) {
		if (!BuildConfig.LOG_DEBUG)
			return;
		if (logLevel <= Log.ERROR) {
			String name = getFunctionName();
			if (name != null) {
				Log.e(tag, name + " - " + str);
			} else {
				Log.e(tag, str.toString());
			}
		}
	}

	public void e(Exception ex) {
		if (!BuildConfig.LOG_DEBUG)
			return;
		if (logLevel <= Log.ERROR) {
			Log.e(tag, "error", ex);
		}
	}

	public void d(Object str) {
		if (!BuildConfig.LOG_DEBUG)
			return;
		if (logLevel <= Log.DEBUG) {
			String name = getFunctionName();
			if (name != null) {
				Log.d(tag, name + " - " + str);
			} else {
				Log.d(tag, str.toString());
			}
		}
	}

}
