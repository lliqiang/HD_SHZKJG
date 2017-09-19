package com.hengda.shzkjg.m.down;




/**
 * Created by $ shiwei.bai on 2016/9/18.
 */

public interface LanguageContract {

    interface View extends BaseView<Presenter> {
        void showLanguage();

        void loadFailed();

        void progress(int soFarBytes, int totalBytes);

        void speed(int speed);

        void error();

        void completed();

        void connected();
    }

    interface Presenter extends BasePresenter {
        void loadLanguage();

        void cancleLoad();

        void checkDb();
    }
}
