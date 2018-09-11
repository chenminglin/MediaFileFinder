package com.bethena.mediafilefinder.searcher;

import android.os.AsyncTask;

import com.bethena.mediafilefinder.BuildConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class SearchAsyncTask extends AsyncTask<File, List<String>, Void> {
    final String TAG = Thread.currentThread().getName();
    ThreadCallBack mThreadCallBack;
    String mKeyWord;

    List<String> fileNames = new ArrayList<>();

    public SearchAsyncTask(ThreadCallBack threadCallBack, String mKeyWord) {
        this.mThreadCallBack = threadCallBack;
        this.mKeyWord = mKeyWord;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Timber.tag(TAG).d("onCancel....");
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (mThreadCallBack != null) {
            mThreadCallBack.onFinished(this);
        }
    }

    @Override
    protected Void doInBackground(File... files) {
        for (File file : files) {
            if (isCancelled()) {
                return null;
            }
            searchFileRecursive(file);
            if (isCancelled()) {
                return null;
            }
            publishProgress(fileNames);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(List<String>... values) {
        super.onProgressUpdate(values);
        if (mThreadCallBack != null) {
            synchronized (fileNames) {
                mThreadCallBack.onFinded(values[0]);
                fileNames.clear();
            }
        }
    }

    void searchFileRecursive(File file) {
        File[] childFiles = file.listFiles();
        if (isCancelled()) {
            return;
        }
        if (file.getName().contains(mKeyWord)) {
            addList(file.getName());
        }

        if (file.getAbsolutePath().contains("immomo") && BuildConfig.DEBUG) {//我的手机会导致崩溃，屏蔽
            return;
        }

        if (childFiles != null && childFiles.length > 0) {
            for (File childFile : childFiles) {
                if (isCancelled()) {
                    break;
                }
//                Timber.tag(TAG).d(childFile.getAbsolutePath());
                if (childFile.getName().contains(mKeyWord)) {
                    if (isCancelled()) {
                        break;
                    }
                    Timber.tag(TAG).d("find = " + childFile.getAbsolutePath());
                    addList(childFile.getName());
                }

                if (childFile.exists() && childFile.canRead()) {
                    if (childFile.isDirectory()) {
                        searchFileRecursive(childFile);
                    }
                }
            }
        }
    }

    public void setThreadCallBack(ThreadCallBack threadCallBack) {
        this.mThreadCallBack = threadCallBack;
    }

    void addList(String name) {
        synchronized (fileNames) {
            fileNames.add(name);
        }
    }

}
