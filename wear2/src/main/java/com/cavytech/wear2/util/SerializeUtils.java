package com.cavytech.wear2.util;

import org.xutils.common.util.FileUtil;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializeUtils {
    private static ObjectOutputStream out;
    private static  ObjectInputStream in;

    /**
     * 序列化
     *
     * @param object
     * @param fileName  文件名
     * @return
     */
    public static void serialize(Object object, String fileName) {
        if(object==null){
            return;
        }
        try {
            //需要一个文件输出流和对象输出流；文件输出流用于将字节输出到文件，对象输出流用于将对象输出为字节
            out = new ObjectOutputStream(new FileOutputStream(FileUtils.getFilePath() + fileName));
            out.writeObject(object);

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 反序列化
     *
     * @param fileName  文件名
     * @return Object
     */
    public static Object unserialize(String fileName) {
        if(!FileUtils.isFileExit(fileName)){
            return null;
        }
        try {
             in = new ObjectInputStream(new FileInputStream(FileUtils.getFilePath() + fileName));
            Object object = in.readObject();

            return object;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
