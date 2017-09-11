package com.example.funsdkdemo;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Lizhiguo on 2017/9/10.
 */
//自定义对话框
public class MyDialog extends Dialog {
    //定义接口
    public interface DataBackListener{
        public void getData(String data);
    }
    private EditText DevIP;
    private Button btnSure;
    private Button btnCancel;
    DataBackListener listener;   //创建监听对象
    public MyDialog(Context context, final DataBackListener listener) {
        super(context);
        //用传递过来的监听器来初始化
        this.listener = listener;
        setContentView(R.layout.add_device_dialog);

        DevIP = (EditText)findViewById(R.id.dev_ip);
        btnSure = (Button)findViewById(R.id.btn_sure);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = DevIP.getText().toString();
                //这里调用接口，将数据传递出去。
                listener.getData(str);
                dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}