package com.nicholasworkshop.download;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.liulishuo.filedownloader.FileDownloader;
import com.nicholasworkshop.download.tool.ReleaseTree;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import timber.log.Timber;

/**
 * Created by nickwph on 3/6/16.
 */
@Module
public class MainModule {

    private Context context;

    MainModule(Context context) {
        this.context = context.getApplicationContext();
    }

    @Provides
    @Singleton
    Context provideContext() {
        return context;
    }

    @Provides
    @Singleton
    Timber.Tree provideTimberTree() {
        return BuildConfig.DEBUG ? new Timber.DebugTree() : new ReleaseTree();
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        return new OkHttpClient();
    }

    @Provides
    @Singleton
    FileDownloader provideFileDownloader() {
        return FileDownloader.getImpl();
    }

    @Provides
    @Singleton
    Handler provideMainThreadHandler() {
        return new Handler(Looper.getMainLooper());
    }

    @Provides
    @Singleton
    CookieManager provideCookieManager() {
        return CookieManager.getInstance();
    }

    @Provides
    @Singleton
    @SuppressWarnings("deprecation")
    CookieSyncManager provideCookieSyncManager() {
        return CookieSyncManager.createInstance(context);
    }

    @Provides
    @Singleton
    WindowManager provideWindowManager() {
        return (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    @Provides
    @Singleton
    @Named("TravelingTypewriter")
    Typeface provideTravelingTypewriterTypeface() {
        return Typeface.createFromAsset(context.getAssets(), "traveling_typewriter.ttf");
    }
}
