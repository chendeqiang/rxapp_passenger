package com.mxingo.passenger.module

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import butterknife.ButterKnife
import com.mxingo.driver.module.BaseActivity
import com.mxingo.driver.utils.Constants
import com.mxingo.passenger.R
import com.mxingo.passenger.model.VersionEntity

class UpdateVersionActivity : BaseActivity() {

    private lateinit var tvUpdateContent: TextView
    private lateinit var btnUpdateIdOk: Button
    private lateinit var btnUpdateIdCancel: Button
    private var versionEntity: VersionEntity? = null

    companion object {
        @JvmStatic
        fun startUpdateVersionActivity(context: MainActivity, versionEntity: VersionEntity) {
            context.startActivity(Intent(context, UpdateVersionActivity::class.java).putExtra(Constants.ACTIVITY_DATA, versionEntity))
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_version)
        ButterKnife.bind(this)

        tvUpdateContent = findViewById<TextView>(R.id.tv_update_content)
        btnUpdateIdOk = findViewById<Button>(R.id.btn_update_id_ok)
        btnUpdateIdCancel = findViewById<Button>(R.id.btn_update_id_cancel)

        versionEntity = intent.getSerializableExtra(Constants.ACTIVITY_DATA) as VersionEntity
        tvUpdateContent.text = "版本:${versionEntity!!.version}\n大小: ${versionEntity!!.size}\n\n${versionEntity!!.log}"

        btnUpdateIdOk.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            val content_url = Uri.parse(versionEntity!!.url)
            intent.data = content_url
            startActivity(intent)
            if (!versionEntity!!.isMustUpdate) {
                finish()
            }
        }

        if (versionEntity!!.isMustUpdate) {
            btnUpdateIdCancel.visibility = View.GONE
        }
        btnUpdateIdCancel.setOnClickListener {
            if (!versionEntity!!.isMustUpdate) {
                finish()
            }
        }

    }

    override fun onBackPressed() {
        if (!versionEntity!!.isMustUpdate) {
            super.onBackPressed()
        }

    }
}
