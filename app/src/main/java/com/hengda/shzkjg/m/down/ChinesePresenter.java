package com.hengda.shzkjg.m.down;

import com.hengda.shzkjg.m.base.App;
import com.hengda.shzkjg.m.base.AppConfig;
import com.hengda.shzkjg.m.tool.NetUtil;

/**
 * Created by lenovo on 2016/10/22.
 */
public class ChinesePresenter implements LanguageContract.Presenter, DownloadCallBackListener {
    private LanguageContract.View mLaugnageView;
    String wifiSSID = NetUtil.getWifiSSID(App.Companion.getInstance());
    //内网地址
    private String path;
    FileLoader loader;

    public ChinesePresenter(LanguageContract.View mLaugnageView) {
        this.mLaugnageView = mLaugnageView;
        mLaugnageView.setPresenter(this);
    }

    @Override
    public void progress(int soFarBytes, int totalBytes) {
        mLaugnageView.progress(soFarBytes, totalBytes);

    }

    @Override
    public void speed(int speed) {
        mLaugnageView.speed(speed);
    }

    @Override
    public void error() {
        mLaugnageView.error();
    }

    @Override
    public void completed() {
        mLaugnageView.completed();
    }

    @Override
    public void connected() {
        mLaugnageView.connected();
    }

    @Override
    public void loadLanguage() {

    }

    @Override
    public void cancleLoad() {

    }

    @Override
    public void checkDb() {
        loader = new FileLoader(this);
        String fileName = "CHINESE";
        String zipPath = AppConfig.Companion.getDefaultFileDir();
        if (NetUtil.isConnected(App.Companion.getInstance())) {
            if (NetUtil.isWifi(App.Companion.getInstance())) {
                if (wifiSSID.contains(AppConfig.DEFAULT_SSID)) {
                    path = "http://" + AppConfig.DEFAULT_IP_PORT_I + "/HD_SHZBWG_RES/Hd_SHZBWG_res.zip";
                } else {
                    path = "http://47.93.81.30/HD_down/Hd_SHZBWG_res.zip ";
                }
            } else {
                path = "http://47.93.81.30/HD_down/Hd_SHZBWG_res.zip ";
            }
            loader.startDownload(path, fileName, zipPath);
        }


    }
}
