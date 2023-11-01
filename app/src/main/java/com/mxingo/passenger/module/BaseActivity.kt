package com.mxingo.driver.module


import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import android.widget.Toast
import com.mxingo.passenger.MyApplication
import com.mxingo.passenger.R
import com.mxingo.passenger.module.ActivityCollector
import com.mxingo.passenger.module.MainActivity
import com.mxingo.passenger.util.HttpUtil
import com.mxingo.passenger.widget.ShowToast


@Suppress("DEPRECATED_IDENTITY_EQUALS")
open class BaseActivity : AppCompatActivity() {


    //退出时间
    private var currentBackPressedTime : Long =0
    //退出间隔
    private val BACK_PRESSED_INTERVAL= 2000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCollector.addActivity(this)
    }

    fun setToolbar(toolbar: Toolbar) {
        toolbar.title = ""
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId === android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        HttpUtil.checkNetwork(this)
//        if (this::class.java.equals(StartPageActivity::class.java)) {
//            MobclickAgent.onPageStart(this.packageName)
//        }
//        MobclickAgent.onResume(this)
    }

    override fun onPause() {
        super.onPause()
//        if (this::class.java.equals(StartPageActivity::class.java)) {
//            MobclickAgent.onPageEnd(this.packageName)
//        }
//        MobclickAgent.onPause(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
//        if (this::class.java.name.equals(MainActivity::class.java.name) && MyApplication.currActivity.equals(MainActivity::class.java.name)) {
//            MyApplication.currActivity = ""
//        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            exitApp()
            return false
        }
        return super.onKeyDown(keyCode,event)
    }

    private fun exitApp() {
        if((System.currentTimeMillis()-currentBackPressedTime)>2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            currentBackPressedTime = System.currentTimeMillis()
        }else{
            ActivityCollector.finishAll()
            //System.exit(0)
            android.os.Process.killProcess(android.os.Process.myPid())
        }

    }
}
