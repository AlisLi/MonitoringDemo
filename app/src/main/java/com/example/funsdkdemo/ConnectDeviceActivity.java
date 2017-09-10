package com.example.funsdkdemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.lib.FunSDK;
import com.example.funsdkdemo.lib.funsdk.support.FunDevicePassword;
import com.example.funsdkdemo.lib.funsdk.support.FunSupport;
import com.example.funsdkdemo.lib.funsdk.support.OnFunDeviceListener;
import com.example.funsdkdemo.lib.funsdk.support.models.FunDevStatus;
import com.example.funsdkdemo.lib.funsdk.support.models.FunDevType;
import com.example.funsdkdemo.lib.funsdk.support.models.FunDevice;
import com.example.funsdkdemo.lib.funsdk.support.models.FunLoginType;

/**
 * Created by Lizhiguo on 2017/5/22.
 */

public class ConnectDeviceActivity extends ActivityAll implements View.OnClickListener,OnFunDeviceListener,AdapterView.OnItemSelectedListener {


    private EditText mEditDevIP;
    private EditText meditDevicePort;
    private EditText mEditDevIpLoginName;
    private EditText mEditDevIpLoginPasswd;
    private Button mBtnDevIpLogin = null;

    private FunDevice mFunDevice = null;
    private String mCurrDevSn = null;

    private final int MESSAGE_DELAY_FINISH = 0x100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_device);

        initView();


    }

    private void initView() {

        mEditDevIP = (EditText) findViewById(R.id.editDeviceIP);
        meditDevicePort = (EditText) findViewById(R.id.editDevicePort);
        mEditDevIpLoginName = (EditText) findViewById(R.id.editDeviceIpLoginName);
        mEditDevIpLoginPasswd = (EditText) findViewById(R.id.editDeviceIpLoginPasswd);
        mBtnDevIpLogin = (Button) findViewById(R.id.devLoginBtnIP);
        mBtnDevIpLogin.setOnClickListener(this);

        // 设置登录方式为本地登录
        FunSupport.getInstance().setLoginType(FunLoginType.LOGIN_BY_LOCAL);

        // 监听设备类事件
        FunSupport.getInstance().registerOnFunDeviceListener(this);

        mEditDevIP.setText("");
        mEditDevIpLoginName.setText("");
        mEditDevIpLoginPasswd.setText("");

    }

    @Override
    protected void onDestroy() {
        // 注销设备事件监听
        FunSupport.getInstance().removeOnFunDeviceListener(this);

        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
        }

        super.onDestroy();
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_DELAY_FINISH: {
                    hideWaitDialog();

                    // 启动/打开设备操作界面
                    if (null != mFunDevice) {

                        // 传入用户名/密码
                        mFunDevice.loginName = mEditDevIpLoginName.getText().toString().trim();
                        if (mFunDevice.loginName.length() == 0) {
                            // 用户名默认是:admin
                            mFunDevice.loginName = "admin";
                        }
                        mFunDevice.loginPsw = mEditDevIpLoginPasswd.getText().toString().trim();

                        //Save the password to local file
                        FunDevicePassword.getInstance().saveDevicePassword(mFunDevice.getDevSn(), mEditDevIpLoginPasswd.getText().toString().trim());
                        FunSDK.DevSetLocalPwd(mFunDevice.getDevSn(), "admin", mEditDevIpLoginPasswd.getText().toString().trim());

                        DeviceActivitys.startDeviceActivity(ConnectDeviceActivity.this, mFunDevice);
                    }

                    mFunDevice = null;
                    finish();
                }
                break;
            }
        }

    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            
            case R.id.devLoginBtnIP: {
                requestDeviceIpStatus();
            }
            break;
            
        }
    }

    // 设备登录
    private void requestDeviceIpStatus() {
        String devIP = mEditDevIP.getText().toString().trim();
        String devport = meditDevicePort.getText().toString().trim();
        if (devIP.length() == 0) {
            showToast(R.string.device_login_error_sn);
            return;
        }
        if (devport.length() == 0) {
            devport = "34567";
        }
        FunDevType devType = null;
        String devMac = null;
        String dev = null;
        dev = devIP + ":" + devport;
        mFunDevice = FunSupport.getInstance().buildTempDeivce(devType, devMac);
        mCurrDevSn = devIP;
        mFunDevice.devType =  FunDevType.EE_DEV_NORMAL_MONITOR;
        mFunDevice.devIp = devIP;
        mFunDevice.tcpPort = Integer.parseInt(devport);
        mFunDevice.devSn = dev;
        // 传入用户名/密码
        mFunDevice.loginName = mEditDevIpLoginName.getText().toString().trim();
        if (mFunDevice.loginName.length() == 0) {
            // 用户名默认是:admin
            mFunDevice.loginName = "admin";
        }
        mFunDevice.loginPsw = mEditDevIpLoginPasswd.getText().toString().trim();
        DeviceActivitys.startDeviceActivity(ConnectDeviceActivity.this, mFunDevice);

    }



    @Override
    public void onDeviceListChanged() {

    }

    @Override
    public void onDeviceStatusChanged(FunDevice funDevice) {
        // 设备状态变化,如果是当前登录的设备查询之后是在线的,打开设备操作界面
        if (null != mCurrDevSn && mCurrDevSn.equals(funDevice.getDevSn())) {

            mFunDevice = funDevice;

            showToast(R.string.device_get_status_success);

            hideWaitDialog();

            if (funDevice.devStatus == FunDevStatus.STATUS_ONLINE) {
                // 如果设备在线,获取设备信息
                if ((funDevice.devType == null || funDevice.devType == FunDevType.EE_DEV_UNKNOWN)) {
                    funDevice.devType = FunDevType.EE_DEV_NORMAL_MONITOR;
                }

                if (null != mHandler) {
                    mHandler.removeMessages(MESSAGE_DELAY_FINISH);
                    mHandler.sendEmptyMessageDelayed(MESSAGE_DELAY_FINISH, 1000);
                }
            } else {
                // 设备不在线
                showToast(R.string.device_stauts_offline);
            }
        }
    }

    @Override
    public void onDeviceAddedSuccess() {

    }

    @Override
    public void onDeviceAddedFailed(Integer errCode) {

    }

    @Override
    public void onDeviceRemovedSuccess() {

    }

    @Override
    public void onDeviceRemovedFailed(Integer errCode) {

    }

    @Override
    public void onAPDeviceListChanged() {

    }

    @Override
    public void onLanDeviceListChanged() {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
