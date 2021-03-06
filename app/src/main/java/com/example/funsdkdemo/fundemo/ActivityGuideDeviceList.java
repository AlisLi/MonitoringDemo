package com.example.funsdkdemo.fundemo;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.funsdkdemo.ActivityAll;
import com.example.funsdkdemo.DeviceActivitys;
import com.example.funsdkdemo.R;
import com.example.funsdkdemo.lib.funsdk.support.FunError;
import com.example.funsdkdemo.lib.funsdk.support.FunSupport;
import com.example.funsdkdemo.lib.funsdk.support.OnFunDeviceListener;
import com.example.funsdkdemo.lib.funsdk.support.OnFunDeviceOptListener;
import com.example.funsdkdemo.lib.funsdk.support.models.FunDevice;
import com.example.funsdkdemo.lib.funsdk.support.models.FunLoginType;
import com.example.funsdkdemo.lib.sdk.struct.H264_DVR_FILE_DATA;

import java.util.ArrayList;
import java.util.List;


/**
 * Demo: 设备列表
 * 说明: 局域网报警功能移到ServiceGuideLanAlarmNotification.java, 有需要,启动Service
 *
 */
public class ActivityGuideDeviceList extends ActivityAll implements OnClickListener, OnFunDeviceListener, ListAdapterFunDevice.OnFunDeviceItemClickListener, OnFunDeviceOptListener {

	
	private TextView mTextTitle = null;
	private ImageButton mBtnBack = null;
	
	private ImageButton mBtnRefresh = null;
	
	private ExpandableListView mListView = null;
	private ListAdapterFunDevice mAdapter = null;
	private List<FunDevice> mDeviceList = new ArrayList<FunDevice>();
	
	private final int MESSAGE_REFRESH_DEVICE_STATUS = 0x100;
	
	// 设备状态刷新时间间隔,目前为3分钟
	private static final int INTERVAL_REFRESH_DEV_STATUS = 3*60*1000;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_device_list);
		
		mTextTitle = (TextView)findViewById(R.id.textViewInTopLayout);
		
		mBtnBack = (ImageButton)findViewById(R.id.backBtnInTopLayout);
		mBtnBack.setOnClickListener(this);
		
		View layoutRefresh = setNavagateRightButton(R.layout.imagebutton_refresh);
		mBtnRefresh = (ImageButton)layoutRefresh.findViewById(R.id.btnRefresh);
		mBtnRefresh.setOnClickListener(this);
		
		mListView = (ExpandableListView)findViewById(R.id.listViewDevice);
		mAdapter = new ListAdapterFunDevice(this, mDeviceList);
		// 不需要连接AP
		mAdapter.setNeedConnectAP(false);
		mAdapter.setOnFunDeviceItemClickListener(this);
		mListView.setAdapter(mAdapter);
		
		// 切换到网络访问访问方式
		FunSupport.getInstance().setLoginType(FunLoginType.LOGIN_BY_INTENTT);
		
		// 监听设备列表类事件
		FunSupport.getInstance().registerOnFunDeviceListener(this);
		
		// 监听设备操作类事件(设备重命名时需要)
		FunSupport.getInstance().registerOnFunDeviceOptListener(this);
		
		// 判断是否用户已经登录,如果未登录打开登录界面
		if ( !FunSupport.getInstance().hasLogin() ) {
			startLogin();
		}
		
		// 刷新设备列表
		refreshTitle();
		refreshDeviceList();
	}
	

	@Override
	protected void onDestroy() {
		
		// 注销设备事件监听
		FunSupport.getInstance().removeOnFunDeviceListener(this);
		
		FunSupport.getInstance().removeOnFunDeviceOptListener(this);
		
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.backBtnInTopLayout:
			{
				// 返回/退出
				finish();
			}
			break;
		case R.id.btnRefresh:
			{
				// 刷新设备列表
				requestToGetDeviceList();
			}
			break;
		}
	}
	
	private void startLogin() {

		finish();
	}

	private void requestToGetDeviceList() {
		if ( !FunSupport.getInstance().requestDeviceList() ) {
			showToast(R.string.guide_message_error_call);
		} else {
			showWaitDialog();
		}
	}
	
	private void refreshTitle() {
		if ( null != mTextTitle ) {
			mTextTitle.setText(String.format(
					getResources().getString(R.string.device_list_for_user), 
					FunSupport.getInstance().getUserName()));
		}
	}
	
	private void refreshDeviceList() {
		hideWaitDialog();
		
		mDeviceList.clear();
		
		mDeviceList.addAll(FunSupport.getInstance().getDeviceList());
		
		mAdapter.notifyDataSetInvalidated();
		
		mHandler.removeMessages(MESSAGE_REFRESH_DEVICE_STATUS);
		
		if ( mDeviceList.size() > 0 ) {
			mHandler.sendEmptyMessageDelayed(MESSAGE_REFRESH_DEVICE_STATUS, 100);
		}
	}
	

	/**
	 * 以下函数实现来自OnFunDeviceListener()，监听设备列表变化
	 */
	@Override
	public void onDeviceListChanged() {
//		if ( null != mListView ) {
//			// 所有展开的列收缩回去
//			for ( int i = 0; i < mListView.getChildCount(); i ++ ) {
//				if ( mListView.isGroupExpanded(i) ) {
//					mListView.collapseGroup(i);
//				}
//			}
//		}
		refreshTitle();
		refreshDeviceList();
	}

	@Override
	public void onDeviceStatusChanged(final FunDevice funDevice) {
		if ( null != mAdapter ) {
			mAdapter.notifyDataSetChanged();
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
		// 设备删除成功
		showToast(R.string.device_opt_remove_success);
		
		// 重新获取用户设备列表,以最新的设备列表为准
        requestToGetDeviceList();
	}


	@Override
	public void onDeviceRemovedFailed(Integer errCode) {
		showToast(FunError.getErrorStr(errCode));
	}
	
	@Override
	public void onAPDeviceListChanged() {
		
	}
	
	@Override
	public void onLanDeviceListChanged() {
		
	}
	
	/**
	 * 以下4个函数来自OnFunDeviceItemClickListener()，监听设备中的操作按钮选择
	 */
	@Override
	public void onFunDeviceRenameClicked(final FunDevice funDevice) {
		// 设备重命名
	}


	@Override
	public void onFunDeviceConnectClicked(FunDevice funDevice) {
		// 打开设备/连接设备
		
	}


	@Override
	public void onFunDeviceControlClicked(FunDevice funDevice) {
		// 打开设备操控界面
		DeviceActivitys.startDeviceActivity(this, funDevice);
	}
	
	@Override
	public void onFunDeviceAlarmClicked(FunDevice funDevice) {
		// 设备报警

	}
	
	@Override
	public void onFunDeviceTransComClicked(FunDevice funDevice) {
		if ( null != funDevice ) {
			Intent intent = new Intent();
			intent.setClass(this, ActivityGuideDeviceTransCom.class);
			intent.putExtra("FUN_DEVICE_ID", funDevice.getId());
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
	}


	@Override
	public void onFunDeviceRemoveClicked(final FunDevice funDevice) {
		// 删除设备
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		AlertDialog alert = builder.setTitle(R.string.device_opt_remove_by_user)
                .setMessage(R.string.device_opt_remove_by_user_tip)
                .setNegativeButton(R.string.common_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    	
                    }
                })
                .setPositiveButton(R.string.common_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if ( !FunSupport.getInstance().requestDeviceRemove(funDevice) ) {
                			showToast(R.string.guide_message_error_call);
                		}
                    }
                }).create();             //创建AlertDialog对象
        alert.show();
	}


	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case MESSAGE_REFRESH_DEVICE_STATUS:
				{
					FunSupport.getInstance().requestAllDeviceStatus();
					
					// 3分钟之后再次获取状态
					removeMessages(MESSAGE_REFRESH_DEVICE_STATUS);
					sendEmptyMessageDelayed(MESSAGE_REFRESH_DEVICE_STATUS, 
							INTERVAL_REFRESH_DEV_STATUS);
				}
				break;
			}
		}
		
	};


	@Override
	public void onDeviceLoginSuccess(FunDevice funDevice) {
		
	}


	@Override
	public void onDeviceLoginFailed(FunDevice funDevice, Integer errCode) {
		// TODO Auto-generated method stub
	}


	@Override
	public void onDeviceGetConfigSuccess(FunDevice funDevice,
										 String configName, int nSeq) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onDeviceGetConfigFailed(FunDevice funDevice, Integer errCode) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onDeviceSetConfigSuccess(final FunDevice funDevice, 
			final String configName) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onDeviceSetConfigFailed(final FunDevice funDevice,
										final String configName, final Integer errCode) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onDeviceChangeInfoSuccess(FunDevice funDevice) {
		// 修改设备信息成功
		showToast(R.string.device_opt_change_name_success);
		
		// 如果有需要，重新获取设备列表,以新的设备列表为准,参考以下注释代码
    	// requestToGetDeviceList();
		// 一般情况下修改信息成功,刷新显示即可
		if ( null != mAdapter ) {
			hideWaitDialog();
			mAdapter.notifyDataSetInvalidated();
		}
	}


	@Override
	public void onDeviceChangeInfoFailed(FunDevice funDevice, Integer errCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeviceOptionSuccess(final FunDevice funDevice, final String option) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onDeviceOptionFailed(final FunDevice funDevice, final String option, final Integer errCode) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDeviceFileListChanged(FunDevice funDevice) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onDeviceFileListChanged(FunDevice funDevice,
			H264_DVR_FILE_DATA[] datas) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onDeviceFileListGetFailed(FunDevice funDevice) {
		// TODO Auto-generated method stub
		
	}

}
