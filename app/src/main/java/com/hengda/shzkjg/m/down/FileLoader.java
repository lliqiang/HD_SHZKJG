package com.hengda.shzkjg.m.down;

import com.hengda.shzkjg.m.base.AppConfig;
import com.hengda.shzkjg.m.tool.ZipUtil;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;



/**
 * Created by baishiwei on 2016/6/28.
 */
public class FileLoader {
    //下载接口
    private DownloadCallBackListener listener;
    private int downloadId;

    public FileLoader(DownloadCallBackListener listener) {
        this.listener = listener;
    }
//
    public void startDownload(String url, String fileName, String zipPath) {
        //开始下载
        downloadId = downloadTask(url, fileName, zipPath).start();
    }
//取消下载
    public void cancleDownload() {
        FileDownloader.getImpl().pause(downloadId);
    }



    public BaseDownloadTask downloadTask(String url, String fileName, final String zipPath) {
        //创建一个temp的文件·路径 CalabashILand/temp/
        final String path = AppConfig.Companion.getDefaultFileDir() + "temp";
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        final File file = new File(dir, fileName + ".zip");
        return FileDownloader.getImpl().create(url)
                //把fileName.zip的压缩文件放到path路径下
                .setPath(path + File.separator + fileName + ".zip")
                .setCallbackProgressTimes(300)
                .setMinIntervalUpdateSpeed(400)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        listener.connected();
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        listener.progress(soFarBytes, totalBytes);
                        listener.speed(task.getSpeed());
                    }
                    @Override
                    protected void blockComplete(BaseDownloadTask task) {
                    }

                    @Override
                    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        //下载完成以后
//                        1、创建文件路径为zipPath的下载路径
                        File temp_dir = new File(zipPath);
                        if (!temp_dir.exists()) {
                            temp_dir.mkdirs();
                        }
                        //解压已经下载好的文件
                        unzipFile(file, zipPath);
                        listener.completed();
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {

                        listener.error();
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                    }
                });
    }

    private void unzipFile(File file, String path) {
        try {
            //从path路径下解压文件并存放到file文件夹中
            ZipUtil.unZipFolder(file.getAbsolutePath(), path);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if (file.exists()) {
//            file.delete();
//        }
    }
}
