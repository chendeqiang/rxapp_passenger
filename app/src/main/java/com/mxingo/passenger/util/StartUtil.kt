package com.mxingo.passenger.util

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import com.mxingo.passenger.dialog.MessageDialog



object StartUtil {

    fun callMobile(mobile: String, activity: Activity) {
        if (TextUtil.isEmpty(mobile)) {
            return
        }
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CALL_PHONE), 1)
        } else {
            val dialog = MessageDialog(activity)
            dialog.setMessageText("" + mobile)
            dialog.setOkText("呼叫")
            dialog.setOnCancelClickListener {
                dialog.dismiss()
            }
            dialog.setOnOkClickListener{
                dialog.dismiss()
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobile))
                activity.startActivity(intent)
            }
            dialog.show()

        }

    }


    fun call110Mobile(mobile: String, activity: Activity) {
        if (TextUtil.isEmpty(mobile)) {
            return
        }
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CALL_PHONE), 1)
        } else {
            val dialog = MessageDialog(activity)
            dialog.setMessageText("" + mobile)
            dialog.setOkText("拨打110")
            dialog.setCancelText("联系客服")
//            dialog.setOnCancelClickListener {
//                dialog.dismiss()
//            }
            dialog.setOnOkClickListener{
                dialog.dismiss()
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "110"))
                activity.startActivity(intent)
            }

            dialog.setOnCancelClickListener {
                dialog.dismiss()
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "4008878810"))
                activity.startActivity(intent)
            }
            dialog.show()

        }

    }

}
