package com.sec.mp.musicplayer.base;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.StrictMode;
import android.support.multidex.MultiDex;

import com.apkfuns.logutils.LogLevel;
import com.apkfuns.logutils.LogUtils;
import com.blankj.utilcode.utils.SPUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.sec.cc.jpush.JPushSetting;
import com.sec.cc.util.CrashHandler;
import com.sec.cc.util.DeviceMgr;
import com.sec.cc.util.PhoneUtil;
import com.sec.mp.musicplayer.contants.SPContants;

import org.xutils.x;

import cn.jpush.android.api.JPushInterface;

//import com.apkfuns.logutils.LogUtils;


/**
 * 做了一些处理的Application的类，为了维持该框架的运行特意写的一个类
 *
 * @author gwm
 */
public class BaseApplication extends Application {
    // 用于存放倒计时时间
    private static final boolean DEVELOPER_MODE = true; // 开启性能测试，检测应用程序所有有可能发生超时的操作，可以在logcat中看到此类操作
    private boolean flag = true; // 标识未检测过版本 false 标识已经检测过版本了
    private boolean isShowLog = true; // 是否打印错误日志
    private static BaseApplication mApplication;
    public static SPUtils spUtils;

    /**
     * 获取该类的实例
     *
     * @return
     */
    public static <T extends BaseApplication> T getmApplication() {
        return (T) mApplication;
    }

    @Override
    public void onCreate() {

        if (mApplication == null) {
            mApplication = this;
        }
        initImageLoader();
        initXutil();
        spUtils = new SPUtils(getApplicationContext(), SPContants.SP_NAME);
        if (DEVELOPER_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads().detectDiskWrites().detectNetwork()
                    .penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
                    .build());
        }
        if (isShowLog) {
            CrashHandler.getInstance().setExceptionCatchMethod(getApplicationContext());
        }
        LogUtils.getLogConfig()
                .configAllowLog(true)
                .configTagPrefix("SECCC")
                .configShowBorders(true)
                .configFormatTag("%d{HH:mm:ss:SSS} %t %c{-5}")
                .configLevel(LogLevel.TYPE_VERBOSE);
        JPushInterface.setDebugMode(true);
        JPushInterface.init(mApplication);
        boolean isVibrator = spUtils.getBoolean(SPContants.IS_VIBRATOR, true);
        boolean isNotification = spUtils.getBoolean(SPContants.IS_NOTIFICATION, true);
        boolean isMute = spUtils.getBoolean(SPContants.IS_MUTE, false);
        if (isNotification) {
            JPushSetting.resumePush(BaseApplication.getmApplication());
        } else {
            JPushSetting.stopPush(BaseApplication.getmApplication());
        }
        JPushSetting.setStyleBasic(this, isVibrator, isMute);
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void initXutil() {
        x.Ext.init(this);
    }


    private void initImageLoader() {
        /**
         * ImageLoader的相关属性初始化
         */
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder() //
                .showImageForEmptyUri(R.drawable.img_fail) //
                .showImageOnFail(R.drawable.img_fail) //
                .cacheInMemory(true) //
                .cacheOnDisk(true) //
                .build();//
        ImageLoaderConfiguration config = new ImageLoaderConfiguration//
                .Builder(getApplicationContext())//
                .defaultDisplayImageOptions(defaultOptions)//
                .discCacheSize(50 * 1024 * 1024)//
                .discCacheFileCount(100)// 缓存一百张图片
                .writeDebugLogs()//
                .build();//
        ImageLoader.getInstance().init(config);
    }


    /**
     * 修改用于判断是否已经弹出过APP版本更新对话框
     */
    public void setFlag(boolean flag) {
        this.flag = flag;
    }


    /**
     * 设置常用的设置项
     *
     * @return
     */
    public DisplayImageOptions getUserPortraitOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.icon_default_user_portrait) //设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.icon_default_user_portrait)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.icon_default_user_portrait)  //设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
//                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
                .build();//构建完成
        return options;
    }

    /**
     * 设置常用的设置项
     *
     * @return
     */
    public DisplayImageOptions getAnswerOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher) //设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.ic_launcher)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.ic_launcher)  //设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
//                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
                .build();//构建完成
        return options;
    }


    /*打印出一些app的参数*/
    private void printAppParameter() {
        PhoneUtil pu = PhoneUtil.getInstance(mApplication);
        DeviceMgr.ScrSize realSize = DeviceMgr.getScreenRealSize(this);

        LogUtils.d(
                "设备信息\nOS：%s(%s)\nScreenSize：%s X %s\nCPU：%s 核心数：%s 最大频率：%s\n是否飞行：%s\n网络类型：%s\n手机品牌：%s \t%s\nROM：%s",
                Build.VERSION.RELEASE,
                Build.VERSION.SDK_INT,
                realSize.w,
                realSize.h,
                pu.getCpuName(), pu.getNumCores(), pu.getMinCpuFreq(),
                pu.isAirModeOpen(),
                pu.getNetWorkType(),
                pu.getBrand(),
                pu.getModel(),
                pu.getTotalInternalMemorySize()
        );

    }

}
