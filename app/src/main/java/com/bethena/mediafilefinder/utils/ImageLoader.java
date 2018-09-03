package com.bethena.mediafilefinder.utils;

import android.content.Context;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;

import java.io.File;

public class ImageLoader {

    public static DrawableTypeRequest loadFile(Context context, File file){
        return  Glide.with(context).load(file);
    }
}
