package com.sec.mp.musicplayer.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONObject;
import com.apkfuns.logutils.LogUtils;
import com.sec.mp.musicplayer.contants.SPContants;
import com.sec.mp.musicplayer.entity.User;


public abstract class BaseFragment extends Fragment {
    private static final String TAG = "BaseFragment";
    protected FragmentManager mFragmentManager = null;

    /**
     * 默认自定义handler
     *
     * @param msg 消息
     */
    protected abstract void handler(Message msg);


    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (((String) msg.obj.toString()).equals("token error")) {
                LogUtils.d(msg);
                ((BaseActivity) getActivity()).showWarnDialog();
            } else {
                handler(msg);
            }
        }
    };

    protected User getUser() {
        User user = null;
        String data = BaseApplication.spUtils.getString(SPContants.USER_DETAILS);
        if (data != null && !data.equals("")) {
            user = JSONObject.parseObject(data, User.class);
        }
        return user;
    }

    protected String getToken() {

        String data = BaseApplication.spUtils.getString(SPContants.TOKEN);

        return data;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
 
}
