package com.hengda.shzkjg.m.fragment


import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.zxing.Result
import com.google.zxing.client.result.ParsedResult

import com.hengda.shzkjg.m.R
import com.mylhyl.zxing.scanner.OnScannerCompletionListener
import kotlinx.android.synthetic.main.fragment_scan.*
import com.google.zxing.client.result.ParsedResultType
import android.widget.Toast
import com.google.zxing.client.result.TextParsedResult
import com.hengda.shzkjg.m.base.AppConfig
import com.hengda.shzkjg.m.bean.Exhibit
import com.hengda.shzkjg.m.ui.PlayActivity
import org.jetbrains.anko.db.MapRowParser
import org.jetbrains.anko.db.SelectQueryBuilder
import org.jetbrains.anko.db.select
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast


/**
 * A simple [Fragment] subclass.
 */
class ScanFragment : Fragment(), OnScannerCompletionListener {


    companion object {
        fun newInstance(): ScanFragment {
            return ScanFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_scan, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        scanner_view.setLaserFrameBoundColor(ContextCompat.getColor(activity, R.color.white));
        scanner_view.setLaserFrameCornerWidth(3);
        scanner_view.setLaserFrameCornerLength(15);
        scanner_view.setLaserColor(ContextCompat.getColor(activity, R.color.white));
        scanner_view.setLaserFrameSize(250, 250);
        scanner_view.setOnScannerCompletionListener(this);
        scanner_view.setDrawText(" ", 15, 0, false, 20);
    }

    override fun onResume() {
        scanner_view.onResume()
        super.onResume()
    }

    override fun onPause() {
        scanner_view.onPause()
        super.onPause()
    }

    override fun OnScannerCompletion(rawResult: Result?, parsedResult: ParsedResult?, barcode: Bitmap?) {
        if (rawResult == null) {
            Toast.makeText(activity, "未发现条形码", Toast.LENGTH_SHORT).show()
            return
        }
        val textParsedResult = (parsedResult as TextParsedResult).toString()
        val isNum = textParsedResult.matches("[0-9]{0,8}".toRegex())//数字+最大长度8
        if (isNum) {
            val exhibits = AppConfig.database.use {
                select("MUSEUM_EXHIBIT").whereSimple("FileNo=?", textParsedResult.toString()).parseList { Exhibit(it as MutableMap<String, Any?>) }
            }
            if (exhibits.size > 0) {
                val exhibit = exhibits[0]
                if (exhibit.AutoNum != 0) {
                    activity.startActivity<PlayActivity>("exhibit" to exhibit)
                } else {
                    activity.toast("您扫描的二维不存在，请选择正确的二维码扫描")
                    scanner_view.restartPreviewAfterDelay(300)
                }
            } else {
                activity.toast("您扫描的二维不存在，请选择正确的二维码扫描")
                scanner_view.restartPreviewAfterDelay(300)
            }
        }else {
            activity.toast("您扫描的二维不存在，请选择正确的二维码扫描")
            scanner_view.restartPreviewAfterDelay(300)
        }

    }

    fun <T : Any> SelectQueryBuilder.parseList(parser: (Map<String, Any?>) -> T): List<T> =
            parseList(object : MapRowParser<T> {
                override fun parseRow(columns: Map<String, Any?>): T = parser(columns)
            })
}// Required empty public constructor
