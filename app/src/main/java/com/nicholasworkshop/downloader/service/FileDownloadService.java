package com.nicholasworkshop.downloader.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.webkit.CookieManager;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloader;
import com.nicholasworkshop.downloader.MainApplication;
import com.nicholasworkshop.downloader.utility.SimpleFileDownloaderListener;

import java.io.File;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import timber.log.Timber;

/**
 * Created by nickwph on 3/5/16.
 */
public class FileDownloadService extends IntentService {

    private static final File STORAGE_DIR = Environment.getExternalStorageDirectory();
    private static final String EXTRA_URL = "EXTRA_URL";
    private static final String EXTRA_MIMETYPE = "EXTRA_MIMETYPE";
    private static final String EXTRA_FILENAME = "EXTRA_FILENAME";

    @Inject FileDownloader fileDownloader;
    @Inject OkHttpClient okHttpClient;

    private String url;
    private String mimeType;
    private String filename;

    public static void newInstance(Context context, String url, String mimeType, String filename) {
        Intent intent = new Intent(context, FileDownloadService.class);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_MIMETYPE, mimeType);
        intent.putExtra(EXTRA_FILENAME, filename);
        context.startService(intent);
    }

    public FileDownloadService() {
        super(FileDownloadService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((MainApplication) getApplication()).getComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        url = intent.getStringExtra(EXTRA_URL);
        mimeType = intent.getStringExtra(EXTRA_MIMETYPE);
        filename = intent.getStringExtra(EXTRA_FILENAME);
        if (filename == null) {
            filename = Uri.parse(url).getLastPathSegment();
        }
        File file = new File(STORAGE_DIR, filename);
        for (int version = 2; file.exists(); version++) {
            Timber.i("file exits! filename=" + file.getName());
            int dotIndex = filename.lastIndexOf('.');
            if (dotIndex >= 0) {
                file = new File(STORAGE_DIR, filename.substring(0, dotIndex) + "-" + version + filename.substring(dotIndex));
            } else {
                file = new File(STORAGE_DIR, filename + version);
            }
        }
        Timber.i("initializing download for file " + file.getName());
        Timber.v("- url: " + url);
        Timber.v("- path: " + file.getAbsolutePath());
        fileDownloader.create(url)
                .setPath(file.getAbsolutePath())
                .addHeader("Cookie", CookieManager.getInstance().getCookie(url))
                .setListener(new FileDownloaderListener())
                .start();
    }

    private class FileDownloaderListener extends SimpleFileDownloaderListener {
        @Override
        protected void completed(BaseDownloadTask task) {
            super.completed(task);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            File file = new File(task.getPath());
            file.setReadable(true, false);
            intent.setDataAndType(Uri.fromFile(file), mimeType);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
