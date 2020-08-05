package com.example.abu.update_library;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.example.abu.update_library.NotificationAgent.NOTIFY_UPDATE_DONE;
import static com.example.abu.update_library.NotificationAgent.NOTIFY_UPDATE_ERROR;


/**
 * Created by ygj on 2020/8/5.
 */
public class UpdateBroadCastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            int clickType = intent.getIntExtra("clickType", -1);
            if (clickType == NOTIFY_UPDATE_DONE) {
                UpdateManager.getInstance(context).install();
            } else if (clickType == NOTIFY_UPDATE_ERROR) {
                UpdateManager.getInstance(context).update();
            }
        }
    }
}
