package com.example.abu.update_library;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.File;

/**
 * Created by ygj on 2020/7/30.
 */

public class InstallUtil {

    public static void installApk(Context context, String apkPath) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //>8.0
            startInstallO(context, apkPath);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //>7.0
            startInstallN(context, apkPath);
        } else {
            startInstall(context, apkPath);
        }
    }

    private static void startInstall(Context context, String apkPath) {
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setDataAndType(Uri.parse("file://" + apkPath), "application/vnd.android.package-archive");
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(install);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static void startInstallN(Context context, String apkPath) {
        Uri uriForFile = UpdateFileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", new File(apkPath));
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setDataAndType(uriForFile, "application/vnd.android.package-archive");
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(install);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void startInstallO(final Context context, final String apkPath) {
        boolean isGranted = context.getPackageManager().canRequestPackageInstalls();
        if (isGranted) startInstallN(context, apkPath);
        else {
            Intent intent = new Intent(context, AndroidOPermissionActivity.class);
            intent.putExtra("apkPath", apkPath);
            context.startActivity(intent);
        }

    }


}
