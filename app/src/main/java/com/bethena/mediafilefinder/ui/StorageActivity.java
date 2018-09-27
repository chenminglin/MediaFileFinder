package com.bethena.mediafilefinder.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.os.EnvironmentCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.widget.Toast;

import com.bethena.mediafilefinder.R;
import com.bethena.mediafilefinder.core.BaseActivity;
import com.bethena.mediafilefinder.utils.FileUtil;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;

import timber.log.Timber;

import static android.os.Build.VERSION.SDK_INT;

public class StorageActivity extends BaseActivity {

    private static final String DEFAULT_FALLBACK_STORAGE_PATH = "/storage/sdcard0";
    public static final Pattern DIR_SEPARATOR = Pattern.compile("/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);

        File sdcard_filedir = Environment.getExternalStorageDirectory();//得到sdcard的目录作为一个文件对象
        printFileSpace(sdcard_filedir);
        File root_filedir = new File("/");
        printFileSpace(root_filedir);

        File extended_filedir = new File(getExtendedMemoryPath(this));
        printFileSpace(extended_filedir);

//        if(Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED)){
//            Environment.is
//            //sdcard状态是没有挂载的情况
//            Toast.makeText(mContext, "sdcard不存在或未挂载", Toast.LENGTH_SHORT).show();
//            return ;
//        }


        String romAvailableSize = FileUtil.getRomAvailableSize(this);
        Timber.tag(TAG).d("romAvailableSize = " + romAvailableSize);
        String SDAvailableSize = FileUtil.getSDAvailableSize(this);
        Timber.tag(TAG).d("SDAvailableSize = " + SDAvailableSize);
        String RomTotalSize = FileUtil.getRomTotalSize(this);
        Timber.tag(TAG).d("RomTotalSize = " + RomTotalSize);
        String RomAvailableSize = FileUtil.getRomAvailableSize(this);
        Timber.tag(TAG).d("RomAvailableSize = " + RomAvailableSize);
        String SDTotalSize = FileUtil.getSDTotalSize(this);
        Timber.tag(TAG).d("SDTotalSize = " + SDTotalSize);
    }

    protected void printFileSpace(File file) {
        long usableSpace = file.getUsableSpace();//获取文件目录对象剩余空间

        long totalSpace = file.getTotalSpace();
        Timber.tag(TAG).d("totalSpace = " + totalSpace);
        long freeSpace = file.getFreeSpace();
        //将一个long类型的文件大小格式化成用户可以看懂的M，G字符串
        String usableSpace_str = Formatter.formatFileSize(this, usableSpace);
        String totalSpace_str = Formatter.formatFileSize(this, totalSpace);
        String freeSpace_str = Formatter.formatFileSize(this, freeSpace);
//        if(usableSpace < 1024 * 1024 * 200){//判断剩余空间是否小于200M
//            Toast.makeText(mContext, "sdcard剩余空间不足,无法满足下载；剩余空间为："+usableSpace_str, Toast.LENGTH_SHORT).show();
//            return ;
//        }

        Timber.tag(TAG).d("usableSpace_str = " + usableSpace_str);
        Timber.tag(TAG).d("totalSpace_str = " + totalSpace_str);
        Timber.tag(TAG).d("freeSpace_str = " + freeSpace_str);
    }

    private static String getExtendedMemoryPath(Context mContext) {
        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (removable) {
                    return path;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized ArrayList<String> getStorageDirectories() {
        // Final set of paths
        final ArrayList<String> rv = new ArrayList<>();
        // Primary physical SD-CARD (not emulated)
        final String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
        // All Secondary SD-CARDs (all exclude primary) separated by ":"
        final String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
        // Primary emulated SD-CARD
        final String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
        if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
            // Device has physical external storage; use plain paths.
            if (TextUtils.isEmpty(rawExternalStorage)) {
                // EXTERNAL_STORAGE undefined; falling back to default.
                // Check for actual existence of the directory before adding to list
                if(new File(DEFAULT_FALLBACK_STORAGE_PATH).exists()) {
                    rv.add(DEFAULT_FALLBACK_STORAGE_PATH);
                } else {
                    //We know nothing else, use Environment's fallback
                    rv.add(Environment.getExternalStorageDirectory().getAbsolutePath());
                }
            } else {
                rv.add(rawExternalStorage);
            }
        } else {
            // Device has emulated storage; external storage paths should have
            // userId burned into them.
            final String rawUserId;
            if (SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                rawUserId = "";
            } else {
                final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                final String[] folders = DIR_SEPARATOR.split(path);
                final String lastFolder = folders[folders.length - 1];
                boolean isDigit = false;
                try {
                    Integer.valueOf(lastFolder);
                    isDigit = true;
                } catch (NumberFormatException ignored) {
                }
                rawUserId = isDigit ? lastFolder : "";
            }
            // /storage/emulated/0[1,2,...]
            if (TextUtils.isEmpty(rawUserId)) {
                rv.add(rawEmulatedStorageTarget);
            } else {
                rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
            }
        }
        // Add all secondary storages
        if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
            // All Secondary SD-CARDs splited into array
            final String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
            Collections.addAll(rv, rawSecondaryStorages);
        }
        if (SDK_INT >= Build.VERSION_CODES.M && checkStoragePermission())
            rv.clear();
        if (SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String strings[] = FileUtil.getExtSdCardPathsForActivity(this);
            for (String s : strings) {
                File f = new File(s);
                if (!rv.contains(s) && FileUtil.canListFiles(f))
                    rv.add(s);
            }
        }
//        if (isRootExplorer()){
//            rv.add("/");
//        }
        File usb = getUsbDrive();
        if (usb != null && !rv.contains(usb.getPath())) rv.add(usb.getPath());

//        if (SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            if (isUsbDeviceConnected()) rv.add(OTGUtil.PREFIX_OTG + "/");
//        }
        return rv;
    }

//    private boolean isUsbDeviceConnected() {
//        if (OTGUtil.isMassStorageDeviceConnected(this)) {
//            if(!SingletonUsbOtg.getInstance().hasRootBeenRequested()) {
//                SingletonUsbOtg.getInstance().setHasRootBeenRequested(false);
//                // we need to set this every time as there is no way to know that whether USB device was
//                // disconnected after closing the app and another one was connected in that case
//                // the URI will obviously change otherwise we could persist the URI even after
//                // reopening the app by not writing this preference when it's not null
//                SingletonUsbOtg.getInstance().setUsbOtgRoot(null);
//            }
//            return true;
//        } else {
//            SingletonUsbOtg.getInstance().setUsbOtgRoot(null);
//            return false;
//        }
//    }

    public boolean checkStoragePermission() {
        // Verify that all required contact permissions have been granted.
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    public File getUsbDrive() {
        File parent = new File("/storage");

        try {
            for (File f : parent.listFiles())
                if (f.exists() && f.getName().toLowerCase().contains("usb") && f.canExecute())
                    return f;
        } catch (Exception e) {}

        parent = new File("/mnt/sdcard/usbStorage");
        if (parent.exists() && parent.canExecute())
            return parent;
        parent = new File("/mnt/sdcard/usb_storage");
        if (parent.exists() && parent.canExecute())
            return parent;

        return null;
    }
}
