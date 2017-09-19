package com.hengda.shzkjg.m.base

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import cn.pedant.SweetAlert.SweetAlertDialog
import com.hengda.shzkjg.m.R
import com.hengda.shzkjg.m.tool.StatusBarCompat
import com.jaeger.library.StatusBarUtil
import org.jetbrains.anko.AnkoLogger

open class BaseActivity : AppCompatActivity(), AnkoLogger {
    var pDialog: SweetAlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        StatusBarUtil.setColorNoTranslucent(this,R.color.blue1)
//        StatusBarUtil.setColor(this, R.color.blue1)
//        StatusBarCompat.setStatusBarColor(this, resources.getColor(R.color.blue1))
//        StatusBarCompat.setWindowStatusBarColor(this, resources.getColor(R.color.blue1))
        initProgressBar()
    }

    fun initProgressBar() {
        pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog!!.getProgressHelper().setBarColor(Color.parseColor("#66B8EE"))
    }
}
