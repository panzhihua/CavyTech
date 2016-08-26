package com.basecore.util.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import android.text.TextUtils;

import com.basecore.util.log.LogUtil;

public class FileUtils {

	public static final int TRY_DELETE_MAX_TIME = 5;

	public static boolean deleteDependon(File paramFile) {
		return deleteDependon(paramFile, 0);
	}

	public static boolean deleteDependon(File paramFile, int paramInt) {
		int i = 1;
		if (paramInt < 1)
			paramInt = TRY_DELETE_MAX_TIME;
		boolean bool = false;
		while (true) {
			if ((paramFile == null) || (bool) || (i > paramInt) || (!paramFile.isFile()) || (!paramFile.exists()))
				return bool;
			bool = paramFile.delete();
			if (bool)
				continue;
			LogUtil.getLogger().d(paramFile.getAbsolutePath() + "删除失败，失败次数为:" + i);
			++i;
		}
	}

	public static void DeleteFile(File file) {
		if (file.exists() == false) {
			return;
		} else {
			if (file.isFile()) {
				file.delete();
				return;
			}
			if (file.isDirectory()) {
				File[] childFile = file.listFiles();
				if (childFile == null || childFile.length == 0) {
					file.delete();
					return;
				}
				for (File f : childFile) {
					DeleteFile(f);
				}
				file.delete();
			}
		}
	}

	public static boolean doesExisted(File paramFile) {
		return (paramFile != null) && (paramFile.exists());
	}

	public static boolean doesExisted(String paramString) {
		if (TextUtils.isEmpty(paramString))
			return false;
		return doesExisted(new File(paramString));
	}

	public static boolean deleteDependon(String paramString) {
		return deleteDependon(paramString, 0);
	}

	public static boolean deleteDependon(String paramString, int paramInt) {
		if (TextUtils.isEmpty(paramString))
			return false;
		return deleteDependon(new File(paramString), paramInt);
	}

	public static InputStream makeInputBuffered(InputStream paramInputStream) {
		AssertUtils.checkNull(paramInputStream);
		if (paramInputStream instanceof BufferedInputStream)
			return paramInputStream;
		return new BufferedInputStream(paramInputStream, 512 * 1024);
	}

	public static OutputStream makeOutputBuffered(OutputStream paramOutputStream) {
		AssertUtils.checkNull(paramOutputStream);
		if (paramOutputStream instanceof BufferedOutputStream)
			return paramOutputStream;
		return new BufferedOutputStream(paramOutputStream, 512 * 1024);
	}
}