package com.example.abu.update_library;

/**
 * Created by ygj on 2020/7/30.
 */

public interface IDownloadAgent extends OnDownloadListener {
    void onError(UpdateError updateError);
}
