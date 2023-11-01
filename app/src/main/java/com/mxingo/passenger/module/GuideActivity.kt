package com.mxingo.passenger.module

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.mxingo.driver.module.BaseActivity
import com.mxingo.passenger.MyApplication
import com.mxingo.passenger.R
import com.mxingo.passenger.dialog.MessageDialog2
import com.mxingo.passenger.module.base.data.UserInfoPreferences
import com.mxingo.passenger.module.login.LoginActivity

class GuideActivity : BaseActivity() {

    private lateinit var btnAgree: Button
    private lateinit var btnNotAgree: Button
    private lateinit var tvPrivacyPolicys: TextView

    companion object {
        @JvmStatic
        fun startGuideActivity(context: Context) {
            context.startActivity(Intent(context, GuideActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)
        initView()
    }

    private fun initView() {
        tvPrivacyPolicys =findViewById(R.id.tv_privacy_policys) as TextView
        btnAgree =findViewById(R.id.bt_agree) as Button
        btnNotAgree =findViewById(R.id.bt_notagree) as Button

        tvPrivacyPolicys.setOnClickListener {
            WebViewActivity.startWebViewActivity(this, "隐私政策", "http://www.mxingo.com/mxnet/app/serviceCustom.html")
        }

        btnNotAgree.setOnClickListener {
            val dialog = MessageDialog2(this)
            dialog.setMessageText("我们非常重视对您信息的保护，如不同意该政策，很遗憾我们将无法提供服务")
            dialog.setOnOkClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }

        btnAgree.setOnClickListener {
            UserInfoPreferences.getInstance().setNotFristStart()
            MyApplication.bus.post(Any())
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

}