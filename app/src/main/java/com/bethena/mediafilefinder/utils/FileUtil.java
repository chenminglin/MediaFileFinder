package com.bethena.mediafilefinder.utils;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.v4.provider.DocumentFile;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

import com.bethena.mediafilefinder.ui.MainActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    public final static int FILE_TYPE_OTHER = 0;
    public final static int FILE_TYPE_AUDIO = 1;
    public final static int FILE_TYPE_VIDEO = 2;
    public final static int FILE_TYPE_IMAGE = 3;
    public final static int FILE_TYPE_DIR = 4;


    private static final String INTERNAL_VOLUME = "internal";
    public static final String EXTERNAL_VOLUME = "external";

    private static final String EMULATED_STORAGE_SOURCE = System.getenv("EMULATED_STORAGE_SOURCE");
    private static final String EMULATED_STORAGE_TARGET = System.getenv("EMULATED_STORAGE_TARGET");
    private static final String EXTERNAL_STORAGE = System.getenv("EXTERNAL_STORAGE");

    public static int whatType(File file) {
        String fileType = DocumentFile.fromFile(file).getType();

        if (fileType.startsWith("audio/")) {
            return FILE_TYPE_AUDIO;
        } else if (fileType.startsWith("video/")) {
            return FILE_TYPE_VIDEO;
        } else if (fileType.startsWith("image/")) {
            return FILE_TYPE_IMAGE;
        }
        return 0;
    }


    public static List<File> getAllParent(File file) {
        List<File> parents = new ArrayList<>();
        getParentRecursive(file, parents);
        return parents;
    }

    private static void getParentRecursive(File file, List<File> parents) {
        File parent = file.getParentFile();

        if (parent != null) {
            parents.add(parent);
        }

        if (parent.getParent() != null) {
            getParentRecursive(parent, parents);
        }
    }

    public static void openFile(File f, Context c) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);

        String type = DocumentFile.fromFile(f).getType();
        if (type != null && type.trim().length() != 0 && !type.equals("*/*")) {
            Uri uri = fileToContentUri(c, f);
            if (uri == null) uri = Uri.fromFile(f);
            intent.setDataAndType(uri, type);
            try {
                c.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
//                openWith(f, c, useNewStack);
            }
        } else {
            // failed to load mime type
//            openWith(f, c, useNewStack);
        }
    }

    public static String normalizeMediaPath(String path) {
        if (TextUtils.isEmpty(EMULATED_STORAGE_SOURCE) ||
                TextUtils.isEmpty(EMULATED_STORAGE_TARGET) ||
                TextUtils.isEmpty(EXTERNAL_STORAGE)) {
            return path;
        }

        if (path.startsWith(EMULATED_STORAGE_SOURCE)) {
            path = path.replace(EMULATED_STORAGE_SOURCE, EMULATED_STORAGE_TARGET);
        }
        return path;
    }

    public static Uri fileToContentUri(Context context, File file) {
        // Normalize the path to ensure media search
        final String normalizedPath = normalizeMediaPath(file.getAbsolutePath());

        // Check in external and internal storages
        Uri uri = fileToContentUri(context, normalizedPath, EXTERNAL_VOLUME);
        if (uri != null) {
            return uri;
        }
        uri = fileToContentUri(context, normalizedPath, INTERNAL_VOLUME);
        if (uri != null) {
            return uri;
        }
        return null;
    }

    private static Uri fileToContentUri(Context context, String path, String volume) {
        final String where = MediaStore.MediaColumns.DATA + " = ?";
        Uri baseUri;
        String[] projection;
        int mimeType = whatType(new File(path));

        switch (mimeType) {
            case FILE_TYPE_IMAGE:
                baseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                projection = new String[]{BaseColumns._ID};
                break;
            case FILE_TYPE_VIDEO:
                baseUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                projection = new String[]{BaseColumns._ID};
                break;
            case FILE_TYPE_AUDIO:
                baseUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                projection = new String[]{BaseColumns._ID};
                break;
            default:
                baseUri = MediaStore.Files.getContentUri(volume);
                projection = new String[]{BaseColumns._ID, MediaStore.Files.FileColumns.MEDIA_TYPE};
        }

        ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(baseUri, projection, where, new String[]{path}, null);
        try {
            if (c != null && c.moveToNext()) {
                boolean isValid = false;
                if (mimeType == FILE_TYPE_IMAGE || mimeType == FILE_TYPE_VIDEO || mimeType == FILE_TYPE_AUDIO) {
                    isValid = true;
                } else {
                    int type = c.getInt(c.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE));
                    isValid = type != 0;
                }

                if (isValid) {
                    // Do not force to use content uri for no media files
                    long id = c.getLong(c.getColumnIndexOrThrow(BaseColumns._ID));
                    return Uri.withAppendedPath(baseUri, String.valueOf(id));
                }
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return null;
    }


    /**
     * 获得SD卡总大小	 * 	 * @return
     */
    public static String getSDTotalSize(Context context) {
        File path = Environment.getExternalStorageDirectory();

        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(context, blockSize * totalBlocks);
    }

    /**
     * 获得sd卡剩余容量，即可用大小	 * 	 * @return
     */
    public static String getSDAvailableSize(Context context) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(context, blockSize * availableBlocks);
    }

    /**
     * 获得机身内存总大小	 * 	 * @return
     */
    public static String getRomTotalSize(Context context) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(context, blockSize * totalBlocks);
    }

    /**
     * 获得机身可用内存	 * 	 * @return
     */
    public static String getRomAvailableSize(Context context) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(context, blockSize * availableBlocks);


    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String[] getExtSdCardPathsForActivity(Context context) {
        List<String> paths = new ArrayList<>();
        for (File file : context.getExternalFilesDirs("external")) {
            if (file != null) {
                int index = file.getAbsolutePath().lastIndexOf("/Android/data");
                if (index < 0) {
                    Log.w("Fileutil", "Unexpected external file dir: " + file.getAbsolutePath());
                } else {
                    String path = file.getAbsolutePath().substring(0, index);
                    try {
                        path = new File(path).getCanonicalPath();
                    } catch (IOException e) {
                        // Keep non-canonical path.
                    }
                    paths.add(path);
                }
            }
        }
        if (paths.isEmpty()) paths.add("/storage/sdcard1");
        return paths.toArray(new String[0]);
    }

    public static boolean canListFiles(File f) {
        return f.canRead() && f.isDirectory();
    }
}
