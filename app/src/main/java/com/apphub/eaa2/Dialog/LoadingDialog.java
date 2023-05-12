package com.apphub.eaa2.Dialog;

import android.app.Dialog;
import android.content.Context;

import com.apphub.eaa2.R;

public class LoadingDialog {

    private Dialog dialog;
    Context context;

    public LoadingDialog(Context context) {
        this.context = context;
        init();
    }

    private void init(){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        if (dialog != null){
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    public void show(){
        dialog.show();
    }

    public void dismiss(){
        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

}
