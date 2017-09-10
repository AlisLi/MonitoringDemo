package com.example.funsdkdemo;

import android.content.Context;
import android.content.Intent;

import com.example.funsdkdemo.fundemo.ActivityGuideDeviceCamera;
import com.example.funsdkdemo.lib.funsdk.support.models.FunDevType;
import com.example.funsdkdemo.lib.funsdk.support.models.FunDevice;

import java.util.HashMap;
import java.util.Map;

public class DeviceActivitys {

	private static Map<FunDevType, Class<?>> sDeviceActivityMap = new HashMap<FunDevType, Class<?>>();
	
	static {
		// 监控设备
		sDeviceActivityMap.put(FunDevType.EE_DEV_NORMAL_MONITOR,
				ActivityGuideDeviceCamera.class);
	}
	
	public static void startDeviceActivity(Context context, FunDevice funDevice) {
		Class<?> a = sDeviceActivityMap.get(funDevice.devType);
		if ( null != a ) {
			Intent intent = new Intent();
			intent.setClass(context, a);
			intent.putExtra("FUN_DEVICE_ID", funDevice.getId());
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
	}
	
	public static Class<?> getDeviceActivity(FunDevice funDevice) {
		if ( null == funDevice ) {
			return null;
		}
		return sDeviceActivityMap.get(funDevice.devType);
	}
}
