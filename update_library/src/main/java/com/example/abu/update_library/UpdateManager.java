package com.example.abu.update_library;

import android.content.Context;

/**
 * Created by ygj on 2020/7/29.
 */

public class UpdateManager {

    private static volatile UpdateManager updateManager;
    private Context mContext;
    private String mCheckUrl;
    private IUpdateChecker updateChecker;
    private OnDownloadListener onDownloadListener;
    private OnFailureListener mOnFailListener;
    private OnPromterShowListener onPromterShowListener;
    private IUpdateParser updateParser;
    private IUpdatePrompter updatePrompter;
    private IUpdateDownload updateDownload;
    private byte[] postData;
    private int smallIcon;
    private UpdateAgent updateAgent;
    private boolean isManaual = false;
    private boolean isIgnore = false;

    private UpdateManager() {

    }


    public static UpdateManager getInstance() {
        if (updateManager == null) {
            synchronized (UpdateManager.class) {
                if (updateManager == null)
                    updateManager = new UpdateManager();
            }
        }
        return updateManager;
    }

    public UpdateManager setmContext(Context mContext) {
        this.mContext = mContext;
        return this;
    }

    public UpdateManager setCheckUrl(String mCheckUrl) {
        this.mCheckUrl = mCheckUrl;
        return this;
    }

    public UpdateManager setManaual(boolean manaual) {
        isManaual = manaual;
        return this;
    }

    public UpdateManager setDownloadListener(OnDownloadListener onDownloadListener) {
        this.onDownloadListener = onDownloadListener;
        return this;
    }

    public UpdateManager setOnPromterShowListener(OnPromterShowListener onPromterShowListener) {
        this.onPromterShowListener = onPromterShowListener;
        return this;
    }

    public UpdateManager setOnFailListener(OnFailureListener onFailListener) {
        this.mOnFailListener = onFailListener;
        return this;
    }

    public UpdateManager setUpdateChecker(IUpdateChecker updateChecker) {
        this.updateChecker = updateChecker;
        return this;
    }

    public UpdateManager setUpdateParser(IUpdateParser updateParser) {
        this.updateParser = updateParser;
        return this;
    }

    public UpdateManager setUpdatePromter(IUpdatePrompter updatePromter) {
        this.updatePrompter = updatePromter;
        return this;
    }

    public UpdateManager setUpdateDownload(IUpdateDownload updateDownload) {
        this.updateDownload = updateDownload;
        return this;
    }

    public UpdateManager setPostData(byte[] postData) {
        this.postData = postData;
        return this;
    }

    public UpdateManager setSmallIcon(int smallIcon) {
        this.smallIcon = smallIcon;
        return this;
    }

    public UpdateManager setIgnore(boolean ignore) {
        isIgnore = ignore;
        return this;
    }

    public void check() {
        if (updateAgent == null)
            updateAgent = new UpdateAgent();
        if (updateChecker == null)
            updateChecker = new DefaultUpdateChecker();
        updateChecker.setPostData(postData);
        if (updateParser == null)
            updateParser = new DefaultUpdateParser();
        if (updatePrompter == null)
            updatePrompter = new DefaultUpdatePromter();
        updatePrompter.setContext(mContext);
        updateAgent.setUpdateChecker(updateChecker);
        updateAgent.setmOnFailListener(mOnFailListener);
        updateAgent.setOnPromterShowListener(onPromterShowListener);
        updateAgent.setUpdateParser(updateParser);
        updateAgent.setUpdatePromter(updatePrompter);
        updateAgent.setSmallIcon(smallIcon);
        updateAgent.setCheckUrl(mCheckUrl);
        updateAgent.setmContext(mContext);
        updateAgent.setManual(isManaual);
        updateAgent.setIgnore(isIgnore);
        updateAgent.check();
    }

    public void update() {
        if (updateAgent != null)
            updateAgent.update();
    }

    public void install() {
        if (updateAgent != null)
            updateAgent.doInstall();
    }

}
