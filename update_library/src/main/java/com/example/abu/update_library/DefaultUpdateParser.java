package com.example.abu.update_library;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ygj on 2020/7/29.
 */

public class DefaultUpdateParser implements IUpdateParser {
    @Override
    public UpdateInfo parser(String info) {
        UpdateInfo updateInfo=new UpdateInfo();
        try {
            JSONObject jsonObject=new JSONObject(info);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return updateInfo;
    }
}
