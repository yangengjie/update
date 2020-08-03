package com.example.abu.update_library;

import android.content.Context;

import java.io.File;

/**
 * Created by ygj on 2020/7/30.
 */

public interface IUpdateDownload {
    void download(Context mContext, IDownloadAgent downloadAgent, String downloadUrl, File temp);
}
