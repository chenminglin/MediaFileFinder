package com.bethena.mediafilefinder.utils;

import android.support.v4.provider.DocumentFile;

import java.io.File;

public class FileUtil {

    public final static int FILE_TYPE_OTHER = 0;
    public final static int FILE_TYPE_AUDIO = 1;
    public final static int FILE_TYPE_VIDEO = 2;
    public final static int FILE_TYPE_IMAGE = 3;


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


}
