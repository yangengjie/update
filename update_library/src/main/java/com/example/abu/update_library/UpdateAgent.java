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
    private NotificationAgent notificationAgent;
    private int smallIcon;

    public UpdateAgent(Context mContext) {
        this.mContext = mContext;
        notificationAgent = new NotificationAgent.Builder(mContext)
                .setChannelId("update")
                .setChannelName("更新")
                .create();
    }

    public void setCheckUrl(String checkUrl) {
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

    public void setSmallIcon(int smallIcon) {
        this.smallIcon = smallIcon;
    }

    public void check() {
        if (UpdateUtil.hasNetWork(mContext)) {
            doCheck();
        } else {
            doFailure(new UpdateError(UpdateError.CHECK_NO_NETWORK));
        }
    }

    private void doCheck() {
        if (isCheckUpdate || isDownloading) {
            doFailure(new UpdateError(UpdateError.DOWNLOADING));
            return;
        }
        mError = null;
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
                doPrompt(UpdateUtil.isApkExist(apkFile));
            }
        }
    }

    void doPrompt(boolean downloadDone) {
        updatePromter.promter(this, downloadDone);
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
        if (isDownloading) {
            doFailure(new UpdateError(UpdateError.DOWNLOADING));
            return;
        }
        if (updateDownload == null)
            updateDownload = new DefaultUpdateDownloader();
        updateDownload.download(mContext, this, updateInfo.downloadUrl, mTempFile);
    }

    @Override
    public void onStart() {
        updatePromter.onStart();
        isDownloading = true;
        mError = null;
        if (!updateInfo.isForced)
            notificationAgent.showNotify(NotificationAgent.UPDATE_NOTIFY_ID, "开始下载", "可稍后查看下载进度", smallIcon);
    }

    @Override
    public void onProgress(int progress) {
        if (!updateInfo.isForced)
            notificationAgent.showProgressNotification(NotificationAgent.UPDATE_NOTIFY_ID, "正在下载新版本", progress + "%", smallIcon, 100, progress);
        updatePromter.onProgress(progress);
    }

    @Override
    public void onFinish() {
        isDownloading = false;
        updatePromter.onFinish();
        if (mError == null && mTempFile.renameTo(apkFile)) {
            if (updateInfo.isForced)
                doInstall();
            else
                notificationAgent.showDoneNotification(NotificationAgent.UPDATE_NOTIFY_ID, "下载完成", "点击进行安装", smallIcon);
        } else {
            if (mOnFailListener != null)
                mOnFailListener.onFail(mError);
            if (!updateInfo.isForced)
                notificationAgent.showErrorNotification(NotificationAgent.UPDATE_NOTIFY_ID, "下载出错", "点击继续下载", smallIcon);
        }
    }

    @Override
    public void onError(UpdateError updateError) {
        updatePromter.onError(updateError);
        mError = updateError;
    }
}
