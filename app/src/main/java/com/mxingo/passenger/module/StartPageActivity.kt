package com.mxingo.passenger.module

import android.os.Bundle
import android.os.Handler
import com.mxingo.driver.module.BaseActivity
import com.mxingo.passenger.MyApplication
import com.mxingo.passenger.R
import com.mxingo.passenger.module.base.data.UserInfoPreferences
import com.mxingo.passenger.module.login.LoginActivity

class StartPageActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_page)

        //MyApplication.bus.post(Any())

        if (UserInfoPreferences.getInstance().isFristStart){
            //第一次启动app，跳转隐私协议页面
            GuideActivity.startGuideActivity(this)
        }else if (MyApplication.isMainActivityLive){
            finish()
            return
        }else{
            LoginActivity.startLoginActivity(this)
        }
    }
}
