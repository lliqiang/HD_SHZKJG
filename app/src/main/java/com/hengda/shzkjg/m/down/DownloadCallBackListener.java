package com.hengda.shzkjg.m.down;

public interface DownloadCallBackListener {
    void progress(int soFarBytes, int totalBytes);

    void speed(int speed);

    void error();

    void completed();

    void connected();
}