package com.bethena.mediafilefinder.viewmodel;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileItemViewModel implements Parcelable {

    File currentFile;

    List<FileItemViewModel> childFiles;

    String fileType;

    int intFileType;


    public File getCurrentFile() {
        return currentFile;
    }

    public void setCurrentFile(File currentFile) {
        this.currentFile = currentFile;
    }

    public List<FileItemViewModel> getChildFiles() {
        return childFiles;
    }

    public void setChildFiles(List<FileItemViewModel> childFiles) {
        this.childFiles = childFiles;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public int getIntFileType() {
        return intFileType;
    }

    public void setIntFileType(int intFileType) {
        this.intFileType = intFileType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.currentFile);
        dest.writeList(this.childFiles);
        dest.writeString(this.fileType);
        dest.writeInt(this.intFileType);
    }

    public FileItemViewModel() {
    }

    protected FileItemViewModel(Parcel in) {
        this.currentFile = (File) in.readSerializable();
        this.childFiles = new ArrayList<FileItemViewModel>();
        in.readList(this.childFiles, FileItemViewModel.class.getClassLoader());
        this.fileType = in.readString();
        this.intFileType = in.readInt();
    }

    public static final Parcelable.Creator<FileItemViewModel> CREATOR = new Parcelable.Creator<FileItemViewModel>() {
        @Override
        public FileItemViewModel createFromParcel(Parcel source) {
            return new FileItemViewModel(source);
        }

        @Override
        public FileItemViewModel[] newArray(int size) {
            return new FileItemViewModel[size];
        }
    };
}
