package com.nicholasworkshop.download;

import android.app.Application;

import com.liulishuo.filedownloader.FileDownloader;

import timber.log.Timber;

/**
 * Created by nickwph on 3/5/16.
 */
public class MainApplication extends Application {

    private MainComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerMainComponent.builder().mainModule(new MainModule(this)).build();
        Timber.plant(component.getTimberTree());
        FileDownloader.init(this);
    }

    public MainComponent getComponent() {
        return component;
    }
}
