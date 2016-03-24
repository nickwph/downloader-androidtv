package com.nicholasworkshop.download.utility;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import javax.inject.Inject;

/**
 * Created by nickwph on 3/8/16.
 */
public class DisplayUtils {

    @Inject Context context;
    @Inject WindowManager windowManager;

    @Inject
    public DisplayUtils() {
        // injectable
    }

    public int getPixelFromDp(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public Point getDisplaySize() {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }
}
