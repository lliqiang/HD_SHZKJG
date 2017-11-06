package com.hengda.shzkjg.m.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

import ch.halcyon.squareprogressbar.SquareProgressBar
import cn.pedant.SweetAlert.SweetAlertDialog
import com.hengda.shzkjg.m.R
import com.hengda.shzkjg.m.base.AppConfig
import com.hengda.shzkjg.m.dialog.HProgressDialog
import com.hengda.shzkjg.m.down.ChinesePresenter
import com.hengda.shzkjg.m.down.LanguageContract
import com.hengda.shzkjg.m.tool.AppUtil
import com.hengda.shzkjg.m.tool.NetUtil
import com.hengda.shzkjg.m.tool.SDCardUtil
import com.hengda.shzkjg.m.tool.Utils
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import rx.Observable
import rx.Single
import java.io.File
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class SettingFragment : Fragment(), LanguageContract.View {
    lateinit var mPresenter: LanguageContract.Presenter
    lateinit var progressDialog: HProgressDialog
    override fun setPresenter(presenter: LanguageContract.Presenter?) {
        mPresenter = presenter!!
    }

    override fun showLanguage() {

    }

    override fun loadFailed() {
        activity.toast("下载失败")
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
        activity.toast("下载失败")
        progressDialog.hide()
    }

    override fun completed() {
        activity.toast("资源更新完成")
        progressDialog.hide()
    }

    override fun connected() {
        progressDialog = HProgressDialog(activity)
        progressDialog
                .tweenAnim(R.drawable.progress_roate, R.anim.progress_rotate)
                .showCancleButton(false)
                .outsideCancelable(false)
                .cancelable(false)
                .show()
    }

    companion object {
        fun newInstance(): SettingFragment {
            return SettingFragment()
        }
    }

    lateinit var progress: SquareProgressBar
    /**
     *  RxDownload.getInstance().download
     */
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var view: View = inflater!!.inflate(R.layout.fragment_setting, container, false)
        view.find<TextView>(R.id.tv_version).text = AppUtil.getVersionName(activity)
        var imgAuto = view.find<ImageView>(R.id.img_auto)
        ChinesePresenter(this)
        if (AppConfig.AUTO == 1) {
            imgAuto.setImageResource(R.mipmap.img_auto_on)
        } else {
            imgAuto.setImageResource(R.mipmap.img_auto_off)
        }
        imgAuto.setOnClickListener {
            if (AppConfig.AUTO == 1) {
                AppConfig.AUTO = 0
                imgAuto.setImageResource(R.mipmap.img_auto_off)
            } else {
                AppConfig.AUTO = 1
                imgAuto.setImageResource(R.mipmap.img_auto_on)
            }
        }
        view.find<Button>(R.id.btn_load).setOnClickListener { v ->
            if (NetUtil.isConnected(activity)) {
                SweetAlertDialog(activity, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setTitleText("资源下载")
                        .setContentText(getString(R.string.down_all))
                        .setConfirmText(getString(R.string.submit))
                        .setConfirmClickListener { dialog ->
                            dialog.dismiss()

                          doAsync {
                             while (AppConfig.isExist()){
                                 Utils.deleteDir(AppConfig.getDefaultFileDir())
                             }
                              uiThread {
                                  mPresenter!!.checkDb()
                              }
                          }

                        }
                        .setCancelText(getString(R.string.cancel))
                        .setCancelClickListener { dialog ->
                            dialog.dismiss()
                        }
                        .show()
            } else {
                SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("网络异常")
                        .setContentText("请在设置中打开WIFI,连接到馆方指定WIFI")
                        .setConfirmText("确定")
                        .setConfirmClickListener { dialog ->
                            dialog.dismiss()
                        }.show()
            }

        }

        return view
    }

    override fun onResume() {
        super.onResume()


    }
}
