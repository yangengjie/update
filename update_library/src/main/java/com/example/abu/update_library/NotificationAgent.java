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
    private NotificationChannel channel;
    private NotificationCompat.Builder builder;

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
            notificationAgent.create();
            return notificationAgent;
        }
    }

    private void create() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            this.channel = createChannel(getNotificationManager());
        builder = getNotificationBuilder();
    }

    public NotificationManager getNotificationManager() {
        return (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public NotificationCompat.Builder getNotificationBuilder() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, channelId);
        builder.setWhen(System.currentTimeMillis())
                //正在交互
                .setOngoing(true)
                //不能删除
                .setAutoCancel(false);
        return builder;
    }

    public void showNotify(int notifyId, String title, String content, int icon) {
        NotificationManager manager = getNotificationManager();
        builder.setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(content);
        manager.notify(notifyId, builder.build());
    }


    public void showProgressNotification(int notifyId, String title, String content, int icon,
                                         int max, int progress) {
        NotificationManager manager = getNotificationManager();
        //indeterminate:true表示不确定进度，false表示确定进度
        //当下载进度没有获取到content-length时，使用不确定进度条
        builder.setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(content)
                .setProgress(max, progress, max == -1);
        manager.notify(notifyId, builder.build());
    }


    public void showDoneNotification(int notifyId, String title, String content, int icon) {
        Intent intent = new Intent("com.example.yiyaoguan111.update");
        intent.putExtra("clickType", NOTIFY_UPDATE_DONE);
        intent.setClass(mContext, UpdateBroadCastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, NOTIFY_UPDATE_DONE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager manager = getNotificationManager();
        builder.setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setProgress(100, 100, false);
        manager.notify(notifyId, builder.build());

    }

    public void showErrorNotification(int notifyId, String title, String content, int icon) {
        Intent intent = new Intent("com.example.yiyaoguan111.update");
        intent.putExtra("clickType", NOTIFY_UPDATE_ERROR);
        intent.setClass(mContext, UpdateBroadCastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, NOTIFY_UPDATE_ERROR, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager manager = getNotificationManager();
        builder.setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true).
                setContentIntent(pendingIntent);
        manager.notify(notifyId, builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private NotificationChannel createChannel(NotificationManager manager) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW);
        if (manager != null)
            manager.createNotificationChannel(channel);
        return channel;
    }

}
