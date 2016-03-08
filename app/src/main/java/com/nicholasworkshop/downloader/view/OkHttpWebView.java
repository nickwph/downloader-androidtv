package com.nicholasworkshop.downloader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * Created by nickwph on 3/5/16.
 */
public class OkHttpWebView extends WebView {

    public OkHttpWebView(Context context) {
        this(context, null);
    }

    public OkHttpWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OkHttpWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public OkHttpWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
