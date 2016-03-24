package com.nicholasworkshop.download.utility;

import android.annotation.SuppressLint;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;

import timber.log.Timber;

/**
 * Created by nickwph on 3/6/16.
 */
public class SimpleFileDownloaderListener extends FileDownloadListener {

    @Override
    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        Timber.v("task=" + task + " soFarBytes=" + soFarBytes + " totalBytes=" + totalBytes);
    }

    @Override
    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
        Timber.v("task=" + task + " etag=" + etag + " isContinue=" + isContinue + " soFarBytes=" + soFarBytes + " totalBytes=" + totalBytes);
    }

    @Override
    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        Timber.v("task=" + task + " soFarBytes=" + soFarBytes + " totalBytes=" + totalBytes);
    }

    @Override
    protected void blockComplete(BaseDownloadTask task) {
        Timber.v("task=" + task);
    }

    @Override
    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
        Timber.v("task=" + task + " ex=" + ex + " retryingTimes=" + retryingTimes + " soFarBytes=" + soFarBytes);
    }

    @Override
    @SuppressLint("SetWorldReadable")
    protected void completed(BaseDownloadTask task) {
        Timber.v("completed task=" + task);
    }

    @Override
    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        Timber.v("paused task=" + task + " soFarBytes=" + soFarBytes + " totalBytes=" + totalBytes);
    }

    @Override
    protected void error(BaseDownloadTask task, Throwable e) {
        Timber.v("error task=" + task + " e=" + e);
        e.printStackTrace();
    }

    @Override
    protected void warn(BaseDownloadTask task) {
        Timber.v("warn task=" + task);
    }
}
