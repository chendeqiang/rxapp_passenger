
package com.mxingo.passenger.module

//import androidx.core.widget.DrawerLayout
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import butterknife.ButterKnife
import com.baidu.location.LocationClient
import com.baidu.mapapi.SDKInitializer
import com.baidu.mapapi.map.MapView
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.mxingo.driver.module.BaseActivity
import com.mxingo.driver.utils.Constants
import com.mxingo.passenger.MyApplication
import com.mxingo.passenger.R
import com.mxingo.passenger.model.CheckVersionEntity
import com.mxingo.passenger.model.VersionEntity
import com.mxingo.passenger.module.base.data.UserInfoPreferences
import com.mxingo.passenger.module.base.data.VersionInfo
import com.mxingo.passenger.module.base.http.ComponentHolder
import com.mxingo.passenger.module.base.http.MyPresenter
import com.mxingo.passenger.module.base.map.BaiduMapUtil
import com.mxingo.passenger.module.coupon.CouponsActivity
import com.mxingo.passenger.module.invoice.InvoiceActivity
import com.mxingo.passenger.module.login.LoginActivity
import com.mxingo.passenger.module.message.MessageActivity
import com.mxingo.passenger.module.order.MyTripsActivity
import com.mxingo.passenger.module.order.PubOrderActivity
import com.mxingo.passenger.module.order.TakeCarNowActivity
import com.mxingo.passenger.module.setting.SettingActivity
import com.mxingo.passenger.util.StartUtil
import com.squareup.otto.Subscribe
import javax.inject.Inject


class MainActivity : BaseActivity(), View.OnClickListener {
    private lateinit var mv: MapView
    @Inject
    lateinit var presenter: MyPresenter
    private lateinit var btnLocation: Button
    private lateinit var llPubOrder: LinearLayout
    private lateinit var llTakeCar: LinearLayout
    private lateinit var tvMobile: TextView
    private lateinit var drawer: DrawerLayout

    private lateinit var flCAll:FrameLayout

    companion object {
        @JvmStatic
        fun startMainActivity(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //百度地图
        SDKInitializer.setAgreePrivacy(applicationContext,true)
        LocationClient.setAgreePrivacy(true)
        SDKInitializer.initialize(applicationContext)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        ComponentHolder.appComponent!!.inject(this)
        presenter.register(this)

        mv = findViewById(R.id.mv) as MapView
        BaiduMapUtil.getInstance().setBaiduMap(mv)
        BaiduMapUtil.getInstance().registerLocationListener()
        MyApplication.isMainActivityLive = true
        requestPermissions()

        initToolbar()

        llTakeCar =findViewById(R.id.ll_take_car)
        btnLocation = findViewById<Button>(R.id.btn_location)
        llPubOrder = findViewById<LinearLayout>(R.id.ll_pub_order)
        flCAll = findViewById<FrameLayout>(R.id.fl_call)

        flCAll.setOnClickListener {
            StartUtil.call110Mobile("如遇突发紧急情况及生命财产受到侵犯，请直接拨打110！", this)
        }

        btnLocation.setOnClickListener {
            BaiduMapUtil.getInstance().registerLocationListener()
        }

        //一键叫车
        llTakeCar.setOnClickListener {
            if (UserInfoPreferences.getInstance().userId == 0) {
                LoginActivity.startLoginActivity(this)
            } else if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 2)
            } else {
                TakeCarNowActivity.startTakeCarNowActivity(this,UserInfoPreferences.getInstance().userId.toString(),BaiduMapUtil.getInstance().locationNow)
            }
        }

        llPubOrder.setOnClickListener {
            if (UserInfoPreferences.getInstance().userId == 0) {
                LoginActivity.startLoginActivity(this)
            } else if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 2)
            } else {
                PubOrderActivity.startPubOrderActivity(this, UserInfoPreferences.getInstance().userId)
            }
        }
    }

    fun requestPermissions() {
        val list = arrayListOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE)
        list.filter {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }.map {
            list.remove(it)
        }

        if (list.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, list.toArray(Array<String>(list.size, { i -> i.toString() })), Constants.permissionMain)
        }
    }

    private fun initToolbar() {
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""
        val tvToolbar = findViewById(R.id.tv_toolbar_title) as TextView
        tvToolbar.text = getString(R.string.app_name)
        drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        val headView = navigationView.getHeaderView(0)
        tvMobile = headView.findViewById(R.id.tv_mobile)
        toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_head)
        toolbar.setNavigationOnClickListener {
            if (UserInfoPreferences.getInstance().userId == 0) {
                LoginActivity.startLoginActivity(this)
            } else {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START)
                } else {
                    drawer.openDrawer(GravityCompat.START)
                }
            }
        }

        headView.findViewById<LinearLayout>(R.id.ll_my_trip).setOnClickListener(this)
        headView.findViewById<LinearLayout>(R.id.ll_coupon).setOnClickListener(this)
        headView.findViewById<LinearLayout>(R.id.ll_invoice).setOnClickListener(this)
        headView.findViewById<LinearLayout>(R.id.ll_message).setOnClickListener(this)
        headView.findViewById<LinearLayout>(R.id.ll_setting).setOnClickListener(this)

        if (UserInfoPreferences.getInstance().userId == 0) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        } else {
            tvMobile.text = UserInfoPreferences.getInstance().mobile
        }

    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.ll_my_trip -> {
                MyTripsActivity.startMyTripsActivity(this, UserInfoPreferences.getInstance().userId)
            }
            R.id.ll_coupon -> {
                CouponsActivity.startCouponsActivity(this, UserInfoPreferences.getInstance().userId)
            }
            R.id.ll_invoice -> {
                InvoiceActivity.startInvoiceActivity(this, UserInfoPreferences.getInstance().userId)
            }
            R.id.ll_message -> {
                MessageActivity.startMessageActivity(this, UserInfoPreferences.getInstance().userId)
            }
            R.id.ll_setting -> {
                SettingActivity.startSettingActivity(this, UserInfoPreferences.getInstance().userId)
            }
        }
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        mv.onResume()
    }

    override fun onPause() {
        super.onPause()
        mv.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mv.onDestroy()
        presenter.unregister(this)
        BaiduMapUtil.getInstance().unregisterLocationListener()
        BaiduMapUtil.getInstance().geoCoderDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_LOGIN_ACTIVITY && resultCode == Activity.RESULT_OK) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            tvMobile.text = UserInfoPreferences.getInstance().mobile
        } else if (requestCode == Constants.REQUEST_USE_SETTING_ACTIVITY && resultCode == Activity.RESULT_OK) {
            drawer.closeDrawer(GravityCompat.START)
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }

    }

    @Subscribe
    fun checkVersion(checkVersionEntity: CheckVersionEntity) {
        if (checkVersionEntity.rspCode.equals("00")) {
            val version = Gson().fromJson(checkVersionEntity.data.value, VersionEntity::class.java)
            if (version.versionCode > VersionInfo.getVersionCode()) {
                version.isMustUpdate = version.forceUpdataVersions.contains(VersionInfo.getVersionName())
                UpdateVersionActivity.startUpdateVersionActivity(this,version)
            }
        }
    }

}
