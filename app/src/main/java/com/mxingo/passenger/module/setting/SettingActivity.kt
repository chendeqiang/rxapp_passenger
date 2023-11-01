package com.mxingo.passenger.module.setting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.gson.Gson
import com.mxingo.driver.module.BaseActivity
import com.mxingo.driver.utils.Constants
import com.mxingo.passenger.R
import com.mxingo.passenger.model.CheckVersionEntity
import com.mxingo.passenger.model.CommEntity
import com.mxingo.passenger.model.VersionEntity
import com.mxingo.passenger.module.MainActivity
import com.mxingo.passenger.module.UpdateVersionActivity
import com.mxingo.passenger.module.WebViewActivity
import com.mxingo.passenger.module.base.data.UserInfoPreferences
import com.mxingo.passenger.module.base.data.VersionInfo
import com.mxingo.passenger.module.base.http.ComponentHolder
import com.mxingo.passenger.module.base.http.MyPresenter
import com.mxingo.passenger.module.login.LoginActivity
import com.mxingo.passenger.widget.MyProgress
import com.mxingo.passenger.widget.ShowToast
import com.squareup.otto.Subscribe
import javax.inject.Inject

class SettingActivity : BaseActivity() {

    private var userId = 0
    @Inject
    lateinit var presenter: MyPresenter
    private lateinit var progress: MyProgress
    private lateinit var rlCheckVersion: RelativeLayout
    private lateinit var tvVersion: TextView

    companion object {
        @JvmStatic
        fun startSettingActivity(activity: Activity, userId: Int) {
            activity.startActivityForResult(Intent(activity, SettingActivity::class.java)
                    .putExtra(Constants.USER_ID, userId), Constants.REQUEST_USE_SETTING_ACTIVITY)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        rlCheckVersion = findViewById(R.id.rl_check_version) as RelativeLayout
        tvVersion = findViewById(R.id.tv_version) as TextView;

        userId = intent.getIntExtra(Constants.USER_ID, 0)
        ComponentHolder.appComponent!!.inject(this)
        presenter.register(this)
        progress = MyProgress(this)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setToolbar(toolbar)
        val tvToolbar = findViewById(R.id.tv_toolbar_title) as TextView
        tvToolbar.text = "设置"

        findViewById<TextView>(R.id.tv_about_as).setOnClickListener {
            AboutUsActivity.startAboutUsActivity(this, "关于我们", Constants.ABOUTUS)
        }

        findViewById<TextView>(R.id.tv_user_yinsi).setOnClickListener {
            WebViewActivity.startWebViewActivity(this, "隐私政策", "http://www.mxingo.com/mxnet/app/serviceCustom.html")
        }

        findViewById<TextView>(R.id.tv_helper).setOnClickListener {
            WebViewActivity.startWebViewActivity(this,"帮助中心",Constants.HELPER)

        }

        findViewById<TextView>(R.id.tv_logout).setOnClickListener {
            LogoutActivity.startLogoutActivity(this, UserInfoPreferences.getInstance().userId)

        }

        findViewById<TextView>(R.id.tv_kefu).setOnClickListener {
            if (UserInfoPreferences.getInstance().userId == 0) {
                LoginActivity.startLoginActivity(this)
            } else {
                SuggestionsActivity.startSuggestionsActivity(this, UserInfoPreferences.getInstance().userId)
            }
        }

        findViewById<Button>(R.id.btn_logout).setOnClickListener {
            progress.show()
            presenter.logout(userId)
        }
        rlCheckVersion.setOnClickListener {
            presenter.checkVersion(Constants.RX_PASSENGER)
        }
        tvVersion.text = "v_${VersionInfo.getVersionName()}"
    }

    @Subscribe
    fun loadData(any: Any) {
        when (any::class.java) {
            CommEntity::class.java -> {
                progress.dismiss()
                UserInfoPreferences.getInstance().clear()
                setResult(Activity.RESULT_OK)
                finish()
            }
            CheckVersionEntity::class.java -> {
                val checkVersionEntity = any as CheckVersionEntity;
                if (checkVersionEntity.rspCode.equals("00")) {
                    val version = Gson().fromJson(checkVersionEntity.data.value, VersionEntity::class.java)
                    if (version.versionCode > VersionInfo.getVersionCode()) {
                        version.isMustUpdate = version.forceUpdataVersions.contains(VersionInfo.getVersionName())
                        UpdateVersionActivity.startUpdateVersionActivity(applicationContext as MainActivity, version)
                    } else {
                        ShowToast.showCenter(this, "您已经是最新版本了")
                    }
                } else {
                    ShowToast.showCenter(this, checkVersionEntity.rspDesc)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unregister(this)
    }
}
