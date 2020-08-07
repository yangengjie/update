package com.example.abu.update_library;

/**
 * Created by ygj on 2020/7/29.
 */

public interface IUpdateChecker {
    void setPostData(byte[] postData);

    void check(ICheckerAgent checkerAgent, String checkUrl);
}
