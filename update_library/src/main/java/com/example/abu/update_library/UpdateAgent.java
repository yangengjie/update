package com.example.abu.update_library;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;

/**
 * Created by ygj on 2020/7/29.
 */

public class UpdateAgent implements ICheckerAgent, IUpdateAgent, IDownloadAgent {
    private Context mContext;
    private String checkUrl;
    private IUpdateChecker updateChecker;
    private UpdateInfo updateInfo;
    private OnFailureListener mOnFailListener;
    private IUpdateParser updateParser;
    private IUpdatePrompter updatePromter;
    private File apkFile;
    private File mTempFile;
    private IUpdateDownload updateDownload;
    private UpdateError mError;
    private boolean isDownloading;
    private boolean isCheckUpdate;

    public UpdateAgent(Context mContext, String checkUrl) {
        this.mContext = mContext;
        this.checkUrl = checkUrl;
    }

    public void setUpdateChecker(IUpdateChecker updateChecker) {
        this.updateChecker = updateChecker;
    }

    public void setmOnFailListener(OnFailureListener mOnFailListener) {
        this.mOnFailListener = mOnFailListener;
    }

    public void setUpdateParser(IUpdateParser updateParser) {
        this.updateParser = updateParser;
    }

    public void setUpdatePromter(IUpdatePrompter updatePromter) {
        this.updatePromter = updatePromter;
    }

    public void setUpdateDownload(IUpdateDownload updateDownload) {
        this.updateDownload = updateDownload;
    }

    public void check() {
        if (UpdateUtil.hasNetWork(mContext)) {
            doCheck();
        } else {
            doFailure(new UpdateError(UpdateError.CHECK_NO_NETWORK));
        }
    }

    private void doCheck() {
        if (isCheckUpdate||isDownloading)
            return;
        mError=null;
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                updateChecker.check(UpdateAgent.this, checkUrl);
                isCheckUpdate = true;
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                doCheckFinish();
            }
        }.execute();
    }

    private void doCheckFinish() {
        isCheckUpdate = false;
        if (mError != null)
            doFailure(mError);
        else {
            if (updateInfo == null) {
                doFailure(new UpdateError(UpdateError.CHECK_UNKNOWN));
            } else if (!updateInfo.hasUpdate) {
                doFailure(new UpdateError(UpdateError.UPDATE_NO_NEWER));
            } else {
                mTempFile = new File(mContext.getExternalCacheDir(), String.format("yiyaoguan%d", updateInfo.versionCode));
                apkFile = new File(mContext.getExternalCacheDir(), String.format("yiyaoguan%d.apk", updateInfo.versionCode));
                if (UpdateUtil.isApkExist(apkFile)) {
                    doInstall();
                } else {
                    doPrompt();
                }
            }
        }
    }

    void doPrompt() {
        if (updatePromter == null)
            updatePromter = new DefaultUpdatePromter(mContext);
        updatePromter.promter(this);
    }

    void doInstall() {
        InstallUtil.installApk(mContext, apkFile.getAbsolutePath());
    }

    public void doFailure(UpdateError updateError) {
        if (mOnFailListener != null && updateError.isError())
            mOnFailListener.onFail(updateError);
    }

    @Override
    public void setInfo(String info) {
        if (updateParser == null)
            updateParser = new DefaultUpdateParser();
        updateInfo = updateParser.parser(info);
    }

    @Override
    public UpdateInfo getInfo() {
        return updateInfo;
    }

    @Override
    public void update() {
        if (UpdateUtil.isApkExist(apkFile)) {
            doInstall();
        } else {
            doDownload();
        }
    }

    private void doDownload() {
        if (isDownloading)
            return;
        if (updateDownload == null)
            updateDownload = new DefaultUpdateDownloader();
        updateDownload.download(mContext, this, updateInfo.downloadUrl, mTempFile);
    }

    @Override
    public void onStart() {
        updatePromter.onStart();
        isDownloading = true;
        mError=null;
    }

    @Override
    public void onProgress(int progress) {
        updatePromter.onProgress(progress);
    }

    @Override
    public void onFinish() {
        isDownloading = false;
        updatePromter.onFinish();
        if (mError == null && mTempFile.renameTo(apkFile)) {
            doInstall();
        } else if (mOnFailListener != null)
            mOnFailListener.onFail(mError);
    }

    @Override
    public void onError(UpdateError updateError) {
        updatePromter.onError(updateError);
        mError = updateError;
    }
}
