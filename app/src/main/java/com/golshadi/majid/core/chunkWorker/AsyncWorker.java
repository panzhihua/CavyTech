package com.golshadi.majid.core.chunkWorker;

import android.os.Build;

import com.golshadi.majid.Utils.helper.FileUtils;
import com.golshadi.majid.database.elements.Chunk;
import com.golshadi.majid.database.elements.Task;
import com.tunshu.util.LogUtil;
import java.io.BufferedOutputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by Majid Golshadi on 4/14/2014.
 */
public class AsyncWorker extends Thread {

    private final int BUFFER_SIZE = 1024;

    private final Task task;
    private final Chunk chunk;
    private final Moderator observer;
    private byte[] buffer;
    private ConnectionWatchDog watchDog = null;
    private Boolean isInterrupted = false;
    public boolean stop = false;
    private HttpURLConnection connection = null;

    public AsyncWorker(Task task, Chunk chunk, Moderator moderator) {
        buffer = new byte[BUFFER_SIZE];
        this.task = task;
        this.chunk = chunk;
        this.observer = moderator;
    }

    public void setInterrupt() {

        isInterrupted = true;
        flag = false;
    }

    @Override
    public void run() {

        try {

            String strUrl = task.url;
            // 有时候会去下载应用宝，尝试把http修改成https
          //  strUrl = strUrl.replace("http://", "https://");
            URL url = new URL(strUrl);

            connection = (HttpURLConnection) url.openConnection();
            // 为HttpUrlConnection设置connectTimeout属性以防止连接被阻塞
            connection.setConnectTimeout(10 * 1000);
            connection.setReadTimeout(10 * 1000);

            // 开始下载的位置为本地已经下载文件的大小
            File cf = new File(FileUtils.address(task.save_address, String.valueOf(chunk.id)));
            chunk.begin = cf.length();

            if(chunk.end == 0){
                downerror();
                return;
            }

            // Range参数中的文件位置是从0开始，最大值是文件长度减1。
            if (chunk.end != 0) // support unresumable links
                connection.setRequestProperty("Range", "bytes=" + chunk.begin + "-" + (chunk.end - 1));

            connection.connect();

            InputStream remoteFileIn = connection.getInputStream();
            FileOutputStream chunkFile = new FileOutputStream(cf, true);

            double length = task.size;
            long count = 0;
            int len = 0;
            double minPercent = 0.0;
            // set watchDoger to stop thread after 1sec if no connection lost
            watchDog = new ConnectionWatchDog(10 * 1000, this);
            watchDog.start();

            BufferedOutputStream outputStream = new BufferedOutputStream(chunkFile);

            while ((len = remoteFileIn.read(buffer)) > 0) {

                // read超时回来后可能已经被停止线程了
                if(this.isInterrupted()){
                    break;
                }
                watchDog.reset();
              //  chunkFile.write(buffer, 0, len);

                outputStream.write(buffer, 0, len);
                outputStream.flush();

                setDownloadLength(len);
                count += len;
                double percent = count / length;
                if (percent >= minPercent || minPercent == 0.0) {
                    minPercent += 0.01;
                    newProcess();
                }
            }
            // 暂停后或完成后不再计算超时
            flag = false;
            remoteFileIn.close();
            chunkFile.close();
            outputStream.close();

            LogUtil.getLogger().e(this.isInterrupted());
            if (Build.VERSION.SDK_INT >= 21) {
                if (!isInterrupted) {
                    observer.rebuild(chunk);
                }
            } else {
                if (!this.isInterrupted()) {
                    observer.rebuild(chunk);
                }
            }


        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            downerror();

        } catch (MalformedURLException e) {
            e.printStackTrace();
            downerror();
        } catch (IOException e) {
            e.printStackTrace();
            downerror();
        } finally {
            if(watchDog != null)
            {
                watchDog.interrupt();
            }

            if(connection != null)
            {
                connection.disconnect();
            }

        }

        return;
    }

    private void downerror(){
        observer.connectionLost(task.id);
        puaseRelatedTask();
    }

    private void process(int read) {
        observer.process(chunk.task_id, read);
    }

    private void newProcess() {
        observer.newProcess(chunk.task_id);
    }

    private void setDownloadLength(int read) {
        observer.setDownloadLength(chunk.task_id, read);
    }

    private void puaseRelatedTask() {

        observer.pause(task.id);
    }

    private boolean flag = true;

    public void connectionTimeOut() {
        if (flag) {
            watchDog.interrupt();
            flag = false;
            observer.connectionLost(task.id);
            puaseRelatedTask();
        }

    }

}
