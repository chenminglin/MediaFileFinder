package com.bethena.mediafilefinder.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bethena.mediafilefinder.R;
import com.bethena.mediafilefinder.core.BaseActivity;
import com.bethena.mediafilefinder.searcher.ThreadCallBack;
import com.bethena.mediafilefinder.searcher.SearchAsyncTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

public class AsyncTaskPoolActivity extends BaseActivity {

    final static int THREAD_COUNT = 10;
    EditText mEditText;
    Button mBtnSearch;

    RecyclerView mListView;

    List<String> mDatas = new ArrayList<>();
    SearchResultAdapter mAdapter;

    TextView mTxtFindedCount;

    volatile List<SearchAsyncTask> mTasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_task_pool);


        mEditText = (EditText) findViewById(R.id.edt_keyword);
        mBtnSearch = (Button) findViewById(R.id.btn_search);
        mListView = (RecyclerView) findViewById(R.id.recycler_view);

        mTxtFindedCount = (TextView) findViewById(R.id.txt_finded_count);

        mListView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SearchResultAdapter(mDatas);
        mListView.setAdapter(mAdapter);


        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSearch();
            }
        });


    }

    void toSearch() {
        cancelAllTasks();
        mDatas.clear();
        mAdapter.notifyDataSetChanged();
        final String keyWord = mEditText.getText().toString();
        if (keyWord == null || keyWord.equals("")) {
            return;
        }
        File file = Environment.getExternalStorageDirectory();
        if (file.exists() && file.canRead()) {
            File[] childfiles = file.listFiles();

            int eachLength = childfiles.length / THREAD_COUNT;
            for (int n = 0; n < THREAD_COUNT; n++) {
                int start = n * eachLength;
                int end = (n + 1) * eachLength - 1;

                Timber.tag(TAG).d("start = " + start);
                Timber.tag(TAG).d("end = " + end);

                File[] tempFiles = Arrays.copyOfRange(childfiles, start, end);
                SearchAsyncTask searchAsyncTask = new SearchAsyncTask(new ThreadCallBack() {
                    @Override
                    public void onFinded(List<String> fileNames) {
                        int start = mDatas.size();
                        mDatas.addAll(fileNames);
                        int end = mDatas.size();
                        mAdapter.notifyItemRangeChanged(start, end);
                        mTxtFindedCount.setText(getString(R.string.finded_count, mDatas.size()));
                    }

                    @Override
                    public void onFinished(SearchAsyncTask task) {
                        if (task.getStatus() == AsyncTask.Status.FINISHED) {
                            mTasks.remove(task);
                        }
                    }

                }, keyWord);
                searchAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, tempFiles);
                mTasks.add(searchAsyncTask);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelAllTasks();
    }

    void cancelAllTasks() {
        for (SearchAsyncTask task : mTasks) {
            if (task.getStatus() != AsyncTask.Status.FINISHED && !task.isCancelled()) {
                task.cancel(true);
                task.setThreadCallBack(null);
            }
        }
        mTasks.clear();
    }
}
