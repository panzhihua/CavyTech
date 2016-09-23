package com.cavytech.wear2.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by longjining on 16/4/21.
 */
public class FileUtils {
    /**
     * 删除单个文件
     * @param   filePath    被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String filePath) {

        try {
            if(!filePath.contains(File.separator)){
                filePath = getFilePath() + filePath;
            }
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                return file.delete();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 删除文件夹以及目录下的文件
     * @param   filePath 被删除目录的文件路径
     * @return  目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String filePath) {

        try {
            boolean flag = false;
            //如果filePath不以文件分隔符结尾，自动添加文件分隔符
            if (!filePath.endsWith(File.separator)) {
                filePath = filePath + File.separator;
            }
            File dirFile = new File(filePath);
            if (!dirFile.exists() || !dirFile.isDirectory()) {
                return false;
            }
            flag = true;
            File[] files = dirFile.listFiles();
            //遍历删除文件夹下的所有文件(包括子目录)
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    //删除子文件
                    flag = deleteFile(files[i].getAbsolutePath());
                    if (!flag) break;
                } else {
                    //删除子目录
                    flag = deleteDirectory(files[i].getAbsolutePath());
                    if (!flag) break;
                }
            }
            if (!flag) return false;
            //删除当前空目录
            return dirFile.delete();
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     *  根据路径删除指定的目录或文件，无论存在与否
     *@param filePath  要删除的目录或文件
     *@return 删除成功返回 true，否则返回 false。
     */
    public static boolean DeleteFolder(String filePath) {

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return false;
            } else {
                if (file.isFile()) {
                    // 为文件时调用删除文件方法
                    return deleteFile(filePath);
                } else {
                    // 为目录时调用删除目录方法
                    return deleteDirectory(filePath);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public static String getFilePath() {


        String serPath = "";
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                serPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Wear"+ File.separator;
            } else {
                serPath = Environment.getDataDirectory().getAbsolutePath() + File.separator + "Wear" + File.separator;
            }

            File serFile = new File(serPath);
            if (!serFile.exists()) {
                if (!serFile.isDirectory() && !serFile.mkdirs()) {
                    throw new IllegalAccessError(" cannot create folder");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return serPath;
    }

    public static boolean isFileExit(String fileName){
        try {
            if(!fileName.contains("/")){
                fileName = FileUtils.getFilePath() + fileName;
            }
            File file = new File(fileName);
            if (file.isFile() && file.exists()) {
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将Bitmap转换成字符串--Base64
     * @param bitmap
     * @return
     */
    public static String bitmaptoString(Bitmap bitmap) {

        String string = null;

        ByteArrayOutputStream bStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);

        byte[] bytes = bStream.toByteArray();

        string = Base64.encodeToString(bytes, Base64.DEFAULT);

        return string;
    }

    /**
     * 获取当前页面截图
     * @param activity
     * @return
     */
    public static Bitmap myShot(Activity activity) {
        // 获取windows中最顶层的view
        View view = activity.getWindow().getDecorView();
        view.buildDrawingCache();

        // 获取状态栏高度
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeights = rect.top;
        Display display = activity.getWindowManager().getDefaultDisplay();

        // 获取屏幕宽和高
        int widths = display.getWidth();
        int heights = display.getHeight();

        // 允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);

        // 去掉状态栏
        Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache(), 0,
                statusBarHeights, widths, heights - statusBarHeights);

        // 销毁缓存信息
        view.destroyDrawingCache();

        return bmp;
    }

    public static String getDrawableFilePath() {


        String serPath = "";
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                serPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Wear/Drawable"+ File.separator;
            } else {
                serPath = Environment.getDataDirectory().getAbsolutePath() + File.separator + "Wear/Drawable" + File.separator;
            }

            File serFile = new File(serPath);
            if (!serFile.exists()) {
                if (!serFile.isDirectory() && !serFile.mkdirs()) {
                    throw new IllegalAccessError(" cannot create folder");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return serPath;
    }

    public static void saveMyBitmap(String bitName, Bitmap mBitmap){
        File f = new File(getDrawableFilePath()+"/" + bitName + ".png");
        try {
            f.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e("TAG","在保存图片时出错："+e.toString());
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
