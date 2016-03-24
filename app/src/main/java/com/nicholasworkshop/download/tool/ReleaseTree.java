package com.nicholasworkshop.download.tool;

import android.util.Log;

import timber.log.Timber;

/**
 * Created by nickwph on 3/5/16.
 */
public class ReleaseTree extends Timber.Tree {
    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return;
        }

//        FakeCrashLibrary.log(priority, tag, message);
//
//        if (t != null) {
//            if (priority == Log.ERROR) {
//                FakeCrashLibrary.logError(t);
//            } else if (priority == Log.WARN) {
//                FakeCrashLibrary.logWarning(t);
//            }
//        }
    }
}
