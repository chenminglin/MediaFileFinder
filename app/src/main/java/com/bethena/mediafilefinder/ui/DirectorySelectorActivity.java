package com.bethena.mediafilefinder.ui;

import android.content.Intent;
import android.os.Environment;
import android.os.Bundle;

import com.bethena.mediafilefinder.Constants;
import com.bethena.mediafilefinder.R;
import com.bethena.mediafilefinder.core.BaseActivity;
import com.bethena.mediafilefinder.utils.FileUtil;

import java.io.File;
import java.util.List;

import timber.log.Timber;

public class DirectorySelectorActivity extends BaseActivity {

    private final static String TAG = DirectorySelectorActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory);

        Intent intent = getIntent();
        File dir = null;

        if (intent != null) {

            if(intent.getExtras()!=null){
                CharSequence csDataDir = intent.getExtras().getCharSequence(Constants.KEY_PARAM1);
                if(csDataDir!=null){
                    String dataDir = csDataDir.toString();
                    dir = new File(dataDir);
                }
            }

        }

        if (dir == null) {
            dir = Environment.getExternalStorageDirectory();
        }

        if (dir.exists() && dir.canRead()) {
            dir.listFiles();


            List<File> parents = FileUtil.getAllParent(dir);

            Timber.tag(TAG).d("this dir is "+dir.getAbsolutePath());

            for (File file : parents) {
                Timber.tag(TAG).d(file.getAbsolutePath());
            }

        }
    }


}
