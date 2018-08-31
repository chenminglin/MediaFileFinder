package com.bethena.mediafilefinder;

import com.bethena.mediafilefinder.utils.FileUtil;

import org.junit.Test;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MathcherTest {

    @Test
    public void fileTypeTest() {


        boolean isAudio = FileUtil.isAudio("jike_706241142723267_pic.m4a", 0);
        System.out.println("isAudio = " + isAudio);
    }
}
