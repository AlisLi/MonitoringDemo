package com.example.funsdkdemo.lib.funsdk.support;

import com.example.funsdkdemo.lib.funsdk.support.models.FunDevRecordFile;

import java.util.List;

public interface OnFunDeviceRecordListener extends OnFunListener {

    void onRequestRecordListSuccess(List<FunDevRecordFile> files);

    void onRequestRecordListFailed(final Integer errCode);

}
