package com.bethena.mediafilefinder.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bethena.mediafilefinder.R;
import com.bethena.mediafilefinder.viewmodel.PathTabViewModel;

import java.util.List;

public class PathTabsAdapter extends RecyclerView.Adapter<PathTabsAdapter.PathTabViewHolder> {

    private List<PathTabViewModel> files;
    private TabOnClickListener mTabOnClickListener;

    public TabOnClickListener getTabOnClickListener() {
        return mTabOnClickListener;
    }

    public void setTabOnClickListener(TabOnClickListener tabOnClickListener) {
        this.mTabOnClickListener = tabOnClickListener;
    }

    public PathTabsAdapter(List<PathTabViewModel> files) {
        this.files = files;
    }

    @Override
    public PathTabsAdapter.PathTabViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tab, parent, false);
        return new PathTabsAdapter.PathTabViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PathTabsAdapter.PathTabViewHolder holder, int position) {
        final PathTabViewModel viewModel = files.get(position);
        holder.txtTab.setText(viewModel.floder.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTabOnClickListener != null) {
                    mTabOnClickListener.onClick(viewModel);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    class PathTabViewHolder extends RecyclerView.ViewHolder {
        TextView txtTab;
        View itemView;

        public PathTabViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTab = (TextView) itemView.findViewById(R.id.txt_tab);
            this.itemView = itemView;
        }
    }

    interface TabOnClickListener {
        void onClick(PathTabViewModel viewModel);
    }
}
