package com.example.abu.update_library;

import android.content.Context;

/**
 * Created by ygj on 2020/7/29.
 */

public class UpdateManager {


    public static class Builder {
        private Context mContext;
        private String mCheckUrl;
        private IUpdateChecker updateChecker;
        private OnDownloadListener onDownloadListener;
        private OnFailureListener mOnFailListener;
        private IUpdateParser updateParser;
        private IUpdatePrompter updatePrompter;
        private IUpdateDownload updateDownload;

        public Builder(Context mContext) {
            this.mContext = mContext;
        }

        public Builder setCheckUrl(String checkUrl) {
            this.mCheckUrl = checkUrl;
            return this;
        }

        public Builder setDownloadListener(OnDownloadListener onDownloadListener) {
            this.onDownloadListener = onDownloadListener;
            return this;
        }

        public Builder setOnFailListener(OnFailureListener onFailListener) {
            this.mOnFailListener = onFailListener;
            return this;
        }

        public Builder setUpdateChecker(IUpdateChecker updateChecker) {
            this.updateChecker = updateChecker;
            return this;
        }

        public Builder setUpdateParser(IUpdateParser updateParser) {
            this.updateParser = updateParser;
            return this;
        }

        public Builder setUpdatePromter(IUpdatePrompter updatePromter) {
            this.updatePrompter = updatePromter;
            return this;
        }

        public Builder setUpdateDownload(IUpdateDownload updateDownload){
            this.updateDownload=updateDownload;
            return this;
        }

        public void check() {
            UpdateAgent updateAgent = new UpdateAgent(mContext, mCheckUrl);
            if (updateChecker == null)
                updateChecker = new DefaultUpdateChecker();
            updateAgent.setUpdateChecker(updateChecker);
            updateAgent.setmOnFailListener(mOnFailListener);
            updateAgent.setUpdateParser(updateParser);
            updateAgent.setUpdatePromter(updatePrompter);
            updateAgent.check();
        }
    }


}
