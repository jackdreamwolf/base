package com.sec.mp.musicplayer.base;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.sec.mp.musicplayer.utils.AppUtils;
import com.zhy.autolayout.AutoLayoutActivity;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * 功能描述：对Activity类进行扩展
 *
 * @author android_ls
 */
public abstract class BaseActivity extends AutoLayoutActivity {
    private boolean running = false;
    private SweetAlertDialog dialog;
    private static final String TAG = "BaseActivity";
    private int SDK_PERMISSION_REQUEST = 124;
    /**
     * LOG打印标签
     */
    private FragmentManager manager;
    private final String url = ""; // 获取服务器上apk最新版本的url
    private AppUtils app_util;
    private final String path = ""; // 保存到本地的文件路径

    /**
     * 默认自定义handler
     *
     * @param msg 消息
     */
    protected abstract void handler(Message msg);


    protected Handler mHandler = new Handler(
    ) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (((String) msg.obj.toString()).equals("token error")) {
                LogUtils.d(msg);
                showWarnDialog();
            } else {
                handler(msg);
            }
        }
    };

    /**
     * 全局的Context {@link #mContext = this.getApplicationContext();}
     */
    protected Context mContext;

//    private IntentFilter filter = new IntentFilter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        running = true;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getPersimmions();
//        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        manager = getSupportFragmentManager();
        int layoutId = getLayoutId();
        if (layoutId != 0) {
            setContentView(layoutId);
            // 删除窗口背景
//             getWindow().setBackgroundDrawable(null);
        }
        mContext = BaseActivity.this;
        app_util = new AppUtils(getApplicationContext());
        // 向用户展示信息前的准备工作在这个方法里处理
        onCreate(manager, savedInstanceState);
        ActivityManager.getInstance().addActivity(this);

    }

    protected void showProgressDialog(String loadText, boolean isCancelable) {
        dialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        dialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        dialog.setTitleText(loadText);
        dialog.setCancelable(isCancelable);
        dialog.show();

    }


    protected void dismissProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

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
        if (TextUtils.isEmpty(data)) {

        }
        return data;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 获取全局的Context
     *
     * @return {@link #mContext = this.getApplicationContext();}
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * 布局文件ID
     *
     * @return
     */
    protected abstract int getLayoutId();


    /**
     * 含有Bundle通过Class打开编辑界面
     **/
    public void startActivityForResult(Class<?> cls, Bundle bundle,
                                       int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
        // overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    /**
     * 带有右进右出动画的退出
     */
    @Override
    public void finish() {
        running = false;
        super.finish();

    }

    /**
     * 默认退出
     */
    public void defaultFinish() {
        super.finish();
    }


    /**
     * 获取当前应用的版本号
     *
     * @return
     * @throws Exception
     */
    public String getVersionName() {
        return app_util.getVersionName();
    }


    /**
     * 安装指定文件路径的apk文件
     *
     * @param path 文件路径
     */
    protected void installApk(String path) {
        app_util.installApk(path);
    }

    /**
     * Activity的生命周期onCreate()方法
     *
     * @param manager            管理Fragment的对象，如果使用Fragment可以跟本框架做到无缝的结合
     * @param savedInstanceState Activity的状态保存
     */
    public abstract void onCreate(FragmentManager manager, Bundle savedInstanceState);

    @Override
    protected void onDestroy() {
        running = false;
        dismissProgressDialog();
        dialog = null;
        ActivityManager.getInstance().removeActivity(this);
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onResume() {
        running = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        running = false;
        super.onPause();
    }

    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /**
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            // 读写权限
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            // 读取电话状态权限
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            //调用照相机权限
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.CAMERA);
            }
            //调用通讯录权限
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_CONTACTS);
            }
            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)) {
                return true;
            } else {
                permissionsList.add(permission);
                return false;
            }

        } else {
            return true;
        }
    }


    /**
     * 重写dispatchTouchEvent，点击输入框以外的控件隐藏键盘
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    protected void showWarnDialog(String s, final boolean isFinishActivity) {
        if (running) {
            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this).setTitleText(s);
            sweetAlertDialog.setCanceledOnTouchOutside(false);
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    if (isFinishActivity) {
                        sweetAlertDialog.dismiss();
                        finish();
                    } else {
                        sweetAlertDialog.dismiss();
                    }
                }
            });
            sweetAlertDialog.show();
        }
    }

    /**
     * 登录超时 dialog
     */
    void showWarnDialog() {

        dismissProgressDialog();
        if (running) {
            ToastUtils.showShortToast(getContext(), "登录超时，请重新登录");
            SweetAlertDialog sd = new SweetAlertDialog(this);
            sd.setTitleText("登录超时，请重新登录");
            sd.setCancelable(false);
            sd.setCanceledOnTouchOutside(false);
            sd.setConfirmText("重新登录");
            sd.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismiss();
                    ActivityManager.getInstance().clearAllActivity();
                    startActivity(new Intent(getContext(), LoginActivity.class));
                    BaseApplication.spUtils.putString(SPContants.USER_DETAILS, "");
                    BaseApplication.spUtils.putString(SPContants.TOKEN, "");
                    BaseApplication.spUtils.putInt(SPContants.ISDOWARN, 0);
                    BaseApplication.spUtils.putInt(SPContants.ISLOCK, 0);
                    BaseApplication.spUtils.putString(SPContants.COOKIE, "");
                }
            });
            sd.show();
        }
    }
}
