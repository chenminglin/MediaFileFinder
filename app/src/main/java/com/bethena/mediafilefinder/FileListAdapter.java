package com.bethena.mediafilefinder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.FileViewHolder> {

    List<String> files;

    public FileListAdapter(List<String> files) {
        this.files = files;
    }

    public void setNewDatas(List<String> files){
        this.files.clear();

        this.files.addAll(files);
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_file, viewGroup, false);
        return new FileViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder fileViewHolder, int i) {
        fileViewHolder.txtFile.setText(files.get(i).toString());
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    class FileViewHolder extends RecyclerView.ViewHolder {

        TextView txtFile;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFile = itemView.findViewById(R.id.txt_path);
        }
    }
}
