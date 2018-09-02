package com.bethena.mediafilefinder.utils;

import android.support.v4.provider.DocumentFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    public final static int FILE_TYPE_OTHER = 0;
    public final static int FILE_TYPE_AUDIO = 1;
    public final static int FILE_TYPE_VIDEO = 2;
    public final static int FILE_TYPE_IMAGE = 3;
    public final static int FILE_TYPE_DIR = 4;


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

}
