package com.bethena.mediafilefinder.core;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.bethena.mediafilefinder.Constants;
import com.bethena.mediafilefinder.R;


public class BaseActivity extends AppCompatActivity {
    protected final String TAG = getClass().getSimpleName();

    protected final String TAG = getClass().getSimpleName();

    String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPermission();
    }

    protected void initPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, R.string.tip_reinstall, Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, permissions, Constants.REQUEST_CODE_PERMISSION);
            }
        }
    }

    protected void replaceFragment(int containerId, Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(containerId, fragment)
                .addToBackStack(fragment.getClass().getSimpleName())
                .commitAllowingStateLoss();
    }
}
