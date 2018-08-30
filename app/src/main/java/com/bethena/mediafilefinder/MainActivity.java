package com.bethena.mediafilefinder;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.os.EnvironmentCompat;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    final static String TAG = MainActivity.class.getSimpleName();

    EditText mEdtPath;
    RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        mEdtPath = findViewById(R.id.edit_path);
        mEdtPath.setText("/storage/emulated/0/qqmusic");

        mRecyclerView = findViewById(R.id.rv_filelist);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        Timber.d("Environment.getDataDirectory() = %s", Environment.getDataDirectory().toString());
        Timber.d("Environment.getExternalStorageState() = %s", Environment.getExternalStorageState().toString());
        Timber.d("Environment.getDownloadCacheDirectory() = %s", Environment.getDownloadCacheDirectory().toString());
        Timber.d("Environment.getExternalStorageDirectory() = %s", Environment.getExternalStorageDirectory().toString());
        Timber.d("Environment.getRootDirectory() = %s", Environment.getRootDirectory().toString());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String inputPath = mEdtPath.getText().toString();

                File file = new File("/");

                Log.d(TAG, "canRead = " + file.canRead());

                if (file.exists()) {
                    String[] arrayFile = file.list();
                    List<String> files = Arrays.asList(arrayFile);
                    mRecyclerView.setAdapter(new FileListAdapter(files));
                } else {

                }

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
