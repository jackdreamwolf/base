package com.sec.mp.musicplayer;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;

import com.sec.mp.musicplayer.base.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void handler(Message msg) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void onCreate(FragmentManager manager, Bundle savedInstanceState) {

    }
}
