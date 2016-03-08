package com.nicholasworkshop.downloader.activity;

import android.app.Activity;
import android.os.Bundle;

import com.nicholasworkshop.downloader.MainApplication;
import com.nicholasworkshop.downloader.R;

/**
 * Created by nickwph on 3/5/16.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((MainApplication)getApplication()).getComponent().inject(this);
    }
}
