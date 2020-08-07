package com.example.abu.update_library;

import android.content.Context;

/**
 * Created by ygj on 2020/7/29.
 */

public interface IUpdatePrompter extends IDownloadAgent{
    void setContext(Context mContext);
    void promter(IUpdateAgent updateAgent,boolean downloadDone);
}
