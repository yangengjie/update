package com.example.abu.update_library;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

/**
 * Created by ygj on 2020/8/5.
 */
public class NotificationAgent {
    private Context mContext;
    private String channelId = "default";
    private String channelName = "通知消息";
    public static final int UPDATE_NOTIFY_ID = 3010;
    public static final int NOTIFY_UPDATE_DONE = 1;
    public static final int NOTIFY_UPDATE_ERROR = 2;

    private NotificationAgent(Context mContext) {
        this.mContext = mContext;
    }

    private void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    private void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public static class Builder {
        private Context mContext;
        private String channelId = "appUpdate";
        private String channelName = "AppUpdate";

        public Builder(Context mContext) {
            this.mContext = mContext;
        }

        public Builder setChannelId(String channelId) {
            this.channelId = channelId;
            return this;
        }

        public Builder setChannelName(String channelName) {
            this.channelName = channelName;
            return this;
        }

        public NotificationAgent create() {
            NotificationAgent notificationAgent = new NotificationAgent(mContext);
            notificationAgent.setChannelId(channelId);
            notificationAgent.setChannelName(channelName);
            return notificationAgent;
        }
    }

    public NotificationManager getNotificationManager() {
        return (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public NotificationCompat.Builder getNotificationBuilder(String title, String content, int icon) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, channelId);
        builder.setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(content)
                .setWhen(System.currentTimeMillis())
                //正在交互
                .setOngoing(true)
                //不能删除
                .setAutoCancel(false);
        return builder;
    }

    public void showNotify(int notifyId, String title, String content, int icon) {
        NotificationManager manager = getNotificationManager();
        NotificationCompat.Builder builder = getNotificationBuilder(title, content, icon);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            manager.createNotificationChannel(createChannel(manager));
        manager.notify(notifyId, builder.build());
    }


    public void showProgressNotification(int notifyId, String title, String content, int icon,
                                         int max, int progress) {
        NotificationManager manager = getNotificationManager();
        NotificationCompat.Builder builder = getNotificationBuilder(title, content, icon);
        //indeterminate:true表示不确定进度，false表示确定进度
        //当下载进度没有获取到content-length时，使用不确定进度条
        builder.setProgress(max, progress, max == -1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            manager.createNotificationChannel(createChannel(manager));
        manager.notify(notifyId, builder.build());
    }


    public void showDoneNotification(int notifyId, String title, String content, int icon) {
        Intent intent = new Intent("com.example.yiyaoguan111.update");
        intent.putExtra("clickType",NOTIFY_UPDATE_DONE);
        intent.setClass(mContext,UpdateBroadCastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, NOTIFY_UPDATE_DONE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager manager = getNotificationManager();
        NotificationCompat.Builder builder = getNotificationBuilder(title, content, icon)
                .setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            manager.createNotificationChannel(createChannel(manager));
        manager.notify(notifyId, builder.build());

    }

    public void showErrorNotification(int notifyId, String title, String content, int icon) {
        Intent intent = new Intent("com.example.yiyaoguan111.update");
        intent.putExtra("clickType",NOTIFY_UPDATE_ERROR);
        intent.setClass(mContext,UpdateBroadCastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, NOTIFY_UPDATE_ERROR, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager manager = getNotificationManager();
        NotificationCompat.Builder builder = getNotificationBuilder(title, content, icon)
                .setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            manager.createNotificationChannel(createChannel(manager));
        manager.notify(notifyId, builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private NotificationChannel createChannel(NotificationManager manager) {
        return new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW);
    }

}
