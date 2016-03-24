package com.nicholasworkshop.download.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.nicholasworkshop.download.MainApplication;
import com.nicholasworkshop.download.R;
import com.nicholasworkshop.download.fragment.MainFragment;

import timber.log.Timber;

/**
 * Created by nickwph on 3/5/16.
 */
public class MainActivity extends Activity {

    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1243251;

    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((MainApplication) getApplication()).getComponent().inject(this);
        mainFragment = (MainFragment) getFragmentManager().findFragmentById(R.id.main_fragment);
        requestPermission();
    }

    @Override
    public void onBackPressed() {
        if (!mainFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Timber.e("Permission result returned");
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                new AlertDialog.Builder(this)
                        .setTitle("Request Permission Failed")
                        .setMessage("Permission to write to external storage is required to use this app. " +
                                "If this error persists, please go to the following location to re-enable it.\n\n" +
                                "    Settings > Apps > SimplyDownload > Permissions\n")
                        .setNeutralButton("Exit", null)
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                finish();
                            }
                        })
                        .create()
                        .show();
            }
        }
    }


    private void requestPermission() {
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            Timber.e("Permission " + permission + " is not granted!");
            ActivityCompat.requestPermissions(this, new String[]{permission}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        }
    }
}
