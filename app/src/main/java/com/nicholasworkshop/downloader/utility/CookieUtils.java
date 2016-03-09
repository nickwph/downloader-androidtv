package com.nicholasworkshop.downloader.utility;

import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by nickwph on 3/8/16.
 */
@SuppressWarnings("deprecation")
public class CookieUtils {

    @Inject CookieManager cookieManager;
    @Inject CookieSyncManager cookieSyncManager;

    @Inject
    public CookieUtils() {
        // injectable
    }

    public void clear() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            cookieManager.removeAllCookies(null);
            cookieManager.flush();
        } else {
            Timber.i("Clearing cookies with code from API<22");
            cookieSyncManager.startSync();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncManager.stopSync();
            cookieSyncManager.sync();
        }
    }
}
