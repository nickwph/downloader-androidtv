package com.nicholasworkshop.downloader.tool;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.webkit.CookieManager;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloader;
import com.nicholasworkshop.downloader.utility.SimpleFileDownloaderListener;

import java.io.File;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

/**
 * Created by nickwph on 3/8/16.
 */
public class FileDownloadManager {

    private static final File STORAGE_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

    @Inject Context context;
    @Inject FileDownloader fileDownloader;

    @Inject
    public FileDownloadManager() {
        // injectable
    }

    public Observable<Progress> start(final String url, final String mimeType, String filename) {
        final File file = getDownloadPath(url, filename);
        Timber.i("initializing download for file " + file.getName());
        Timber.v("- url: " + url);
        Timber.v("- path: " + file.getAbsolutePath());
        return Observable.create(new Observable.OnSubscribe<Progress>() {
            @Override
            public void call(final Subscriber<? super Progress> subscriber) {
                final Progress progress = new Progress();
                progress.url = url;
                progress.path = file.getAbsolutePath();
                String cookie = CookieManager.getInstance().getCookie(url);
                BaseDownloadTask task = fileDownloader.create(url)
                        .setPath(file.getAbsolutePath())
                        .setListener(new SimpleFileDownloaderListener() {

                            @Override
                            protected void error(BaseDownloadTask task, Throwable e) {
                                subscriber.onError(e);
                            }

                            @Override
                            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                                progress.totalBytes = totalBytes;
                                progress.soFarBytes = soFarBytes;
                                subscriber.onNext(progress);
                            }

                            @Override
                            protected void completed(BaseDownloadTask task) {
                                progress.soFarBytes = progress.totalBytes;
                                subscriber.onNext(progress);
                                subscriber.onCompleted();
                            }
                        });
                if (cookie != null) {
                    task.addHeader("Cookie", CookieManager.getInstance().getCookie(url));
                }
                task.start();
            }
        });
    }

    public static class Progress {
        private String url;
        private String path;
        private int soFarBytes;
        private int totalBytes;

        public int getSoFarBytes() {
            return soFarBytes;
        }

        public int getTotalBytes() {
            return totalBytes;
        }

        public String getUrl() {
            return url;
        }

        public String getPath() {
            return path;
        }

    }

    private File getDownloadPath(String url, String filename) {
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
        return file;
    }
}
