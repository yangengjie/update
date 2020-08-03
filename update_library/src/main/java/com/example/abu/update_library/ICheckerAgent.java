package com.example.abu.update_library;

/**
 * Created by ygj on 2020/7/29.
 */

public interface ICheckerAgent {
    void setInfo(String info);

    void onError(UpdateError updateError);
}
