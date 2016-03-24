package com.nicholasworkshop.download;

import com.nicholasworkshop.download.activity.MainActivity;
import com.nicholasworkshop.download.fragment.MainFragment;

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
}
