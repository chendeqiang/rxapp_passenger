package com.mxingo.passenger;

import android.app.Application;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.common.BaiduMapSDKException;
import com.igexin.sdk.PushManager;
import com.mxingo.passenger.module.base.http.AppComponent;
import com.mxingo.passenger.module.base.http.AppModule;
import com.mxingo.passenger.module.base.http.ComponentHolder;
import com.mxingo.passenger.module.base.http.DaggerAppComponent;
import com.mxingo.passenger.module.base.push.PushIntentService;
import com.mxingo.passenger.module.base.push.PushService;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;


/**
 * Created by zhouwei on 2017/6/22.
 */

public class MyApplication extends Application {

    public static Application application;
    public static Bus bus;
    public static String orderNo;
    public static String currActivity = "";
    public static boolean isMainActivityLive = false;

    @Override
    public void onCreate() {
        super.onCreate();
        AppComponent appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
        ComponentHolder.setAppComponent(appComponent);
        application = this;
        bus = new Bus();
        bus.register(this);
    }

    @Subscribe
    public void startApp(Object o) {
        //个推初始化
        PushManager.getInstance().initialize(this.getApplicationContext(), PushService.class);
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), PushIntentService.class);

        //百度地图
        SDKInitializer.setAgreePrivacy(getApplicationContext(), true);

        try {
            // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
            SDKInitializer.initialize(getApplicationContext());
        } catch (BaiduMapSDKException e) {

        }
        SDKInitializer.setCoordType(CoordType.GCJ02);
        LocationClient.setAgreePrivacy(true);
        //设置使用https请求，否则报错：“HttpClient: Catch connection exception, INNER_ERROR”
        SDKInitializer.setHttpsEnable(true);

        bus.unregister(this);

    }

}
