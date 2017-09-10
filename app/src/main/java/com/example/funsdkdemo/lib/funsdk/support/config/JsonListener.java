package com.example.funsdkdemo.lib.funsdk.support.config;

public interface JsonListener {
	String getSendMsg();

	boolean onParse(String json);
}
