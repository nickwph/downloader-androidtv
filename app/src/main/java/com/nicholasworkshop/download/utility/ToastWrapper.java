package com.nicholasworkshop.download.utility;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import javax.inject.Inject;

/**
 * Created by nickwph on 3/8/16.
 */
public class ToastWrapper {

    @Inject Context context;
    @Inject Handler handler;

    public void showText(final String msg) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void showText(final int resourceId) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, context.getString(resourceId), Toast.LENGTH_LONG).show();
            }
        });
    }
}
