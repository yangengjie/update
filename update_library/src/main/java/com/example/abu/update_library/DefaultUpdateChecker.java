package com.example.abu.update_library;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ygj on 2020/7/29.
 */

public class DefaultUpdateChecker implements IUpdateChecker {
    private byte[] postData;


    @Override
    public void setPostData(byte[] postData) {
        this.postData = postData;
    }

    @Override
    public void check(ICheckerAgent checkerAgent, String checkUrl) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(checkUrl).openConnection();
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Connection", "Keep-Alive");
            //设置是否从httpUrlConnection读入，默认情况下是true; 用于connection.getInputStream().read()
            connection.setDoInput(true);
            if (postData == null)
                connection.setRequestMethod("GET");
            else {
                //由于post需要传递参数，需要使用connection.getOutputStream().write()
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Length", postData.length + "");
                // Post 请求不能使用缓存
                connection.setUseCaches(false);
                connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
                connection.setRequestMethod("POST");
                connection.getOutputStream().write(postData);
            }
            //上述的各种配置需要在openConnection()到connect()之前完成
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                checkerAgent.setInfo(UpdateUtil.readString(connection.getInputStream()));
            } else {
                checkerAgent.onError(new UpdateError(UpdateError.CHECK_HTTP_STATUS));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }
}
