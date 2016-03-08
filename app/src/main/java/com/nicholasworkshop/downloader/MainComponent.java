package com.nicholasworkshop.downloader;

import com.nicholasworkshop.downloader.activity.MainActivity;
import com.nicholasworkshop.downloader.fragment.MainFragment;
import com.nicholasworkshop.downloader.service.FileDownloadService;

import javax.inject.Singleton;

import dagger.Component;
import timber.log.Timber;

/**
 * Created by nickwph on 3/6/16.
 */
@Singleton
@Component(modules = MainModule.class)
public interface MainComponent {

    Timber.Tree getTimberTree();

    void inject(MainActivity mainActivity);

    void inject(MainFragment mainFragment);

    void inject(FileDownloadService fileDownloadService);
}
