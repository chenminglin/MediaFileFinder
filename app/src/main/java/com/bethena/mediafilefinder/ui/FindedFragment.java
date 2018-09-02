package com.bethena.mediafilefinder.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bethena.mediafilefinder.Constants;
import com.bethena.mediafilefinder.R;
import com.bethena.mediafilefinder.viewmodel.FileItemViewModel;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class FindedFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    FileItemViewModel mFolderViewModel;

    List<FileItemViewModel> mFileItemViewModels;

    RecyclerView mRecyclerView;

    FileListAdapter mAdapter;

    TextView mTxtPath;

    public FindedFragment() {
    }


    public static FindedFragment newInstance(FileItemViewModel folderViewModel) {
        FindedFragment fragment = new FindedFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.KEY_PARAM1,folderViewModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFolderViewModel = getArguments().getParcelable(Constants.KEY_PARAM1);
            mFileItemViewModels = mFolderViewModel.getChildFiles();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_finded, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mTxtPath = view.findViewById(R.id.txt_path);

        mTxtPath.setText(mFolderViewModel.getCurrentFile().getAbsolutePath());

        mAdapter = new FileListAdapter(mFileItemViewModels);

        mAdapter.setOnItemClickListener(new FileListAdapter.OnItemClickListener() {
            @Override
            public void onNextDir(FileItemViewModel fileItemViewModel) {
                MainActivity activity = (MainActivity) getActivity();
                activity.nextFolder(fileItemViewModel);
            }

            @Override
            public void onOpenMedia(FileItemViewModel fileItemViewModel) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String bpath = "file://" + fileItemViewModel.getCurrentFile().getAbsolutePath();
                intent.setDataAndType(Uri.parse(bpath), fileItemViewModel.getFileType());
                startActivity(intent);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(getContext())
                        .build()
        );




    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
