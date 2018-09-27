package com.bethena.mediafilefinder.ui;

import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bethena.mediafilefinder.R;
import com.bethena.mediafilefinder.core.BaseActivity;
import com.bethena.mediafilefinder.searcher.MyAsyncTask;

import java.io.File;
import java.util.Arrays;

import timber.log.Timber;

public class AsyncTaskFinderActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_task_finder);

        File file = Environment.getExternalStorageDirectory();

        if (file.exists() && file.canRead()) {
            File[] childFiles = file.listFiles();

            int eachLenght = childFiles.length / 5;
            Timber.tag(TAG).d("all size = " + childFiles.length);
            for (int n = 0; n < 5; n++) {

                int start = n * eachLenght;
                int end = ((n + 1) * eachLenght) - 1;

                Timber.tag(TAG).d("start = " + start);
                Timber.tag(TAG).d("end = " + end);

                File[] rangeFiles = Arrays.copyOfRange(childFiles,start,end);

                MyAsyncTask myAsyncTask = new MyAsyncTask();
                myAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, rangeFiles);
            }

        }


    }
}
