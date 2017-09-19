package com.hengda.shzkjg.m.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.hengda.shzkjg.m.R
import com.hengda.shzkjg.m.base.App
import com.hengda.shzkjg.m.base.AppConfig
import com.hengda.shzkjg.m.base.BaseActivity
import com.hengda.shzkjg.m.dialog.HProgressDialog
import com.hengda.shzkjg.m.down.ChinesePresenter
import com.hengda.shzkjg.m.down.LanguageContract
import com.hengda.shzkjg.m.fragment.MapFragment
import com.hengda.shzkjg.m.tool.NetUtil
import com.hengda.shzkjg.m.tool.Utils
import com.othershe.nicedialog.BaseNiceDialog
import com.othershe.nicedialog.NiceDialog
import com.othershe.nicedialog.ViewConvertListener
import com.othershe.nicedialog.ViewHolder
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import rx.Observable
import java.util.concurrent.TimeUnit

class LaucherActivity : BaseActivity(), LanguageContract.View {
    lateinit var mPresenter: LanguageContract.Presenter
    lateinit var progressDialog: HProgressDialog
    var wifiSSID = NetUtil.getWifiSSID(App.instance)
    var dialog: NiceDialog = NiceDialog.init()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_laucher)
        ChinesePresenter(this)
        if (!AppConfig.isResExist()) {
            if (NetUtil.isConnected(this)) {
                if (NetUtil.isWifi(App.instance)) {
                    if (wifiSSID.contains(AppConfig.DEFAULT_SSID)) {
                        SweetAlertDialog(this@LaucherActivity, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                                .setTitleText("资源下载")
                                .setContentText(getString(R.string.down_all))
                                .setConfirmText(getString(R.string.submit))
                                .setConfirmClickListener { dialog ->
                                    dialog.dismiss()
                                    mPresenter?.checkDb()
                                }
                                .setCancelText(getString(R.string.cancel))
                                .setCancelClickListener { dialog ->
                                    dialog.dismiss()
                                    finish()
                                }
                                .show()
                    } else {
                        dialog.setLayoutId(R.layout.dialog_resource)
                                .setConvertListener(object : ViewConvertListener {
                                    override fun convertView(holder: ViewHolder, dialog: BaseNiceDialog) {
                                        holder.convertView.find<TextView>(R.id.tv_know).setOnClickListener {
                                            dialog.dismiss()
                                            mPresenter?.checkDb()
                                        }
                                    }
                                })
                                .setDimAmount(0.3f)
                                .setShowBottom(false)
                                .setOutCancel(false)
                                .setMargin(30)
                                .show(supportFragmentManager)
                    }
                } else {
                    SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("网络异常")
                            .setContentText("请在设置中打开WIFI,连接到馆方指定WIFI")
                            .setConfirmText("确定")
                            .setConfirmClickListener { dialog ->
                                dialog.dismiss()
                                finish()
                            }.show()
                }
            } else {
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("网络异常")
                        .setContentText("请在设置中打开WIFI,连接到馆方指定WIFI")
                        .setConfirmText("确定")
                        .setConfirmClickListener { dialog ->
                            dialog.dismiss()
                            finish()
                        }.show()
            }


        } else {
            Observable.timer(2, TimeUnit.SECONDS).subscribe {
                startActivity<MainActivity>()
                finish()
            }
        }

    }

    override fun setPresenter(presenter: LanguageContract.Presenter?) {
        mPresenter = presenter!!
    }

    override fun showLanguage() {

    }

    override fun loadFailed() {
        toast("下载失败")
    }

    override fun progress(soFarBytes: Int, totalBytes: Int) {
        if (soFarBytes == totalBytes)
            progressDialog.message("解压中...")
        else if (soFarBytes != 0) {
            progressDialog.process(String.format("%s/%s",
                    Utils.getFormatSize(soFarBytes.toDouble()),
                    Utils.getFormatSize(totalBytes.toDouble())))
        }
    }

    override fun speed(speed: Int) {
        progressDialog.speed(speed.toString() + "kb/s")
    }

    override fun error() {
        toast("下载失败")
    }

    override fun completed() {
        Observable.timer(1, TimeUnit.SECONDS).subscribe {
            startActivity<MainActivity>()
            finish()
        }
    }

    override fun connected() {
        progressDialog = HProgressDialog(this@LaucherActivity)
        progressDialog
                .tweenAnim(R.drawable.progress_roate, R.anim.progress_rotate)
                .showCancleButton(false)
                .outsideCancelable(false)
                .cancelable(false)
                .show()
    }

    override fun onResume() {
        super.onResume()

    }
}
