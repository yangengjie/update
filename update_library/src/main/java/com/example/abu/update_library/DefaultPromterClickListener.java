package com.example.abu.update_library;

import android.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

/**
 * Created by ygj on 2020/7/29.
 */

public class DefaultPromterClickListener implements View.OnClickListener {

    private IUpdateAgent updateAgent;
    private boolean autoDismiss;
    private AlertDialog alertDialog;

    public DefaultPromterClickListener(IUpdateAgent updateAgent, boolean autoDismiss) {
        this.updateAgent = updateAgent;
        this.autoDismiss = autoDismiss;
    }

    public void setAlertDialog(AlertDialog alertDialog) {
        this.alertDialog = alertDialog;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_sure) {
            updateAgent.update();
        } else if (v.getId() == R.id.tv_restart) {
            v.setId(R.id.tv_sure);
            ((TextView) v).setText("立即更新");
            updateAgent.update();
        }
        if (alertDialog != null && autoDismiss && !updateAgent.getInfo().isForced)
            alertDialog.dismiss();
    }
}
