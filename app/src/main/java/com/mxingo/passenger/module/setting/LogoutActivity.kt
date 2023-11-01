package com.mxingo.passenger.module.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.mxingo.driver.module.BaseActivity
import com.mxingo.driver.utils.Constants
import com.mxingo.passenger.R
import com.mxingo.passenger.widget.ShowToast

/**
 * Created by deqiangchen on 2023/7/4.
 */
class LogoutActivity: BaseActivity() {
    private var userId = 0
    private lateinit var btnLogOut: Button
    private lateinit var raBtn: RadioButton

    companion object {
        @JvmStatic
        fun startLogoutActivity(context: Context, userId: Int) {
            context.startActivity(Intent(context, LogoutActivity::class.java).putExtra(Constants.USER_ID, userId))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logout)
        userId = intent.getIntExtra(Constants.USER_ID, 0)
        init()
    }

    private fun init() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setToolbar(toolbar)
        val tvToolbar = findViewById<TextView>(R.id.tv_toolbar_title)
        tvToolbar.text = "注销账号"

        btnLogOut = findViewById(R.id.btn_logout)
        raBtn = findViewById(R.id.rabtn)
        initListener()

    }

    private fun initListener() {

        raBtn.setOnClickListener {
            if (raBtn.isChecked){
                btnLogOut.isClickable = true
                btnLogOut.isSelected = true
            }
        }
        btnLogOut.setOnClickListener {
            ShowToast.showCenter(this,"申请已提交")
            finish()
        }
    }
}