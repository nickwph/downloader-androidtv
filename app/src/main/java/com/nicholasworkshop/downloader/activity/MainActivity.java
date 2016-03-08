package com.nicholasworkshop.downloader.activity;

import android.app.Activity;
import android.os.Bundle;

import com.nicholasworkshop.downloader.MainApplication;
import com.nicholasworkshop.downloader.R;
import com.nicholasworkshop.downloader.fragment.MainFragment;

/**
 * Created by nickwph on 3/5/16.
 */
public class MainActivity extends Activity {

    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((MainApplication) getApplication()).getComponent().inject(this);
        mainFragment = (MainFragment) getFragmentManager().findFragmentById(R.id.main_fragment);
    }

    @Override
    public void onBackPressed() {
        if (!mainFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
