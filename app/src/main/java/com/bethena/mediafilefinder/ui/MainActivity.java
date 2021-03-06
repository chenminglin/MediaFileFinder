package com.bethena.mediafilefinder.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.provider.DocumentFile;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bethena.mediafilefinder.BuildConfig;
import com.bethena.mediafilefinder.R;
import com.bethena.mediafilefinder.core.BaseActivity;
import com.bethena.mediafilefinder.utils.FileUtil;
import com.bethena.mediafilefinder.utils.InputUtil;
import com.bethena.mediafilefinder.viewmodel.FileItemViewModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class MainActivity extends BaseActivity {

    final static String TAG = MainActivity.class.getSimpleName();

    EditText mEdtPath;

    ContentLoadingProgressBar mProgressBar;


    FrameLayout mFragmentContainer;

    final static int FILE_MIN_UNIT = 900 * 1024;

    /**
     * 记录所有符合条件的目录，key为路径
     */
    Map<String, FileItemViewModel> mPathFolderMap = new HashMap<>();


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            FileItemViewModel fileItemViewModel = (FileItemViewModel) msg.obj;

            nextFolder(fileItemViewModel);

            mProgressBar.hide();
            mFragmentContainer.setVisibility(View.VISIBLE);
        }
    };

    public void nextFolder(FileItemViewModel fileItemViewModel) {
        if (fileItemViewModel != null && fileItemViewModel.getChildFiles() != null
                && fileItemViewModel.getChildFiles().size() != 0) {

            FindedFragment findedFragment = FindedFragment.newInstance(fileItemViewModel);
            nextFolder(findedFragment);
        }
    }

    public void nextFolder(FindedFragment fragment) {
        replaceFragment(R.id.fragment_container, fragment);
    }

    public void toFolder(File file) {
        FileItemViewModel fileItemViewModel = mPathFolderMap.get(file.getAbsolutePath());
        nextFolder(fileItemViewModel);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFragmentContainer = (FrameLayout) findViewById(R.id.fragment_container);

        mEdtPath = (EditText) findViewById(R.id.edit_path);
//        mEdtPath.setText(Environment.getExternalStorageDirectory().toString());


        mProgressBar = (ContentLoadingProgressBar) findViewById(R.id.progress);
        mProgressBar.hide();


        Timber.tag(TAG).d("Environment.getDataDirectory() = %s", Environment.getDataDirectory().toString());
        Timber.tag(TAG).d("Environment.getExternalStorageState() = %s", Environment.getExternalStorageState().toString());
        Timber.tag(TAG).d("Environment.getDownloadCacheDirectory() = %s", Environment.getDownloadCacheDirectory().toString());
        Timber.tag(TAG).d("Environment.getExternalStorageDirectory() = %s", Environment.getExternalStorageDirectory().toString());
        Timber.tag(TAG).d("Environment.getRootDirectory() = %s", Environment.getRootDirectory().toString());

        mEdtPath.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    doSearch();
//                    try {
//                        doSearch2();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    return true;
                }
                return false;
            }
        });

        Button btnSearch = (Button) findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                doSearch();
            }
        });

//        Button btnChooseFolder = (Button) findViewById(R.id.btn_choose_folder);
//        btnChooseFolder.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, DirectorySelectorActivity.class);
//                startActivity(intent);
//            }
//        });

//        FileUtil.openFile(new File("/storage/emulated/0/Android/media/com.ruguoapp.jike/jikeImg/jike_706241142723267_pic.jpeg"), this);

        Button buttonStorage = (Button) findViewById(R.id.btn_storage);
        buttonStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,StorageActivity.class);
                startActivity(intent);
            }
        });
    }

    private void doSearch() {
        String inputPath = mEdtPath.getText().toString();
        final File file = new File(Environment.getExternalStorageDirectory().toString() + File.separator + inputPath.trim());
//        Timber.tag(TAG).d(TAG, "canRead = " + file.canRead());
        if (file.exists() && file.canRead()) {
            if (file.isDirectory()) {
                String[] arrayFile = file.list();
                if (arrayFile != null && arrayFile.length > 0) {
                    mFragmentContainer.setVisibility(View.GONE);
                    mProgressBar.show();
                    InputUtil.hideKeyboard(mEdtPath);

                    new HandlerThread("hello") {
                        @Override
                        protected void onLooperPrepared() {
                            super.onLooperPrepared();
                            FileItemViewModel fileItemViewModel = new FileItemViewModel();
                            fileItemViewModel.setCurrentFile(file);
                            List<FileItemViewModel> findedItemViewModels = findFileByDir(fileItemViewModel);
                            fileItemViewModel.setChildFiles(findedItemViewModels);

                            mPathFolderMap.put(file.getAbsolutePath(), fileItemViewModel);

                            Message message = mHandler.obtainMessage();
                            message.obj = fileItemViewModel;
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


    protected void doSearch2() throws IOException {
        String inputPath = mEdtPath.getText().toString();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            String targetPath = Environment.getExternalStorageDirectory().toString() + File.separator + inputPath.trim();
//            URI uri = URI.create("file:///" + targetPath);
//            Path path = Paths.get(uri);
//            DirectoryStream<Path> streams = Files.newDirectoryStream(path);
//            for (Path path1 : streams) {
//                Timber.tag(TAG).d(path1.toString());
//            }
//        }


    }

    /*public List<File> findFileByDir(File file) {
        if (file.exists() && file.canRead()) {
            file.isDirectory();
            File[] files;
            Timber.tag(TAG).d(file.getAbsolutePath());
            files = file.listFiles();

            if (files.length > 0) {
                List<File> fileList = new ArrayList<>();
                for (File childFile : files) {
                    Timber.tag(TAG).d(childFile.getAbsolutePath() + " = " + DocumentFile.fromFile(childFile).getType());
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
    }*/


    public List<FileItemViewModel> findFileByDir(FileItemViewModel fileItemViewModel) {
        File currentFile = fileItemViewModel.getCurrentFile();
        if (currentFile.exists() && currentFile.canRead()) {
            if (currentFile.getAbsolutePath().contains("immomo") && BuildConfig.DEBUG) {//我的手机会导致崩溃，屏蔽
                return new ArrayList<>();
            }
            File[] files;
//            Timber.tag(TAG).d(currentFile.getAbsolutePath());
            files = currentFile.listFiles();

            if (files.length > 0) {
                List<FileItemViewModel> childFileViewModels = new ArrayList<>();
                for (File childFile : files) {
//                    Timber.tag(TAG).d(childFile.getAbsolutePath() + " = " + DocumentFile.fromFile(childFile).getType());
                    if (childFile.isDirectory()) {
                        FileItemViewModel childFileViewModel = new FileItemViewModel();
                        childFileViewModel.setCurrentFile(childFile);
                        List<FileItemViewModel> findedFiles = findFileByDir(childFileViewModel);
                        if (findedFiles.size() > 0) {
                            childFileViewModel.setChildFiles(findedFiles);
                            childFileViewModel.setIntFileType(FileUtil.FILE_TYPE_DIR);
                            childFileViewModels.add(childFileViewModel);
                            mPathFolderMap.put(currentFile.getAbsolutePath(), fileItemViewModel);
                        }
                    } else {
                        int fileType = FileUtil.whatType(childFile);
                        if (childFile.length() > FILE_MIN_UNIT || fileType == FileUtil.FILE_TYPE_VIDEO) {
                            FileItemViewModel viewModel = new FileItemViewModel();
                            viewModel.setCurrentFile(childFile);
                            viewModel.setIntFileType(fileType);
                            viewModel.setFileType(DocumentFile.fromFile(childFile).getType());
                            childFileViewModels.add(viewModel);
                        }
                    }
                }
                return childFileViewModels;
            }
        }
        return new ArrayList<>();
    }


    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
            return;
        } else {
            finish();
        }

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
