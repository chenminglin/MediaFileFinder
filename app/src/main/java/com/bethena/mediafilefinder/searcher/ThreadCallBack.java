package com.bethena.mediafilefinder.searcher;

import java.util.List;

public interface ThreadCallBack {
    void onFinded(List<String> fileNames);
    void onFinished(SearchAsyncTask task);
}
