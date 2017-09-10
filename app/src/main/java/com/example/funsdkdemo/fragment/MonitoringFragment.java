package com.example.funsdkdemo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.funsdkdemo.ConnectDeviceActivity;
import com.example.funsdkdemo.R;


/**
 * Created by Lizhiguo on 2017/5/22.
 */

public class MonitoringFragment extends Fragment implements View.OnClickListener {
    private View mView;
    private ImageButton addDeviceBt;
    private String[] data = {"肖申克的救赎", "这个杀手不太冷", "霸王别姬", "泰坦尼克号", "瓦力",
            "三傻大闹宝莱坞", "放牛班的春天", "千与千寻", "鬼子来了", "星际穿越"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_monitoring,container,false);

        return mView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //初始化控件
        initView();

        //事件处理
        addDeviceBt.setOnClickListener(this);

    }


    private void initView() {

        addDeviceBt = (ImageButton) getActivity().findViewById(R.id.add_device_id);

        //已经添加的设备列表
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,data);
        ListView listView = (ListView) getActivity().findViewById(R.id.device_list);
        listView.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_device_id:
                Intent intent = new Intent(getActivity(), ConnectDeviceActivity.class);
                startActivity(intent);
                break;
        }
    }
}
