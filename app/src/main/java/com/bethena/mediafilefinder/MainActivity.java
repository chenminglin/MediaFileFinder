package com.bethena.mediafilefinder;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.EnvironmentCompat;
import android.support.v4.provider.DocumentFile;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bethena.mediafilefinder.utils.FileUtil;
import com.bethena.mediafilefinder.utils.InputUtil;
import com.bethena.mediafilefinder.viewmodel.FileViewModel;

import org.reactivestreams.Publisher;

import java.io.File;
import java.security.Permissions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    final static String TAG = MainActivity.class.getSimpleName();

    EditText mEdtPath;
    RecyclerView mRecyclerView;

    ContentLoadingProgressBar mProgressBar;

    String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};

    final static int REQUEST_CODE_PERMISSION = 0x1001;

    final static int FILE_MIN_UNIT = 900 * 1024;


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            List<FileViewModel> files = (List<FileViewModel>) msg.obj;
            mRecyclerView.setAdapter(new FileListAdapter(files));
            mRecyclerView.setVisibility(View.VISIBLE);
            mProgressBar.hide();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, R.string.tip_reinstall, Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSION);
            }
        }

        mEdtPath = findViewById(R.id.edit_path);
        mEdtPath.setText("/storage/emulated/0/immomo/users/106373217/feedvideo/");

        mRecyclerView = findViewById(R.id.rv_filelist);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mProgressBar = findViewById(R.id.progress);
        mProgressBar.hide();


        Timber.tag(TAG).d("Environment.getDataDirectory() = %s", Environment.getDataDirectory().toString());
        Timber.tag(TAG).d("Environment.getExternalStorageState() = %s", Environment.getExternalStorageState().toString());
        Timber.tag(TAG).d("Environment.getDownloadCacheDirectory() = %s", Environment.getDownloadCacheDirectory().toString());
        Timber.tag(TAG).d("Environment.getExternalStorageDirectory() = %s", Environment.getExternalStorageDirectory().toString());
        Timber.tag(TAG).d("Environment.getRootDirectory() = %s", Environment.getRootDirectory().toString());

        Button btnSearch = (Button) findViewById(R.id.btn_search);
        File file=new File( Environment.getExternalStorageDirectory().getAbsolutePath());
        for (File file1 : file.listFiles()) {
            Log.i("zuoyuan",file1.getAbsolutePath());
        }

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                String inputPath = mEdtPath.getText().toString();
                final File file = new File(inputPath);

                Timber.tag(TAG).d(TAG, "canRead = " + file.canRead());

                if (file.exists() && file.canRead()) {
                    if (file.isDirectory()) {
                        String[] arrayFile = file.list();
                        if (arrayFile != null && arrayFile.length > 0) {
                            mRecyclerView.setVisibility(View.GONE);
                            mProgressBar.show();
                            InputUtil.hideKeyboard(mEdtPath);
                            new HandlerThread("hello") {
                                @Override
                                protected void onLooperPrepared() {
                                    super.onLooperPrepared();
                                    List<File> files = findFileByDir(file);
                                    List<FileViewModel> fileViewModels = new ArrayList<>();

                                    for (File f : files) {
                                        FileViewModel viewModel = new FileViewModel();
                                        viewModel.filePath = f.getPath();
                                        viewModel.fileType = DocumentFile.fromFile(f).getType();
                                        fileViewModels.add(viewModel);
                                    }

                                    Message message = mHandler.obtainMessage();
                                    message.obj = fileViewModels;
                                    mHandler.sendMessage(message);
                                }
                            }.start();

                        } else {
                            Toast.makeText(MainActivity.this, R.string.tip_nodata, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, R.string.tip_isnot_dir, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, R.string.tip_dir_cannot_read, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public List<File> findFileByDir(File file) {
        if (file.exists() && file.canRead()) {
            file.isDirectory();
            File[] files = new File[0];
                Timber.tag(TAG).d(file.getAbsolutePath());
                files = file.listFiles();

            if (files.length > 0) {
                List<File> fileList = new ArrayList<>();
                for (File childFile : files) {
                    Timber.tag(TAG).d(childFile.getAbsolutePath() +" = "+DocumentFile.fromFile(childFile).getType());
                    if (childFile.isDirectory()) {
                        fileList.addAll(findFileByDir(childFile));
                    } else {
                        if (childFile.length() > FILE_MIN_UNIT || FileUtil.whatType(childFile) != FileUtil.FILE_TYPE_OTHER) {
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


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
