package com.bethena.mediafilefinder.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bethena.mediafilefinder.R;
import com.bethena.mediafilefinder.utils.FileUtil;
import com.bethena.mediafilefinder.viewmodel.FileItemViewModel;
import com.bethena.mediafilefinder.viewmodel.FileViewModel;

import java.util.List;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.FileViewHolder> {

    List<FileItemViewModel> files;

    public FileListAdapter(List<FileItemViewModel> files) {
        this.files = files;
    }

    public void setNewDatas(List<FileItemViewModel> files) {
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
    public void onBindViewHolder(@NonNull final FileViewHolder fileViewHolder, int i) {

        final FileItemViewModel viewModel = files.get(i);

        fileViewHolder.txtFileName.setText(viewModel.getCurrentFile().getName());

        int fileType = viewModel.getIntFileType();

        int fileTypeDrawableId = 0;

        switch (fileType) {
            case FileUtil.FILE_TYPE_OTHER:
                fileTypeDrawableId = R.drawable.ic_content_paste_black_24dp;
                break;
            case FileUtil.FILE_TYPE_AUDIO:
                fileTypeDrawableId = R.drawable.ic_audiotrack_black_24dp;
                break;
            case FileUtil.FILE_TYPE_IMAGE:
                fileTypeDrawableId = R.drawable.ic_photo_size_select_actual_black_24dp;
                break;
            case FileUtil.FILE_TYPE_VIDEO:
                fileTypeDrawableId = R.drawable.ic_theaters_black_24dp;
                break;
            case FileUtil.FILE_TYPE_DIR:
                fileTypeDrawableId = R.drawable.ic_folder_black_24dp;
                break;
            default:
                fileTypeDrawableId = R.drawable.ic_content_paste_black_24dp;
                break;
        }

        fileViewHolder.imgFileType.setImageResource(fileTypeDrawableId);


        if(fileType==FileUtil.FILE_TYPE_DIR){
            fileViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mOnItemClickListener!=null){
                        mOnItemClickListener.onNextDir(viewModel);
                    }
                }
            });
        }else if(fileType!=FileUtil.FILE_TYPE_OTHER){
            fileViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mOnItemClickListener!=null){
                        mOnItemClickListener.onOpenMedia(viewModel);
                    }
                }
            });
        }else{
            fileViewHolder.itemView.setOnClickListener(null);
        }

        if(fileType!=FileUtil.FILE_TYPE_DIR){
            long size = viewModel.getCurrentFile().length();
            long kbSize = size/1024;
            fileViewHolder.txtFileSize.setText(kbSize+"kb");

        }else{
            fileViewHolder.txtFileSize.setText("");
        }

    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    class FileViewHolder extends RecyclerView.ViewHolder {

        ImageView imgFileType;
        TextView txtFileName;
        TextView txtFileSize;
        View itemView;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFileType = itemView.findViewById(R.id.img_file_type);
            txtFileName = itemView.findViewById(R.id.txt_file_name);
            txtFileSize = itemView.findViewById(R.id.txt_size);
            this.itemView = itemView;
        }
    }

    public interface OnItemClickListener{
        void onNextDir(FileItemViewModel fileItemViewModel);

        void onOpenMedia(FileItemViewModel fileItemViewModel);
    }

    private OnItemClickListener mOnItemClickListener;

    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
