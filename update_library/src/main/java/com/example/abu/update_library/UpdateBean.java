package com.example.abu.update_library;

/**
 * Created by ygj on 2020/8/7.
 */
public class UpdateBean {

    private String statusCode;
    private String flag; //检查用户的合法性
    private String isupgrade; //1 强制更新 0非强制
    private String url;//apk下载url
    private String info; //更新内容
    private String Isnew; //1 有更新 0无更新

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getIsupgrade() {
        return isupgrade;
    }

    public void setIsupgrade(String isupgrade) {
        this.isupgrade = isupgrade;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getIsnew() {
        return Isnew;
    }

    public void setIsnew(String isnew) {
        Isnew = isnew;
    }
}
