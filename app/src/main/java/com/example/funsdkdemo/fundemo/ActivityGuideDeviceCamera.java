package com.example.funsdkdemo.fundemo;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.funsdkdemo.ActivityAll;
import com.example.funsdkdemo.MyAdapter;
import com.example.funsdkdemo.MyDialog;
import com.example.funsdkdemo.R;
import com.example.funsdkdemo.common.DialogInputPasswd;
import com.example.funsdkdemo.common.UIFactory;
import com.example.funsdkdemo.lib.funsdk.support.FunDevicePassword;
import com.example.funsdkdemo.lib.funsdk.support.FunError;
import com.example.funsdkdemo.lib.funsdk.support.FunLog;
import com.example.funsdkdemo.lib.funsdk.support.FunPath;
import com.example.funsdkdemo.lib.funsdk.support.FunSupport;
import com.example.funsdkdemo.lib.funsdk.support.OnFunDeviceOptListener;
import com.example.funsdkdemo.lib.funsdk.support.config.OPPTZControl;
import com.example.funsdkdemo.lib.funsdk.support.config.OPPTZPreset;
import com.example.funsdkdemo.lib.funsdk.support.config.SystemInfo;
import com.example.funsdkdemo.lib.funsdk.support.models.FunDevType;
import com.example.funsdkdemo.lib.funsdk.support.models.FunDevice;
import com.example.funsdkdemo.lib.funsdk.support.models.FunStreamType;
import com.example.funsdkdemo.lib.funsdk.support.utils.FileUtils;
import com.example.funsdkdemo.lib.funsdk.support.utils.MyUtils;
import com.example.funsdkdemo.lib.funsdk.support.widget.FunVideoView;
import com.example.funsdkdemo.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.lib.EPTZCMD;
import com.lib.FunSDK;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.example.funsdkdemo.lib.funsdk.support.models.FunDevType.EE_DEV_SPORTCAMERA;

/**
 * Demo: 监控类设备播放控制等 
 * 
 *
 */
@SuppressLint("ClickableViewAccessibility")
public class ActivityGuideDeviceCamera 
				extends ActivityAll
				implements OnClickListener,
							OnFunDeviceOptListener,
		OnPreparedListener,
		OnErrorListener,
		OnInfoListener {

	//通过ip连接的设备列表
	private List<String> userDevicesIP = new ArrayList<String>();
	private RecyclerView userDevicesList;

	//添加用户设备列表
	private ImageButton addUserDevice = null;
	//刷新用户设备列表
	private ImageButton refreshUserDevice = null;
	private MyAdapter adapter;

	
	private LinearLayout mLayoutTop = null;
	private TextView mTextTitle = null;

	private FunDevice mFunDevice = null;

	private RelativeLayout mLayoutVideoWnd = null;
	private FunVideoView mFunVideoView = null;
	private LinearLayout mVideoControlLayout = null;
	private TextView mTextStreamType = null;

	private Button mBtnPlay = null;
	private Button mBtnStop = null;
	private Button mBtnStream = null;
	private Button mBtnCapture = null;
	private Button mBtnRecord = null;
	private Button mBtnScreenRatio = null;
	private Button mBtnFishEyeInfo = null;
	/*private Button mBtnGetPreset = null;
	private Button mBtnSetPreset = null;
	private View mSplitView = null;*/
	private RelativeLayout mLayoutRecording = null;

	private LinearLayout mLayoutControls = null;
	/*private LinearLayout mLayoutChannel = null;
	private RelativeLayout mBtnVoiceTalk = null;
	private Button mBtnVoice = null;
    private ImageButton mBtnQuitVoice = null;
	private ImageButton mBtnDevCapture = null;
	private ImageButton mBtnDevRecord = null;*/

/*	private RelativeLayout mLayoutDirectionControl = null;
	private ImageButton mPtz_up = null;
	private ImageButton mPtz_down = null;
	private ImageButton mPtz_left = null;
	private ImageButton mPtz_right = null;*/

	private TextView mTextVideoStat = null;
	/*private AlertDialog alert = null;
	private AlertDialog.Builder builder = null;

	private String preset = null;*/
	private int mChannelCount;
	private boolean isGetSysFirst = true;

	private final int REFRESH_DEVICE_LIST = 1;
	private final int MESSAGE_PLAY_MEDIA = 0x100;
	private final int MESSAGE_AUTO_HIDE_CONTROL_BAR = 0x102;
	private final int MESSAGE_TOAST_SCREENSHOT_PREVIEW = 0x103;
	private final int MESSAGE_OPEN_VOICE = 0x104;

	// 自动隐藏底部的操作控制按钮栏的时间
	private final int AUTO_HIDE_CONTROL_BAR_DURATION = 10000;

/*	private TalkManager mTalkManager = null;*/

	private boolean mCanToPlay = false;

	public String NativeLoginPsw; //本地密码
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_device_camera);


		int devId = getIntent().getIntExtra("FUN_DEVICE_ID", 0);
		//接收切换以前活动的IP列表
		String [] tempIPArray = getIntent().getStringArrayExtra("FUN_DEVICE_LIST");
		//如果是切换设备，将数组转换为列表
		if(tempIPArray != null){
			for(String uid: tempIPArray){
				userDevicesIP.add(uid);
			}
		}



		mFunDevice = FunSupport.getInstance().findDeviceById(devId);

		if (null == mFunDevice) {
			finish();
			return;
		}

		mLayoutTop = (LinearLayout) findViewById(R.id.layoutTop);

		mTextTitle = (TextView) findViewById(R.id.textViewInTopLayout);

		/*mBtnBack.setOnClickListener(this);*/

		mLayoutVideoWnd = (RelativeLayout) findViewById(R.id.layoutPlayWnd);

		mBtnPlay = (Button) findViewById(R.id.btnPlay);
		mBtnStop = (Button) findViewById(R.id.btnStop);
		mBtnStream = (Button) findViewById(R.id.btnStream);
		mBtnCapture = (Button) findViewById(R.id.btnCapture);
		mBtnRecord = (Button) findViewById(R.id.btnRecord);
		mBtnScreenRatio = (Button) findViewById(R.id.btnScreenRatio);
		mBtnFishEyeInfo = (Button) findViewById(R.id.btnFishEyeInfo);

		mLayoutRecording = (RelativeLayout) findViewById(R.id.layout_recording);
		mBtnPlay.setOnClickListener(this);
		mBtnStop.setOnClickListener(this);
		mBtnStream.setOnClickListener(this);
		mBtnCapture.setOnClickListener(this);
		mBtnRecord.setOnClickListener(this);
		mBtnScreenRatio.setOnClickListener(this);
		mBtnFishEyeInfo.setOnClickListener(this);

		mTextVideoStat = (TextView) findViewById(R.id.textVideoStat);

		mLayoutControls = (LinearLayout) findViewById(R.id.under_content);

		//Toast.makeText(this,FunSupport.getInstance().getLanDeviceList().toString(),Toast.LENGTH_LONG).show();



		//如果局域网中存在此ip，将IP传入到userDevicesIP中
		if(isExistLandNet(mFunDevice.getDevIP())&&(!isExistListView(mFunDevice.getDevIP(),userDevicesIP))){
			userDevicesIP.add(mFunDevice.getDevIP());
		}else if(!isExistLandNet(mFunDevice.getDevIP())){
			Toast.makeText(ActivityGuideDeviceCamera.this,"不存在此设备!",Toast.LENGTH_LONG).show();
		}else{
			//Toast.makeText(ActivityGuideDeviceCamera.this,"设备已存在！",Toast.LENGTH_LONG).show();
		}



		userDevicesList = (RecyclerView) findViewById(R.id.recyclerView);
		userDevicesList.setHasFixedSize(true);//如果可以确定每个item的高度是固定的，设置这个选项可以提高性能

		userDevicesList.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));//设置RecyclerView的布局管理
		userDevicesList.setItemAnimator(new DefaultItemAnimator());//设置item的添加删除动画，采用默认的动画效果
		adapter = new MyAdapter(this,userDevicesIP);
		userDevicesList.setAdapter(adapter);

		adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {    //添加监听器
			@Override
			public void onItemClick(View view, int position) {
				changeDevice(position);
			}

			@Override
			public void onItemLongClick(View view, int position) {

			}
		});


		addUserDevice = (ImageButton) findViewById(R.id.add_user_device);
		refreshUserDevice = (ImageButton) findViewById(R.id.refresh_user_device);
		addUserDevice.setOnClickListener(this);
		refreshUserDevice.setOnClickListener(this);



		mTextTitle.setText(mFunDevice.devIp);
		/*mBtnVoiceTalk.setOnClickListener(this);mBtnVoiceTalk = (RelativeLayout) findViewById(R.id.btnVoiceTalk);
		mBtnVoice = (Button) findViewById(R.id.Btn_Talk_Switch);
		mBtnQuitVoice = (ImageButton) findViewById(R.id.btn_quit_voice);
		mBtnDevCapture = (ImageButton) findViewById(R.id.btnDevCapture);
		mBtnDevRecord = (ImageButton) findViewById(R.id.btnDevRecord);
		mBtnGetPreset = (Button) findViewById(R.id.btnGetPreset);
		mBtnSetPreset = (Button) findViewById(R.id.btnSetPreset);
		mSplitView = findViewById(R.id.splitView);

		mLayoutDirectionControl = (RelativeLayout) findViewById(R.id.layoutDirectionControl);
		mPtz_up = (ImageButton) findViewById(R.id.ptz_up);
		mPtz_down = (ImageButton) findViewById(R.id.ptz_down);
		mPtz_left = (ImageButton) findViewById(R.id.ptz_left);
		mPtz_right = (ImageButton) findViewById(R.id.ptz_right);
		mBtnVoiceTalk.setOnTouchListener(mIntercomTouchLs);
		mBtnVoice.setOnClickListener(this);
        mBtnQuitVoice.setOnClickListener(this);
		mBtnDevCapture.setOnClickListener(this);
		mBtnDevRecord.setOnClickListener(this);
		mBtnGetPreset.setOnClickListener(this);
		mBtnSetPreset.setOnClickListener(this);

		mPtz_up.setOnTouchListener(onPtz_up);
		mPtz_down.setOnTouchListener(onPtz_down);
		mPtz_left.setOnTouchListener(onPtz_left);
		mPtz_right.setOnTouchListener(onPtz_right);

		mLayoutControls = (LinearLayout) findViewById(R.id.layoutFunctionControl);
		mLayoutChannel = (LinearLayout) findViewById(R.id.layoutChannelBtn);*/

		mFunVideoView = (FunVideoView) findViewById(R.id.funVideoView);
		if (mFunDevice.devType == FunDevType.EE_DEV_LAMP_FISHEYE) {
			// 鱼眼灯泡,设置鱼眼效果
			mFunVideoView.setFishEye(true);
		}

	/*	// 如果支持云台控制，显示方向键和预置点按钮
		if (mFunDevice.isSupportPTZ()) {
			mSplitView.setVisibility(View.VISIBLE);
			mLayoutDirectionControl.setVisibility(View.VISIBLE);
		}*/

		mFunVideoView.setOnTouchListener(new OnVideoViewTouchListener());
		mFunVideoView.setOnPreparedListener(this);
		mFunVideoView.setOnErrorListener(this);
		mFunVideoView.setOnInfoListener(this);
		mVideoControlLayout = (LinearLayout) findViewById(R.id.layoutVideoControl);

		mTextStreamType = (TextView) findViewById(R.id.textStreamStat);



		// 注册设备操作回调
		FunSupport.getInstance().registerOnFunDeviceOptListener(this);


		/*mTextTitle.setText(mFunDevice.devName);*/

		// 允许横竖屏切换
		/*setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);*/

		showVideoControlBar();

		/*mTalkManager = new TalkManager(mFunDevice);*/

		mCanToPlay = false;

		// 如果设备未登录,先登录设备
		if (!mFunDevice.hasLogin() || !mFunDevice.hasConnected()) {
			loginDevice();
		} else {
			requestSystemInfo();
		}
	}




	@Override
	protected void onDestroy() {

		stopMedia();

		FunSupport.getInstance().removeOnFunDeviceOptListener(this);

//		 if ( null != mFunDevice ) {
//		 FunSupport.getInstance().requestDeviceLogout(mFunDevice);
//		 }

		if (null != mHandler) {
			mHandler.removeCallbacksAndMessages(null);
			mHandler = null;
		}

		super.onDestroy();
	}

	
	@Override
	protected void onResume() {

		if (mCanToPlay) {
			playRealMedia();
		}
//			 resumeMedia();

		super.onResume();
	}


	@Override
	protected void onPause() {

		/*stopTalk(0);*/
       /* CloseVoiceChannel(0);*/

		stopMedia();
//		 pauseMedia();

		super.onPause();
	}


	@Override
	public void onBackPressed() {
		// 如果当前是横屏，返回时先回到竖屏
		if (getResources().getConfiguration().orientation
	            == Configuration.ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			return;
		}

		finish();
	}


	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// 检测屏幕的方向：纵向或横向
	    if (getResources().getConfiguration().orientation
	            == Configuration.ORIENTATION_LANDSCAPE) {
			// 当前为横屏， 在此处添加额外的处理代码
			showAsLandscape();
	    }
	    else if(getResources().getConfiguration().orientation
	            == Configuration.ORIENTATION_PORTRAIT) {
			// 当前为竖屏， 在此处添加额外的处理代码
			showAsPortrait();
		}

		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() >= 1000 && v.getId() < 1000 + mChannelCount) {
			mFunDevice.CurrChannel = v.getId() - 1000;
			mFunVideoView.stopPlayback();
			playRealMedia();
		}
		switch (v.getId()) {
		case 1101: {
			startDevicesPreview();
		}
			break;
		case R.id.btnPlay: // 开始播放
		{
			mFunVideoView.stopPlayback();
			mHandler.sendEmptyMessageDelayed(MESSAGE_PLAY_MEDIA, 1000);
//			playRealMedia();
		}
			break;
		case R.id.btnStop: // 停止播放
		{
			stopMedia();
		}
			break;
		case R.id.btnStream: // 切换码流
		{
			switchMediaStream();
		}
			break;
		case R.id.btnCapture: // 截图
		{
			tryToCapture();
		}
			break;
		case R.id.btnRecord: // 录像
		{
			tryToRecord();
		}
			break;
        /*    case R.id.Btn_Talk_Switch:
            {
                OpenVoiceChannel();
            }
            break;
            case R.id.btn_quit_voice:
            {
                CloseVoiceChannel(500);
            }
            break;*/
		/*case R.id.btnDevCapture: // 远程设备图像列表
		{
			startPictureList();
		}
			break;
		case R.id.btnDevRecord: // 远程设备录像列表
		{
			startRecordList();
		}
			break;*/
		case R.id.btnScreenRatio: // 横竖屏切换
		{
			switchOrientation();
		}
			break;
		/*case R.id.btnSetPreset:
			{
			final EditText editText = new EditText(this);
			int inputType = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL;
			editText.setInputType(inputType);
				new AlertDialog.Builder(this).setTitle(R.string.user_input_preset_number)
					.setView(editText)
					.setPositiveButton(R.string.common_confirm, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							int i = 0;
							String preset = editText.getText().toString();
							if (TextUtils.isEmpty(preset)) {
								i = 1;
							}
							 else {
								i = Integer.parseInt(preset);
							}
							if (i > 200) {
								 Toast.makeText(getApplicationContext(),R.string.user_input_preset_number_warn, Toast.LENGTH_SHORT).show();
							} else {
								// 注意：如果是IPC/摇头机,channel = 0, 否则channel=-1，以实际使用设备为准，如果需要兼容，可以两条命令同时发送
								OPPTZControl cmd = new OPPTZControl(OPPTZControl.CMD_SET_PRESET, 0, i);
								FunSupport.getInstance().requestDeviceCmdGeneral(mFunDevice, cmd);

								// for Demo, 为了兼容设备，cmd2和cmd一起发送，两条命令的差别是channel值不同
								OPPTZControl cmd2 = new OPPTZControl(OPPTZControl.CMD_SET_PRESET, -1, i);
								FunSupport.getInstance().requestDeviceCmdGeneral(mFunDevice, cmd2);
							}
						}

					})
					.setNegativeButton(R.string.common_cancel, null).show();
		}
			break;
		case R.id.btnGetPreset:
			{
			OPPTZPreset oPPTZPreset = (OPPTZPreset) mFunDevice.getConfig(OPPTZPreset.CONFIG_NAME);
			if (null != oPPTZPreset) {
				int[] ids = oPPTZPreset.getIds();
				Arrays.sort(ids);
				if (ids != null && ids.length > 0) {
					final String[] idStrs = new String[ids.length];
					for (int i = 0; i < ids.length; i++) {
						idStrs[i] = (Integer.toString(ids[i]));
					}
					alert = null;
					builder = new AlertDialog.Builder(this);
			            alert = builder
			                    .setTitle(R.string.user_select_preset)
							.setSingleChoiceItems(idStrs, 0, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									preset = idStrs[which];
								}
			                    })
			                    .setPositiveButton(R.string.common_skip, new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									if (TextUtils.isEmpty(preset)) {
										preset = idStrs[0];
									}
									which = Integer.parseInt(preset);
									OPPTZControl cmd = new OPPTZControl(OPPTZControl.CMD_GO_TO_PRESET, 0, which);
									FunSupport.getInstance().requestDeviceCmdGeneral(mFunDevice, cmd);
								}
								})
								.setNegativeButton(R.string.common_delete, new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									if (TextUtils.isEmpty(preset)) {
										preset = idStrs[0];
									}
									which = Integer.parseInt(preset);
									OPPTZControl cmd = new OPPTZControl(OPPTZControl.CMD_CLEAR_PRESET, 0, which);
									FunSupport.getInstance().requestDeviceCmdGeneral(mFunDevice, cmd);
								}
							}).setNeutralButton(R.string.common_correct, new DialogInterface.OnClickListener(){
									@Override
									public void onClick(DialogInterface dialog, int which) {
                                        OPPTZControl cmd = new OPPTZControl(OPPTZControl.CMD_CORRECT, 0, 0);
                                        FunSupport.getInstance().requestDeviceCmdGeneral(mFunDevice, cmd);
									}
								}).create();
					alert.show();
				}
			}
		}
			break;*/
		case R.id.btnFishEyeInfo:
			{
				// 显示鱼眼信息
				showFishEyeInfo();
			}
			break;
		case R.id.add_user_device:
			{
				//添加用户设备IP
				addDevice();


			}
			break;
		case R.id.refresh_user_device:
			{
				//刷新用户设备IP
				refreshUserDeviceList();

			}
		}

	}

	private Handler handler = new Handler(){
		public  void handleMessage(Message msg){
			switch (msg.what){
				case REFRESH_DEVICE_LIST:
					showWaitDialog();
			}
		}
	};

	//刷新局域网设备列表
	private void requestToGetLanDeviceList() {
		if ( !FunSupport.getInstance().requestLanDeviceList() ) {
			showToast(R.string.guide_message_error_call);
		} else {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Message message = new Message();
						message.what = REFRESH_DEVICE_LIST;
						handler.sendMessage(message);
						Thread.sleep(500);
						hideWaitDialog();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}

	//刷新用户设备IP
	private void refreshUserDeviceList() {
		int flag = 1,i;


		//刷新局域网中设备列表
		requestToGetLanDeviceList();



		//迭代寻找局域网中是否还存在显示列表中的设备，若没找到则删去，然后刷新列表
		Iterator<String> it = userDevicesIP.iterator();
		while (it.hasNext()){
			String ip = it.next();
			for(i = 0;i < FunSupport.getInstance().getLanDeviceList().size();i++){
				if(FunSupport.getInstance().getLanDeviceList().get(i).getDevIP().equals(ip)){
					break;
				}
			}

			if(i == FunSupport.getInstance().getLanDeviceList().size()){
				it.remove();
				flag = 0;
			}

		}

		if(flag == 0){
			adapter.notifyDataSetChanged();
		}
	}

	//切换设备
	private int changeDevice(int position) {
		int i,tempId=0;
		String ipDate = userDevicesIP.get(position);


		//循环查找此ip的设备
		for(i = 0;i < FunSupport.getInstance().getLanDeviceList().size();i++){
			if(ipDate.equals(FunSupport.getInstance().getLanDeviceList().get(i).getDevIP())){
				//获取设备id
				tempId = FunSupport.getInstance().getLanDeviceList().get(i).getId();

				break;
			}
		}

		//没有查询到，返回0
		if(i == FunSupport.getInstance().getLanDeviceList().size()){
			return  0;
		}

		Toast.makeText(ActivityGuideDeviceCamera.this,mFunDevice.getDevName(),Toast.LENGTH_LONG).show();

		//结束当前活动
		finish();

		//从自己的活动跳转到自己的活动
		Intent intent = new Intent();
		intent.setClass(ActivityGuideDeviceCamera.this,ActivityGuideDeviceCamera.class);
		intent.putExtra("FUN_DEVICE_ID",tempId);
		//将列表转为数组
		String[] IPArray = userDevicesIP.toArray(new String[userDevicesIP.size()]);
		//将列表中的IP数据传到切换后的活动中
		intent.putExtra("FUN_DEVICE_LIST",IPArray);
		startActivity(intent);

		return 1;
	}

	//检验IP是否已经加入到了ListView中
	private boolean isExistListView(String ip,List<String> ipList){
		if(ipList.contains(ip)){
			return true;
		}else{
			return false;
		}
	}

	//检验IP是否存在于局域网设备列表中
	private boolean isExistLandNet(String ip){
		int i;

		for(i = 0;i < FunSupport.getInstance().getLanDeviceList().size();i++){
			if(ip.equals(FunSupport.getInstance().getLanDeviceList().get(i).getDevIP())){
				//如果IP存在于局域网返回true
				return true;
			}
		}
		return false;

	}

	private void addDevice() {

		//刷新局域网中设备列表
		requestToGetLanDeviceList();


		//创建对话框对象的时候对对话框进行监听
		MyDialog dialog = new MyDialog(ActivityGuideDeviceCamera.this,
				new MyDialog.DataBackListener() {
					//利用回调机制
					@Override
					public void getData(String data) {
						String result = data;
						if(isExistLandNet(data)&&!isExistListView(data,userDevicesIP)){
							userDevicesIP.add(result);
							adapter.notifyItemInserted(1);
						}else if(!isExistLandNet(data)){
							Toast.makeText(ActivityGuideDeviceCamera.this,"不存在此设备!",Toast.LENGTH_LONG).show();
						}else{
							Toast.makeText(ActivityGuideDeviceCamera.this,"设备已存在！",Toast.LENGTH_LONG).show();
						}
					}
				});
		dialog.setTitle("添加设备：");
		dialog.show();

	}

	private void tryToRecord() {

		if (!mFunVideoView.isPlaying() || mFunVideoView.isPaused()) {
			showToast(R.string.media_record_failure_need_playing);
			return;
		}


		if (mFunVideoView.bRecord) {
			mFunVideoView.stopRecordVideo();
			mLayoutRecording.setVisibility(View.INVISIBLE);
			toastRecordSucess(mFunVideoView.getFilePath());
		} else {
			mFunVideoView.startRecordVideo(null);
			mLayoutRecording.setVisibility(View.VISIBLE);
			showToast(R.string.media_record_start);
		}

	}

	/**
	 * 视频截图,并延时一会提示截图对话框
	 */
	private void tryToCapture() {
		if (!mFunVideoView.isPlaying()) {
			showToast(R.string.media_capture_failure_need_playing);
			return;
		}

		final String path = mFunVideoView.captureImage(null);	//图片异步保存
		if (!TextUtils.isEmpty(path)) {
			Message message = new Message();
			message.what = MESSAGE_TOAST_SCREENSHOT_PREVIEW;
			message.obj = path;
			mHandler.sendMessageDelayed(message, 200);			//此处延时一定时间等待图片保存完成后显示，也可以在回调成功后显示
		}
	}
	
	

	/**
	 * 显示截图成功对话框
	 * @param path
	 */
	private void toastScreenShotPreview(final String path) {
		View view = getLayoutInflater().inflate(R.layout.screenshot_preview, null, false);
		ImageView iv = (ImageView) view.findViewById(R.id.iv_screenshot_preview);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inDither = true;
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		iv.setImageBitmap(bitmap);
        new AlertDialog.Builder(this)
                .setTitle(R.string.device_socket_capture_preview)
                .setView(view)
                .setPositiveButton(R.string.device_socket_capture_save,
                        new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						File file = new File(path);
                                File imgPath = new File(FunPath.PATH_PHOTO + File.separator
                                        + file.getName());
						if (imgPath.exists()) {
							showToast(R.string.device_socket_capture_exist);
						} else {
                                    FileUtils.copyFile(path, FunPath.PATH_PHOTO + File.separator
                                            + file.getName());
							showToast(R.string.device_socket_capture_save_success);
						}
					}
                        })
                .setNegativeButton(R.string.device_socket_capture_delete,
                        new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						FunPath.deleteFile(path);
						showToast(R.string.device_socket_capture_delete_success);
					}
                        })
                .show();
	}

	/**
	 * 显示录像成功对话框
	 * @param path
	 */
	private void toastRecordSucess(final String path) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.device_sport_camera_record_success)
				.setMessage(getString(R.string.media_record_stop) + path)
				.setPositiveButton(R.string.device_sport_camera_record_success_open,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Intent intent = new Intent("android.intent.action.VIEW");
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								String type = "video/*";
								Uri uri = Uri.fromFile(new File(path));
								intent.setDataAndType(uri, type);
								startActivity(intent);
								FunLog.e("test", "------------startActivity------" + uri.toString());
							}
						})
				.setNegativeButton(R.string.device_sport_camera_record_success_cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						})
				.show();
	}

	private void showVideoControlBar() {
		if (mVideoControlLayout.getVisibility() != View.VISIBLE) {
			TranslateAnimation ani = new TranslateAnimation(0, 0, UIFactory.dip2px(this, 42), 0);
			ani.setDuration(200);
			mVideoControlLayout.startAnimation(ani);
			mVideoControlLayout.setVisibility(View.VISIBLE);
		}

		if (getResources().getConfiguration().orientation
				== Configuration.ORIENTATION_LANDSCAPE) {
			// 横屏情况下,顶部标题栏也动画显示
			TranslateAnimation ani = new TranslateAnimation(0, 0, -UIFactory.dip2px(this, 48), 0);
			ani.setDuration(200);
			mLayoutTop.startAnimation(ani);
			mLayoutTop.setVisibility(View.VISIBLE);
		} else {
			mLayoutTop.setVisibility(View.VISIBLE);
		}

		// 显示后设置10秒后自动隐藏
		mHandler.removeMessages(MESSAGE_AUTO_HIDE_CONTROL_BAR);
		mHandler.sendEmptyMessageDelayed(MESSAGE_AUTO_HIDE_CONTROL_BAR, AUTO_HIDE_CONTROL_BAR_DURATION);
	}

	private void hideVideoControlBar() {
		if (mVideoControlLayout.getVisibility() != View.GONE) {
			TranslateAnimation ani = new TranslateAnimation(0, 0, 0, UIFactory.dip2px(this, 42));
			ani.setDuration(200);
			mVideoControlLayout.startAnimation(ani);
			mVideoControlLayout.setVisibility(View.GONE);
		}

		if (getResources().getConfiguration().orientation
	            == Configuration.ORIENTATION_LANDSCAPE) {
			// 横屏情况下,顶部标题栏也隐藏
			TranslateAnimation ani = new TranslateAnimation(0, 0, 0, -UIFactory.dip2px(this, 48));
			ani.setDuration(200);
			mLayoutTop.startAnimation(ani);
			mLayoutTop.setVisibility(View.GONE);
		}

		// 隐藏后清空自动隐藏的消息
		mHandler.removeMessages(MESSAGE_AUTO_HIDE_CONTROL_BAR);
	}

	private void showAsLandscape() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// 隐藏底部的控制按钮区域
		mLayoutControls.setVisibility(View.GONE);

		// 视频窗口全屏显示
		RelativeLayout.LayoutParams lpWnd = (RelativeLayout.LayoutParams) mLayoutVideoWnd.getLayoutParams();
		lpWnd.height = LayoutParams.MATCH_PARENT;
		// lpWnd.removeRule(RelativeLayout.BELOW);
		lpWnd.topMargin = 0;
		mLayoutVideoWnd.setLayoutParams(lpWnd);

		// 上面标题半透明背景
		mLayoutTop.setBackgroundColor(0x40000000);

		mBtnScreenRatio.setText(R.string.device_opt_smallscreen);
	}

	private void showAsPortrait() {
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// 还原上面标题栏背景
		mLayoutTop.setBackgroundColor(getResources().getColor(R.color.white));
		mLayoutTop.setVisibility(View.VISIBLE);

		// 视频显示为小窗口
		RelativeLayout.LayoutParams lpWnd = (RelativeLayout.LayoutParams) mLayoutVideoWnd.getLayoutParams();
		lpWnd.height = UIFactory.dip2px(this, 240);
		lpWnd.topMargin = UIFactory.dip2px(this, 48);
		// lpWnd.addRule(RelativeLayout.BELOW, mLayoutTop.getId());
		mLayoutVideoWnd.setLayoutParams(lpWnd);

		// 显示底部的控制按钮区域
		mLayoutControls.setVisibility(View.VISIBLE);

		mBtnScreenRatio.setText(R.string.device_opt_fullscreen);
	}

	/**
	 * 切换视频全屏/小视频窗口(以切横竖屏切换替代)
	 */
	private void switchOrientation() {
		// 横竖屏切换
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
				&& getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
			// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		} else if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}


	/***
	 * 打开 多通道预览
	 */
	private void startDevicesPreview(){
		Intent intent = new Intent();
		intent.putExtra("FUNDEVICE_ID", mFunDevice.getId());
		intent.setClass(this, ActivityGuideDevicePreview.class);
		startActivityForResult(intent, 0);
	}

	private class OnVideoViewTouchListener implements OnTouchListener {

		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent event) {

			if (event.getAction() == MotionEvent.ACTION_UP) {
				// 显示或隐藏视频操作菜单
				if (mVideoControlLayout.getVisibility() == View.VISIBLE) {
					hideVideoControlBar();
				} else {
					showVideoControlBar();
				}
			}

			return false;
		}

	}

	private void loginDevice() {
		showWaitDialog();

		FunSupport.getInstance().requestDeviceLogin(mFunDevice);
	}

	private void requestSystemInfo() {
		showWaitDialog();

		FunSupport.getInstance().requestDeviceConfig(mFunDevice, SystemInfo.CONFIG_NAME);
	}
	// 获取设备预置点列表
	private void requestPTZPreset() {
		FunSupport.getInstance().requestDeviceConfig(mFunDevice, OPPTZPreset.CONFIG_NAME, 0);
	}

	private void startPictureList() {
		Intent intent = new Intent();
		intent.putExtra("FUN_DEVICE_ID", mFunDevice.getId());
		intent.putExtra("FILE_TYPE", "jpg");
		if (mFunDevice.devType == EE_DEV_SPORTCAMERA) {
			intent.setClass(this, ActivityGuideDeviceSportPicList.class);
		} else {
			intent.setClass(this, ActivityGuideDevicePictureList.class);
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	private void startRecordList() {
		Intent intent = new Intent();
		intent.putExtra("FUN_DEVICE_ID", mFunDevice.getId());
		intent.putExtra("FILE_TYPE", "h264;mp4");
		intent.setClass(this, ActivityGuideDeviceRecordList.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}


	private void playRealMedia() {

		// 显示状态: 正在打开视频...
		mTextVideoStat.setText(R.string.media_player_opening);
		mTextVideoStat.setVisibility(View.VISIBLE);

		if (mFunDevice.isRemote) {
			mFunVideoView.setRealDevice(mFunDevice.getDevSn(), mFunDevice.CurrChannel);
		} else {
			String deviceIp = FunSupport.getInstance().getDeviceWifiManager().getGatewayIp();
			mFunVideoView.setRealDevice(deviceIp, mFunDevice.CurrChannel);
		}

		// 打开声音
		mFunVideoView.setMediaSound(true);

		// 设置当前播放的码流类型
		if (FunStreamType.STREAM_SECONDARY == mFunVideoView.getStreamType()) {
			mTextStreamType.setText(R.string.media_stream_secondary);
		} else {
			mTextStreamType.setText(R.string.media_stream_main);
		}
	}
	// 添加通道选择按钮
	@SuppressWarnings("ResourceType")
	private void addChannelBtn(int channelCount) {

		int m = UIFactory.dip2px(this, 5);
		int p = UIFactory.dip2px(this, 3);
		TextView textView = new TextView(this);
		LinearLayout.LayoutParams layoutParamsT = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		layoutParamsT.setMargins(m, m, m, m);
		textView.setLayoutParams(layoutParamsT);
		textView.setText(R.string.device_opt_channel);
		textView.setTextSize(UIFactory.dip2px(this, 10));
		textView.setTextColor(getResources().getColor(R.color.theme_color));
		/*mLayoutChannel.addView(textView);*/

		Button bt = new Button(this);
		bt.setId(1101);
		bt.setTextColor(getResources().getColor(R.color.theme_color));
		bt.setPadding(p, p, p, p);
		bt.setLayoutParams(layoutParamsT);
		bt.setText(R.string.device_camera_channels_preview_title);
		bt.setOnClickListener(this);
		/*mLayoutChannel.addView(bt);*/

		for (int i = 0; i < channelCount; i++) {
			Button btn = new Button(this);
			btn.setId(1000 + i);
			btn.setTextColor(getResources().getColor(R.color.theme_color));
			btn.setPadding(p, p, p, p);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(UIFactory.dip2px(this, 40),
					UIFactory.dip2px(this, 40));
			layoutParams.setMargins(m, m, m, m);
			btn.setLayoutParams(layoutParams);
			btn.setText(String.valueOf(i));
			btn.setOnClickListener(this);
			/*mLayoutChannel.addView(btn);*/
		}

	}

	private void stopMedia() {
		if (null != mFunVideoView) {
			mFunVideoView.stopPlayback();
			mFunVideoView.stopRecordVideo();
		}
	}

	private void pauseMedia() {
		if (null != mFunVideoView) {
			mFunVideoView.pause();
		}
	}

	private void resumeMedia() {
		if (null != mFunVideoView) {
			mFunVideoView.resume();
		}
	}

	private void switchMediaStream() {
		if (null != mFunVideoView) {
			if (FunStreamType.STREAM_MAIN == mFunVideoView.getStreamType()) {
				mFunVideoView.setStreamType(FunStreamType.STREAM_SECONDARY);
			} else {
				mFunVideoView.setStreamType(FunStreamType.STREAM_MAIN);
			}

			// 重新播放
			mFunVideoView.stopPlayback();
			playRealMedia();
		}
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_PLAY_MEDIA:
				{
				playRealMedia();
			}
				break;
			case MESSAGE_AUTO_HIDE_CONTROL_BAR:
				{
				hideVideoControlBar();
			}
				break;
            case MESSAGE_TOAST_SCREENSHOT_PREVIEW:
                {
				String path = (String) msg.obj;
				toastScreenShotPreview(path);
			}
				break;
            case MESSAGE_OPEN_VOICE:
                {
				mFunVideoView.setMediaSound(true);
			}
			}
		}
	};

	/*private OnTouchListener mIntercomTouchLs = new OnTouchListener() {

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			try {
				if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
					startTalk();
				} else if (arg1.getAction() == MotionEvent.ACTION_UP) {
					stopTalk(500);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	};

	private void startTalk() {
		if (mTalkManager != null && mHandler != null && mFunVideoView != null) {
			mTalkManager.onStartThread();
            mTalkManager.setTalkSound(false);
		}
	}

	private void stopTalk(int delayTime) {
		if (mTalkManager != null && mHandler != null && mFunVideoView != null) {
			mTalkManager.onStopThread();
            mTalkManager.setTalkSound(true);
		}
	}*/

  /*  private void OpenVoiceChannel(){

        if (mBtnVoice.getVisibility() == View.VISIBLE) {
            TranslateAnimation ani = new TranslateAnimation(0, 0, UIFactory.dip2px(this, 100), 0);
            ani.setDuration(200);
            mBtnVoiceTalk.setAnimation(ani);
            mBtnVoiceTalk.setVisibility(View.VISIBLE);
            mBtnVoice.setVisibility(View.GONE);

            mFunVideoView.setMediaSound(false);			//关闭本地音频

            mTalkManager.onStartTalk();
			mTalkManager.setTalkSound(true);
        }
    }

    private void CloseVoiceChannel(int delayTime){

        if (mBtnVoiceTalk.getVisibility() == View.VISIBLE) {
            TranslateAnimation ani = new TranslateAnimation(0, 0, 0, UIFactory.dip2px(this, 100));
            ani.setDuration(200);
            mBtnVoiceTalk.setAnimation(ani);
            mBtnVoiceTalk.setVisibility(View.GONE);
            mBtnVoice.setVisibility(View.VISIBLE);

            mTalkManager.onStopTalk();
            mHandler.sendEmptyMessageDelayed(MESSAGE_OPEN_VOICE, delayTime);
        }
    }*/

	/**
	 * 显示输入设备密码对话框
	 */
	private void showInputPasswordDialog() {
		DialogInputPasswd inputDialog = new DialogInputPasswd(this,
				getResources().getString(R.string.device_login_input_password), "", R.string.common_confirm,
				R.string.common_cancel) {

			@Override
			public boolean confirm(String editText) {
				// 重新以新的密码登录
				if (null != mFunDevice) {
					NativeLoginPsw = editText;

					onDeviceSaveNativePws();

					// 重新登录
					loginDevice();
				}
				return super.confirm(editText);
			}

			@Override
			public void cancel() {
				super.cancel();

				// 取消输入密码,直接退出
				finish();
			}

		};

		inputDialog.show();
	}

	private void showFishEyeInfo() {
		if ( null != mFunVideoView ) {
			String fishEyeInfo = mFunVideoView.getFishEyeFrameJSONString();
			Intent intent = new Intent();
			intent.setClass(this, ActivityDeviceFishEyeInfo.class);
			intent.putExtra("FISH_EYE_INFO", fishEyeInfo);
			intent.putExtra("DEVICE_SN", mFunDevice.getDevSn());
			this.startActivity(intent);
		}
	}
	
	public void onDeviceSaveNativePws() {
		FunDevicePassword.getInstance().saveDevicePassword(mFunDevice.getDevSn(),
				NativeLoginPsw);
		// 库函数方式本地保存密码
		if (FunSupport.getInstance().getSaveNativePassword()) {
			FunSDK.DevSetLocalPwd(mFunDevice.getDevSn(), "admin", NativeLoginPsw);
			// 如果设置了使用本地保存密码，则将密码保存到本地文件
		}
	}

	@Override
	public void onDeviceLoginSuccess(final FunDevice funDevice) {
		System.out.println("TTT---->>>> loginsuccess");
		
		if (null != mFunDevice && null != funDevice) {
			if (mFunDevice.getId() == funDevice.getId()) {
				
				// 登录成功后立刻获取SystemInfo
				// 如果不需要获取SystemInfo,在这里播放视频也可以:playRealMedia();
				requestSystemInfo();
			}
		}
	}

	@Override
	public void onDeviceLoginFailed(final FunDevice funDevice, final Integer errCode) {
		// 设备登录失败
		hideWaitDialog();
		showToast(FunError.getErrorStr(errCode));

		// 如果账号密码不正确,那么需要提示用户,输入密码重新登录
		if (errCode == FunError.EE_DVR_PASSWORD_NOT_VALID) {
			showInputPasswordDialog();
		}
	}

	@Override
	public void onDeviceGetConfigSuccess(final FunDevice funDevice, final String configName, final int nSeq) {
		int channelCount = 0;
		if (SystemInfo.CONFIG_NAME.equals(configName)) {
			
			if (!isGetSysFirst) {
				return;
			}
			
			// 更新UI
			//此处为示例如何取通道信息，可能会增加打开视频的时间，可根据需求自行修改代码逻辑
			if (funDevice.channel == null) {
				FunSupport.getInstance().requestGetDevChnName(funDevice);
				requestSystemInfo();
				return;
			}
			channelCount = funDevice.channel.nChnCount;
			// if (channelCount >= 5) {
			// channelCount = 5;
			// }
			if (channelCount > 1) {
				mChannelCount = channelCount;

				addChannelBtn(channelCount);
			}

			hideWaitDialog();

			// 设置允许播放标志
			mCanToPlay = true;
			
			isGetSysFirst = false;
			
			showToast(getType(funDevice.getNetConnectType()));
			
			// 获取信息成功后,如果WiFi连接了就自动播放
			// 此处逻辑客户自定义
			if (MyUtils.detectWifiNetwork(this)) {
				playRealMedia();
			} else {
				showToast(R.string.meida_not_auto_play_because_no_wifi);
			}

			// 如果支持云台控制,在获取到SystemInfo之后,获取预置点信息,如果不需要云台控制/预置点功能功能,可忽略之
			if (mFunDevice.isSupportPTZ()) {
				requestPTZPreset();
			}
		} else if (OPPTZPreset.CONFIG_NAME.equals(configName)) {

		} else if (OPPTZControl.CONFIG_NAME.equals(configName)) {
			Toast.makeText(getApplicationContext(), R.string.user_set_preset_succeed, Toast.LENGTH_SHORT).show();

			// 重新获取预置点列表
			requestPTZPreset();
		}
	}

	private String getType(int i){
		switch (i) {
		case 0:
			return "P2P";
		case 1:
			return "DSS";
		case 2:
			return "IP";
		case 5:
			return "RPS";
		default:
			return "";
		}
	}

	@Override
	public void onDeviceGetConfigFailed(final FunDevice funDevice, final Integer errCode) {
		showToast(FunError.getErrorStr(errCode));
	}


	@Override
	public void onDeviceSetConfigSuccess(final FunDevice funDevice,
			final String configName) {

	}


	@Override
	public void onDeviceSetConfigFailed(final FunDevice funDevice,
										final String configName, final Integer errCode) {
		if (OPPTZControl.CONFIG_NAME.equals(configName)) {
			Toast.makeText(getApplicationContext(), R.string.user_set_preset_fail, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onDeviceChangeInfoSuccess(final FunDevice funDevice) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDeviceChangeInfoFailed(final FunDevice funDevice, final Integer errCode) {
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
	public void onDeviceFileListChanged(FunDevice funDevice, H264_DVR_FILE_DATA[] datas) {

	}


	@Override
	public void onPrepared(MediaPlayer arg0) {
		// TODO Auto-generated method stub

	}


	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// 播放失败
		showToast(getResources().getString(R.string.media_play_error) 
				+ " : " 
				+ FunError.getErrorStr(extra));

		if ( FunError.EE_TPS_NOT_SUP_MAIN == extra
				|| FunError.EE_DSS_NOT_SUP_MAIN == extra ) {
			// 不支持高清码流,设置为标清码流重新播放
			if (null != mFunVideoView) {
				mFunVideoView.setStreamType(FunStreamType.STREAM_SECONDARY);
				playRealMedia();
			}
		}

		return true;
	}


	@Override
	public boolean onInfo(MediaPlayer arg0, int what, int extra) {
		if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
			mTextVideoStat.setText(R.string.media_player_buffering);
			mTextVideoStat.setVisibility(View.VISIBLE);
		} else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
			mTextVideoStat.setVisibility(View.GONE);
		}
		return true;
	}

	private OnTouchListener onPtz_up = new OnTouchListener() {

		// @SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent arg1) {
			boolean bstop = true;
			int nPTZCommand = -1;
			// return false;
			switch (arg1.getAction()) {
			case KeyEvent.ACTION_DOWN:
				Log.i("test", "onPtz_up -- KeyEvent.ACTION_DOWN");
				bstop = false;
				nPTZCommand = EPTZCMD.TILT_UP;
				break;
			case KeyEvent.ACTION_UP:
				Log.i("test", "onPtz_up -- KeyEvent.ACTION_UP");
				nPTZCommand = EPTZCMD.TILT_UP;
				bstop = true;
				break;
			case KeyEvent.ACTION_MULTIPLE:
				nPTZCommand = EPTZCMD.TILT_UP;
				bstop = Math.abs(arg1.getX()) > v.getWidth()
						|| Math.abs(arg1.getY()) > v.getHeight();
				break;
			default:
				break;
			}
			onContrlPTZ1(nPTZCommand, bstop);
			return false;
		}
	};
	private OnTouchListener onPtz_down = new OnTouchListener() {

		// @SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent arg1) {
			boolean bstop = true;
			int nPTZCommand = -1;
			// return false;
			switch (arg1.getAction()) {
			case KeyEvent.ACTION_DOWN:
				bstop = false;
				nPTZCommand = EPTZCMD.TILT_DOWN;
				break;
			case KeyEvent.ACTION_UP:
				bstop = true;
				nPTZCommand = EPTZCMD.TILT_DOWN;
				onContrlPTZ1(nPTZCommand, bstop);
				break;
			case KeyEvent.ACTION_MULTIPLE:
				nPTZCommand = EPTZCMD.TILT_DOWN;
				bstop = Math.abs(arg1.getX()) > v.getWidth()
						|| Math.abs(arg1.getY()) > v.getHeight();
				break;
			default:
				break;
			}
			onContrlPTZ1(nPTZCommand, bstop);
			return false;
		}
	};
	private OnTouchListener onPtz_left = new OnTouchListener() {

		// @SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent arg1) {
			boolean bstop = true;
			int nPTZCommand = -1;
			// return false;
			switch (arg1.getAction()) {
			case KeyEvent.ACTION_DOWN:
				bstop = false;
				nPTZCommand = EPTZCMD.PAN_LEFT;
				break;
			case KeyEvent.ACTION_UP:
				bstop = true;
				nPTZCommand = EPTZCMD.PAN_LEFT;
				break;
			case KeyEvent.ACTION_MULTIPLE:
				nPTZCommand = EPTZCMD.PAN_LEFT;
				bstop = Math.abs(arg1.getX()) > v.getWidth()
						|| Math.abs(arg1.getY()) > v.getHeight();
				break;
			default:
				break;
			}
			onContrlPTZ1(nPTZCommand, bstop);
			return false;
		}
	};
	private OnTouchListener onPtz_right = new OnTouchListener() {

		// @SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent arg1) {
			boolean bstop = true;
			int nPTZCommand = -1;
			// return false;
			switch (arg1.getAction()) {
			case KeyEvent.ACTION_DOWN:
				bstop = false;
				nPTZCommand = EPTZCMD.PAN_RIGHT;
				break;
			case KeyEvent.ACTION_UP:
				bstop = true;
				nPTZCommand = EPTZCMD.PAN_RIGHT;
				break;
			case KeyEvent.ACTION_MULTIPLE:
				nPTZCommand = EPTZCMD.PAN_RIGHT;
				bstop = Math.abs(arg1.getX()) > v.getWidth()
						|| Math.abs(arg1.getY()) > v.getHeight();
				break;
			default:
				break;
			}
			onContrlPTZ1(nPTZCommand, bstop);
			return false;
		}
	};


	private void onContrlPTZ1(int nPTZCommand, boolean bStop) {
		FunSupport.getInstance().requestDevicePTZControl(mFunDevice,
	    		nPTZCommand, bStop, mFunDevice.CurrChannel);
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		mFunDevice.CurrChannel = arg1;
		System.out.println("TTTT----"+mFunDevice.CurrChannel);
		if (mCanToPlay) {
			playRealMedia();
		}
	}


	@Override
	public void onDeviceFileListGetFailed(FunDevice funDevice) {
		// TODO Auto-generated method stub
		
	}	

}
