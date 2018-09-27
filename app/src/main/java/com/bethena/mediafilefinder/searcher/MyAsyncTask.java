package com.bethena.mediafilefinder.searcher;

import android.os.AsyncTask;
import android.support.v4.provider.DocumentFile;

import com.bethena.mediafilefinder.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class MyAsyncTask extends AsyncTask<File, Integer, Void> {

    final String TAG = Thread.currentThread().getName();

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    @Override
    protected Void doInBackground(File... files) {

        for (File file : files) {
            findFileByDir(file);
        }

        return null;
    }

    public List<File> findFileByDir(File file) {
        if (file.exists() && file.canRead()) {
            file.isDirectory();
            File[] files;
            Timber.tag(TAG).d(file.getAbsolutePath());
            files = file.listFiles();

            if (files != null && files.length > 0) {
                List<File> fileList = new ArrayList<>();
                for (File childFile : files) {
                    Timber.tag(TAG).d(childFile.getAbsolutePath() + " = " + DocumentFile.fromFile(childFile).getType());
                    if (childFile.isDirectory()) {
                        fileList.addAll(findFileByDir(childFile));
                    } else {
                        if (childFile.length() > 9000000 || FileUtil.whatType(childFile) != FileUtil.FILE_TYPE_OTHER) {
                            fileList.add(childFile);
                        }
                    }
                }
                return fileList;
            }
        }
        return new ArrayList<>();
    }


    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled(Void aVoid) {
        super.onCancelled(aVoid);
    }
}
