package com.basecore.util.core;

import java.util.Collection;
import java.util.Map;

import android.text.TextUtils;

import com.basecore.BuildConfig;

public class AssertUtils {
	public static void check(boolean paramBoolean) {
		if ((paramBoolean) || (BuildConfig.DEBUG))
			return;
		throw new IllegalArgumentException("isTrue must be TRUE!");
	}

	public static void checkArrayNullOrEmpty(byte[] paramArrayOfByte) {
		if ((paramArrayOfByte == null) && (!BuildConfig.DEBUG))
			throw new NullPointerException();
		if ((paramArrayOfByte.length != 0) || (BuildConfig.DEBUG))
			return;
		throw new IllegalArgumentException("array must have some datas");
	}

	public static void checkArrayNullOrEmpty(char[] paramArrayOfChar) {
		if ((paramArrayOfChar == null) && (!BuildConfig.DEBUG))
			throw new NullPointerException();
		if ((paramArrayOfChar.length != 0) || (BuildConfig.DEBUG))
			return;
		throw new IllegalArgumentException("array must have some datas");
	}

	public static void checkArrayNullOrEmpty(double[] paramArrayOfDouble) {
		if ((paramArrayOfDouble == null) && (!BuildConfig.DEBUG))
			throw new NullPointerException();
		if ((paramArrayOfDouble.length != 0) || (BuildConfig.DEBUG))
			return;
		throw new IllegalArgumentException("array must have some datas");
	}

	public static void checkArrayNullOrEmpty(float[] paramArrayOfFloat) {
		if ((paramArrayOfFloat == null) && (!BuildConfig.DEBUG))
			throw new NullPointerException();
		if ((paramArrayOfFloat.length != 0) || (BuildConfig.DEBUG))
			return;
		throw new IllegalArgumentException("array must have some datas");
	}

	public static void checkArrayNullOrEmpty(int[] paramArrayOfInt) {
		if ((paramArrayOfInt == null) && (!BuildConfig.DEBUG))
			throw new NullPointerException();
		if ((paramArrayOfInt.length != 0) || (BuildConfig.DEBUG))
			return;
		throw new IllegalArgumentException("array must have some datas");
	}

	public static void checkArrayNullOrEmpty(long[] paramArrayOfLong) {
		if ((paramArrayOfLong == null) && (!BuildConfig.DEBUG))
			throw new NullPointerException();
		if ((paramArrayOfLong.length != 0) || (BuildConfig.DEBUG))
			return;
		throw new IllegalArgumentException("array must have some datas");
	}

	public static void checkArrayNullOrEmpty(Object[] paramArrayOfObject) {
		if ((paramArrayOfObject == null) && (!BuildConfig.DEBUG))
			throw new NullPointerException();
		if ((paramArrayOfObject.length != 0) || (BuildConfig.DEBUG))
			return;
		throw new IllegalArgumentException("array must have some datas");
	}

	public static void checkArrayNullOrEmpty(short[] paramArrayOfShort) {
		if ((paramArrayOfShort == null) && (!BuildConfig.DEBUG))
			throw new NullPointerException();
		if ((paramArrayOfShort.length != 0) || (BuildConfig.DEBUG))
			return;
		throw new IllegalArgumentException("array must have some datas");
	}

	public static void checkCollectionNullOrEmpty(Collection<?> paramCollection) {
		if ((paramCollection == null) && (!BuildConfig.DEBUG))
			throw new NullPointerException();
		if ((paramCollection.size() != 0) || (BuildConfig.DEBUG))
			return;
		throw new IllegalArgumentException("array must have some datas");
	}

	public static void checkMapNullOrEmpty(Map<?, ?> paramMap) {
		if ((paramMap == null) && (!BuildConfig.DEBUG))
			throw new NullPointerException();
		if ((paramMap.size() != 0) || (BuildConfig.DEBUG))
			return;
		throw new IllegalArgumentException("array must have some datas");
	}

	public static void checkNull(Object paramObject) {
		if ((paramObject != null) || (BuildConfig.DEBUG))
			return;
		throw new NullPointerException();
	}

	public static void checkRange(Collection<?> paramCollection, int paramInt) {
		checkCollectionNullOrEmpty(paramCollection);
		if ((paramInt >= 0) && (paramInt < paramCollection.size()))
			return;
		throw new IndexOutOfBoundsException(
				"You can't get the element of position:" + paramInt + "!");
	}

	public static void checkRange(Map<?, ?> paramMap, int paramInt) {
		checkMapNullOrEmpty(paramMap);
		if ((paramInt >= 0) && (paramInt < paramMap.size()))
			return;
		throw new IndexOutOfBoundsException(
				"You can't get the element of position:" + paramInt + "!");
	}

	public static void checkRange(byte[] paramArrayOfByte, int paramInt) {
		checkArrayNullOrEmpty(paramArrayOfByte);
		if ((paramInt >= 0) && (paramInt < paramArrayOfByte.length))
			return;
		throw new IndexOutOfBoundsException(
				"You can't get the element of position:" + paramInt + "!");
	}

	public static void checkRange(char[] paramArrayOfChar, int paramInt) {
		checkArrayNullOrEmpty(paramArrayOfChar);
		if ((paramInt >= 0) && (paramInt < paramArrayOfChar.length))
			return;
		throw new IndexOutOfBoundsException(
				"You can't get the element of position:" + paramInt + "!");
	}

	public static void checkRange(double[] paramArrayOfDouble, int paramInt) {
		checkArrayNullOrEmpty(paramArrayOfDouble);
		if ((paramInt >= 0) && (paramInt < paramArrayOfDouble.length))
			return;
		throw new IndexOutOfBoundsException(
				"You can't get the element of position:" + paramInt + "!");
	}

	public static void checkRange(float[] paramArrayOfFloat, int paramInt) {
		checkArrayNullOrEmpty(paramArrayOfFloat);
		if ((paramInt >= 0) && (paramInt < paramArrayOfFloat.length))
			return;
		throw new IndexOutOfBoundsException(
				"You can't get the element of position:" + paramInt + "!");
	}

	public static void checkRange(int[] paramArrayOfInt, int paramInt) {
		checkArrayNullOrEmpty(paramArrayOfInt);
		if ((paramInt >= 0) && (paramInt < paramArrayOfInt.length))
			return;
		throw new IndexOutOfBoundsException(
				"You can't get the element of position:" + paramInt + "!");
	}

	public static void checkRange(long[] paramArrayOfLong, int paramInt) {
		checkArrayNullOrEmpty(paramArrayOfLong);
		if ((paramInt >= 0) && (paramInt < paramArrayOfLong.length))
			return;
		throw new IndexOutOfBoundsException(
				"You can't get the element of position:" + paramInt + "!");
	}

	public static void checkRange(Object[] paramArrayOfObject, int paramInt) {
		checkArrayNullOrEmpty(paramArrayOfObject);
		if ((paramInt >= 0) && (paramInt < paramArrayOfObject.length))
			return;
		throw new IndexOutOfBoundsException(
				"You can't get the element of position:" + paramInt + "!");
	}

	public static void checkRange(short[] paramArrayOfShort, int paramInt) {
		checkArrayNullOrEmpty(paramArrayOfShort);
		if ((paramInt >= 0) && (paramInt < paramArrayOfShort.length))
			return;
		throw new IndexOutOfBoundsException(
				"You can't get the element of position:" + paramInt + "!");
	}

	public static void checkStringNullOrEmpty(CharSequence paramCharSequence) {
		if ((!TextUtils.isEmpty(paramCharSequence)) || (BuildConfig.DEBUG))
			return;
		throw new IllegalArgumentException("str must have some datas");
	}

	public static Throwable getRootCause(Throwable paramThrowable) {
		if (paramThrowable == null) {
			paramThrowable = null;
			return paramThrowable;
		}
		Throwable localThrowable1 = paramThrowable.getCause();
		Throwable localThrowable2 = localThrowable1;
		while (true) {
			if (localThrowable1 == null) {
				if (localThrowable2 != null)
					;
				return localThrowable2;
			}
			localThrowable2 = localThrowable1;
			localThrowable1 = localThrowable1.getCause();
		}
	}
}