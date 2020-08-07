package com.example.abu.update_library;

import com.google.gson.Gson;

/**
 * Created by ygj on 2020/7/29.
 */

public class DefaultUpdateParser implements IUpdateParser {
    @Override
    public UpdateInfo parser(String info) {
        UpdateInfo updateInfo = new UpdateInfo();
        try {
            /**
             * "statusCode":"1","flag":"1","isupgrade":"0","url":"","info":"","Isnew":0
             */
            UpdateBean updateBean = new Gson().fromJson(info, UpdateBean.class);
            updateInfo.updateTitle = "发现新版本";
            updateInfo.hasUpdate = "1".equals(updateBean.getIsnew());
            updateInfo.isForced = "1".equals(updateBean.getIsupgrade());
            updateInfo.updateContent = updateBean.getInfo();
            updateInfo.downloadUrl = updateBean.getUrl();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return updateInfo;
    }
}
