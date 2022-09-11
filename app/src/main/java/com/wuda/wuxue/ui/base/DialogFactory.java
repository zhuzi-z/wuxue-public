package com.wuda.wuxue.ui.base;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.wuda.wuxue.bean.UserType;
import com.wuda.wuxue.network.ResponseResult;

public class DialogFactory {
    public interface ResultCallback<T> {
        void result(T t, int which);
    }

    public static AlertDialog selectUserDialog(Context mContext, ResultCallback<UserType> callback) {
        String[] items = {"本科生", "研究生"};
        UserType[] users = {UserType.UNDERGRADUATE, UserType.GRADUATE};
        final int[] selected = {-1};
        return new MaterialAlertDialogBuilder(mContext)
                .setTitle("请选择用户类型")
                .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selected[0] = which;
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.result(users[selected[0]], which);
                    }
                })
                .setNeutralButton("取消", null)
                .create();
    }

    public static AlertDialog loadingDialog(Context mContext) {

        LinearProgressIndicator indicator = new LinearProgressIndicator(mContext);
        indicator.setIndeterminate(true);

        return new MaterialAlertDialogBuilder(mContext)
                .setMessage("请稍候")
                .setView(indicator)
                .setCancelable(false)
                .create();
    }

    public static AlertDialog textInputDialog(Context mContext, String title, ResultCallback<String> callback) {
        TextInputLayout textInputLayout = new TextInputLayout(mContext);
        textInputLayout.setPadding(8, 8, 8, 8);
        TextInputEditText textInputEditText = new TextInputEditText(mContext);
        textInputLayout.addView(textInputEditText);

        return new MaterialAlertDialogBuilder(mContext)
                .setTitle(title)
                .setView(textInputLayout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = textInputEditText.getText().toString();
                        callback.result(text, which);
                    }
                })
                .create();
    }

    public static AlertDialog errorInfoDialog(Context mContext, ResponseResult<?> result) {

        String msg;

        if (result.getException() == null) {
            msg = "未知错误";
        } else {
            msg = result.getException().getMessage();
        }

        if (result.getFlag() != null)
            msg += "\nFlag: " + result.getFlag();

        return new MaterialAlertDialogBuilder(mContext)
                .setTitle("出错啦")
                .setMessage(msg)
                .setPositiveButton("确定", null)
                .create();
    }

    public static AlertDialog simpleDialog(Context mContext, CharSequence[] items, ResultCallback<CharSequence> callback) {
        return new MaterialAlertDialogBuilder(mContext)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.result(items[which], which);
                    }
                })
                .create();
    }
}
