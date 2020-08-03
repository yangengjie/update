package com.example.abu.update_library;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by ygj on 2020/7/31.
 */

public class AndroidOPermissionActivity extends AppCompatActivity {
    public static final int INSTALL_PACKAGES_REQUESTCODE = 1;
    private String apkPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apkPath = getIntent().getStringExtra("apkPath");
        //未知来源应用授权页面
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        //设置打开允许安装外部来源应用的app,指定我们自己的包名
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, INSTALL_PACKAGES_REQUESTCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // 授权成功
            InstallUtil.installApk(this, apkPath);
        } else {
            // 授权失败
        }
        finish();
    }


}
