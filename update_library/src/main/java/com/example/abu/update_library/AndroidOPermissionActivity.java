package com.example.abu.update_library;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import static android.widget.ListPopupWindow.MATCH_PARENT;
import static android.widget.ListPopupWindow.WRAP_CONTENT;

/**
 * Created by ygj on 2020/7/31.
 */

public class AndroidOPermissionActivity extends AppCompatActivity {
    public static final int INSTALL_PACKAGES_REQUESTCODE = 1;
    private String apkPath;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apkPath = getIntent().getStringExtra("apkPath");
        showDialog();
    }


    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setMessage("安装应用需要打开未知来源权限，请去设置中开启权限");
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startInstallPermissionSettingActivity();
                alertDialog.dismiss();
            }
        });
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setLayout(MATCH_PARENT, WRAP_CONTENT);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInstallPermissionSettingActivity() {
        //注意这个是8.0新API
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, INSTALL_PACKAGES_REQUESTCODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INSTALL_PACKAGES_REQUESTCODE && resultCode == RESULT_OK) {
            // 授权成功
            InstallUtil.installApk(this, apkPath);
        } else {
            // 授权失败
            Toast.makeText(this, "新版本安装失败，可能会影响正常使用", Toast.LENGTH_SHORT).show();
        }
        finish();
    }


}
