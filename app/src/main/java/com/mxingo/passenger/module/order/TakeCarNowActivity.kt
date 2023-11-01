package com.mxingo.passenger.module.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.mxingo.driver.module.BaseActivity
import com.mxingo.driver.utils.Constants
import com.mxingo.passenger.R
import com.mxingo.passenger.module.base.http.ComponentHolder
import com.mxingo.passenger.module.base.http.MyPresenter
import com.mxingo.passenger.widget.MyProgress
import com.mxingo.passenger.widget.ShowToast
import javax.inject.Inject

/**
 * Created by deqiangchen on 2022/11/25.
 */
class TakeCarNowActivity :BaseActivity() {
    @Inject
    lateinit var presenter: MyPresenter
    private lateinit var progress: MyProgress
    private lateinit var locationNow: String
    private lateinit var tvLoc: TextView
    private lateinit var btnCallCar: Button

    private lateinit var userId: String
    companion object {
        @JvmStatic
        fun startTakeCarNowActivity(context: Context, userId: String, locationNow: String) {
            context.startActivity(Intent(context, TakeCarNowActivity::class.java)
                    .putExtra("locationNow",locationNow)
                    .putExtra(Constants.USER_ID, userId))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_car_now)
        ComponentHolder.appComponent!!.inject(this)
        presenter.register(this)
        progress = MyProgress(this)
        locationNow=  intent.getStringExtra("locationNow") as String
        userId = intent.getStringExtra(Constants.USER_ID) as String
        initView()
    }

    private fun initView() {
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setToolbar(toolbar)
        val tvToolbar = findViewById(R.id.tv_toolbar_title) as TextView
        tvToolbar.text = "一键叫车"

        tvLoc = findViewById(R.id.tv_loc) as TextView
        btnCallCar =findViewById(R.id.btn_call_car) as Button
        tvLoc.text = locationNow

        btnCallCar.setOnClickListener {
            ShowToast.showCenter(this,"功能暂未开通")
        }
    }
}