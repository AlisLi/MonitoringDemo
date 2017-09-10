package com.example.funsdkdemo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.funsdkdemo.R;

/**
 * Created by Lizhiguo on 2017/5/22.
 */

public class DeviceFragment extends Fragment {
    private View mView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_device,container,false);

        return mView;
    }
}
