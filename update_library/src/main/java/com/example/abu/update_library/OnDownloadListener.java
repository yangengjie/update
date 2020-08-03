package com.example.abu.update_library;

/**
 * Created by ygj on 2020/7/29.
 */

public interface OnDownloadListener {
    void onStart();
    void onProgress(int progress);
    void onFinish();
}
