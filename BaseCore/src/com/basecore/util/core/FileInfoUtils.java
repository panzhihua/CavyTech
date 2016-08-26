/*
 * Copyright (C) 2013
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
package com.basecore.util.core;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;

import android.content.Context;
import android.os.Environment;

import com.nostra13.universalimageloader.utils.StorageUtils;

public class FileInfoUtils
{
	/**
	 * 返回自定文件或文件夹的大小
	 * 
	 * @param f
	 * @return
	 * @throws Exception
	 */
	public static long getFileSizes(File f) throws Exception
	{// 取得文件大小
		long s = 0;
		if (f.exists())
		{
			FileInputStream fis = null;
			fis = new FileInputStream(f);
			s = fis.available();
		} else
		{
			f.createNewFile();
			System.out.println("文件不存在");
		}
		return s;
	}

	// 递归
	public static long getFileSize(File f) throws Exception// 取得文件夹大小
	{
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++)
		{
			if (flist[i].isDirectory())
			{
				size = size + getFileSize(flist[i]);
			} else
			{
				size = size + flist[i].length();
			}
		}
		return size;
	}

	public static String FormetFileSize(long fileS)
	{// 转换文件大小
		DecimalFormat df = new DecimalFormat("#0.00");
		String fileSizeString = "";
		if (fileS < 1024)
		{
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576)
		{
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824)
		{
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else
		{
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	public static long getlist(File f)
	{// 递归求取目录文件个数
		long size = 0;
		File flist[] = f.listFiles();
		size = flist.length;
		for (int i = 0; i < flist.length; i++)
		{
			if (flist[i].isDirectory())
			{
				size = size + getlist(flist[i]);
				size--;
			}
		}
		return size;

	}
	
	public static String generalFileName() {
		return String.valueOf(System.currentTimeMillis()) + ".jpg";
	}
	public static String getDownloadDir(Context context) {
		File cacheDir = StorageUtils.getCacheDirectory(context, true);
		File individualDir = new File(cacheDir, "download");
		if (individualDir.exists() || individualDir.mkdir()) {
			cacheDir = individualDir;
		}
		return cacheDir.getAbsolutePath();
	}


}
