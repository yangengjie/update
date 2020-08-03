package com.example.abu.update_library;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ygj on 2020/7/29.
 */

public class DefaultUpdateChecker implements IUpdateChecker {
    @Override
    public void check(ICheckerAgent checkerAgent, String checkUrl) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(checkUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
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
