package com.bethena.mediafilefinder.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.provider.DocumentFile;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bethena.mediafilefinder.BuildConfig;
import com.bethena.mediafilefinder.Constants;
import com.bethena.mediafilefinder.R;
import com.bethena.mediafilefinder.utils.FileUtil;
import com.bethena.mediafilefinder.viewmodel.FileItemViewModel;
import com.bethena.mediafilefinder.viewmodel.PathTabViewModel;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;


public class FindedFragment extends Fragment {
    private static final String TAG = FindedFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    FileItemViewModel mFolderViewModel;
    List<FileItemViewModel> mFileItemViewModels;
    RecyclerView mRecyclerView;
    FileListAdapter mAdapter;
    TextView mTxtPath;

    List<PathTabViewModel> mTabViewModels;
    RecyclerView mTabRecyclerView;
    PathTabsAdapter mPathTabsAdapter;

    public FindedFragment() {
    }


    public static FindedFragment newInstance(FileItemViewModel folderViewModel) {
        FindedFragment fragment = new FindedFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.KEY_PARAM1, folderViewModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFolderViewModel = getArguments().getParcelable(Constants.KEY_PARAM1);
            mFileItemViewModels = mFolderViewModel.getChildFiles();

            mTabViewModels = new ArrayList<>();
            List<File> parents = FileUtil.getAllParent(mFolderViewModel.getCurrentFile());
            if (parents != null && parents.size() > 0) {
                for (int i = parents.size() - 1; i >= 0; i--) {
                    mTabViewModels.add(new PathTabViewModel(parents.get(i)));
                }
            }

            mTabViewModels.add(new PathTabViewModel(mFolderViewModel.getCurrentFile()));
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
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mTxtPath = (TextView) view.findViewById(R.id.txt_path);
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
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                String bpath = "file://" + fileItemViewModel.getCurrentFile().getAbsolutePath();
////                Uri uri = Uri.fromFile(fileItemViewModel.getCurrentFile());
////                if(fileItemViewModel.getIntFileType()== FileUtil.FILE_TYPE_AUDIO){
////                    intent.setDataAndType(uri, "audio/*");
////                }else if(fileItemViewModel.getIntFileType()== FileUtil.FILE_TYPE_VIDEO){
////                    intent.setDataAndType(uri, "video/*");
////                }else if(fileItemViewModel.getIntFileType()== FileUtil.FILE_TYPE_IMAGE){
////                    intent.setDataAndType(uri, "image/*");
////                }
//                Uri uri = null;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    uri = FileProvider.getUriForFile(getActivity(),
//                            "com.bethena.mediafilefinder.fileProvider", fileItemViewModel.getCurrentFile());
//                } else {
//                    uri = Uri.fromFile(fileItemViewModel.getCurrentFile());
//                }
//                intent.setDataAndType(uri, fileItemViewModel.getFileType());
//                startActivity(intent);

//                FileUtil.openFile(fileItemViewModel.getCurrentFile(), getContext());

                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(Intent.ACTION_VIEW);
                File file = fileItemViewModel.getCurrentFile();
                intent.setDataAndType(Uri.fromFile(file), DocumentFile.fromFile(file).getType());
                getActivity().startActivity(intent);

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(getContext())
                        .build()
        );


        mTabRecyclerView = (RecyclerView) view.findViewById(R.id.rv_path_tabs);
        LinearLayoutManager tabManager = new LinearLayoutManager(getContext());
        tabManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mTabRecyclerView.setLayoutManager(tabManager);

        mPathTabsAdapter = new PathTabsAdapter(mTabViewModels);
        mTabRecyclerView.setAdapter(mPathTabsAdapter);

        mPathTabsAdapter.setTabOnClickListener(new PathTabsAdapter.TabOnClickListener() {
            @Override
            public void onClick(PathTabViewModel viewModel) {
                Timber.tag(TAG).d(viewModel.floder.getAbsolutePath());
                MainActivity activity = (MainActivity) getActivity();

                activity.toFolder(viewModel.floder);
            }
        });

        mTabRecyclerView.addItemDecoration(new VerticalDividerItemDecoration
                .Builder(getContext()).drawable(R.drawable.ic_navigate_next_black_24dp)
                .build());

        tabManager.scrollToPositionWithOffset(mTabViewModels.size() - 1, 0);


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
