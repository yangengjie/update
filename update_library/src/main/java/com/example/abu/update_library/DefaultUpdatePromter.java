package com.example.abu.update_library;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import static android.widget.ListPopupWindow.MATCH_PARENT;
import static android.widget.ListPopupWindow.WRAP_CONTENT;

/**
 * Created by ygj on 2020/7/29.
 */

public class DefaultUpdatePromter implements IUpdatePrompter {

    private Context mContext;
    private IUpdateAgent updateAgent;
    private AlertDialog alertDialog;
    private UpdateInfo updateInfo;
    private TextView tv_sure;
    private UpdateError updateError;
    private boolean downloadDone;

    @Override
    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void promter(IUpdateAgent updateAgent, boolean downloadDone, OnPromterShowListener onPromterShowListener) {
        if (mContext instanceof Activity && (((Activity) mContext).isFinishing() || ((Activity) mContext).isDestroyed()))
            return;
        this.updateAgent = updateAgent;
        this.downloadDone = downloadDone;
        updateInfo = updateAgent.getInfo();
        if (updateInfo == null)
            return;
        DefaultPromterClickListener clickListener = new DefaultPromterClickListener(updateAgent, true);
        alertDialog = new AlertDialog.Builder(mContext)
                .setView(getCustomView(clickListener))
                .setCancelable(false)
                .create();
        clickListener.setAlertDialog(alertDialog);
        if (onPromterShowListener != null)
            onPromterShowListener.onPromterShow(alertDialog);
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
            alertDialog.getWindow().setLayout(MATCH_PARENT, WRAP_CONTENT);
        }
    }

    @Override
    public Dialog getDialog() {
        return alertDialog;
    }

    public View getCustomView(DefaultPromterClickListener clickListener) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_layout, null);
        TextView title = view.findViewById(R.id.tv_update_title);
        TextView content = view.findViewById(R.id.tv_update_content);
        content.setMovementMethod(new ScrollingMovementMethod());
        content.setVerticalScrollBarEnabled(true);
        TextView tv_cancel = view.findViewById(R.id.tv_cancel);
        View viewline = view.findViewById(R.id.view_line2);
        tv_sure = view.findViewById(R.id.tv_sure);
        title.setText(updateInfo.updateTitle);
        content.setText(updateInfo.updateContent);
        if (updateInfo.isForced) {
            tv_cancel.setVisibility(View.GONE);
            viewline.setVisibility(View.GONE);
        }
        tv_cancel.setOnClickListener(clickListener);
        tv_sure.setOnClickListener(clickListener);
        if (downloadDone) {
            tv_sure.setId(R.id.tv_install);
            tv_sure.setText("安装");
        }

        return view;
    }

    @Override
    public void onStart() {
        updateError = null;
        if (updateInfo != null && !updateInfo.isForced && alertDialog != null)
            alertDialog.dismiss();
    }

    @Override
    public void onProgress(int progress) {
        if (updateInfo != null && updateInfo.isForced && tv_sure != null)
            tv_sure.setText(String.format("下载中%d", progress) + "%");
    }

    @Override
    public void onFinish() {
        if (updateInfo.isForced) {
            if (updateError != null && updateError.isError()) {
                tv_sure.setId(R.id.tv_restart);
                tv_sure.setText("下载失败，重新下载");
            } else {
                tv_sure.setId(R.id.tv_install);
                tv_sure.setText("安装");
            }
        } else if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    @Override
    public void onError(UpdateError updateError) {
        this.updateError = updateError;
    }


}
