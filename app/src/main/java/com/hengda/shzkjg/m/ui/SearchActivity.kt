package com.hengda.shzkjg.m.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.hengda.shzkjg.m.R
import kotlinx.android.synthetic.main.head_search_detail.*
import kotlinx.android.synthetic.main.search_layout.*
import com.hengda.zwf.hdscanner.ScannerActivity
import com.hengda.zwf.hdscanner.ScanBuilder
import android.app.Activity
import android.content.ContentValues
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.text.TextUtils
import android.util.Log
import com.hengda.shzkjg.m.adapter.ExhibitAdapter
import com.hengda.shzkjg.m.base.AppConfig
import com.hengda.shzkjg.m.base.BaseActivity
import com.hengda.shzkjg.m.bean.Exhibit
import com.hengda.shzkjg.m.fragment.MapFragment
import com.hengda.shzkjg.m.tool.SpacesItemDecoration
import com.hengda.zwf.hdscanner.ScanConfig
import com.jakewharton.rxbinding.widget.RxTextView
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent
import com.mylhyl.zxing.scanner.common.Intents
import kotlinx.android.synthetic.main.activity_list_detail.*
import kotlinx.android.synthetic.main.tab_layout.*
import org.jetbrains.anko.db.MapRowParser
import org.jetbrains.anko.db.SelectQueryBuilder
import org.jetbrains.anko.db.select
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class SearchActivity : BaseActivity() {
    var exhibitList: List<Exhibit>? = mutableListOf<Exhibit>()
    //    var exhibitHistory: List<Exhibit>? = mutableListOf()
    var flag = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_layout)
        AppConfig.CONSTANT = 0
        val scanConfig = initScan()
        initListener(scanConfig)

        val manager = LinearLayoutManager(this, OrientationHelper.VERTICAL, false)
        rw_search.layoutManager = manager
        rw_search.addItemDecoration(SpacesItemDecoration(10))
        searchExhibit()
//        loadData()

    }

    override fun onResume() {
        super.onResume()
        loadData()
        rw_search.visibility = View.GONE
        flow_search.visibility = View.VISIBLE
        tv_clear_two.visibility = View.VISIBLE
    }

    private fun loadData() {
        var exhibitHistory: List<Exhibit>? = mutableListOf()
        flow_search.removeAllViews()
        AppConfig.database.use {
            exhibitHistory = select("MUSEUM_EXHIBIT ").whereSimple("temp>500 ORDER BY temp DESC").parseList { Exhibit(it as MutableMap<String, Any?>) }
        }
        if (exhibitHistory!!.size > 0) {
            tv_clear_two.text = "清除搜索记录"
        } else {
            tv_clear_two.text = "暂无搜索记录"
        }
        //sortedDescending
        if (exhibitHistory!!.size <= 5) {
            for (i in 0 until (exhibitHistory as List<Exhibit>).size) {
                var text: TextView = TextView(this)
                text.setBackgroundResource(R.mipmap.bg_flow)
                text.gravity = Gravity.CENTER
                text.text = (exhibitHistory as List<Exhibit>)[i].Name
                flow_search.addView(text)
                text.setOnClickListener {
                    startActivity<PlayActivity>("exhibit" to (exhibitHistory as List<Exhibit>)[i])
                }
            }
        } else {
            var temList = exhibitHistory!!.take(5)
            for (i in 0 until temList.size) {
                var text: TextView = TextView(this)
                text.setBackgroundResource(R.mipmap.bg_flow)
                text.gravity = Gravity.CENTER
                text.text = (exhibitHistory as List<Exhibit>)[i].Name
                flow_search.addView(text)
                text.setOnClickListener {
                    startActivity<PlayActivity>("exhibit" to (exhibitHistory as List<Exhibit>)[i])
                }
            }
        }
    }

    private fun searchExhibit() {
        RxTextView.textChangeEvents(et_search)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<TextViewTextChangeEvent>() {
                    override fun onCompleted() {}

                    override fun onError(e: Throwable) {}

                    override fun onNext(textViewTextChangeEvent: TextViewTextChangeEvent) {
                        val changedMessage = textViewTextChangeEvent.text().toString()
                        if (TextUtils.isEmpty(changedMessage)) {
                            flow_search.visibility = View.VISIBLE
                            tv_clear_two.visibility = View.VISIBLE
                            rw_search.visibility = View.GONE
                        } else {
                            flow_search.visibility = View.GONE
                            tv_clear_two.visibility = View.GONE
                            rw_search.visibility = View.VISIBLE
                            AppConfig.database.use {
                                var exhibitList = select("MUSEUM_EXHIBIT").whereSimple("IsExhibit=0 AND FileNo LIKE ? OR Name LIKE ?", "%${et_search.text}%", "%${et_search.text}%").parseList { Exhibit(it as MutableMap<String, Any?>) }
                                if (exhibitList.size == 0) {
                                    toast("无结果")
                                }
                                val adapter: ExhibitAdapter = ExhibitAdapter(R.layout.list_layout, exhibitList as MutableList<Exhibit>)
                                rw_search.adapter = adapter
                                adapter.notifyDataSetChanged()
                                adapter.setOnItemClickListener { adapter, view, position ->

                                    val intent = Intent(this@SearchActivity, PlayActivity::class.java)
                                    intent.putExtra("flag", flag)
                                    intent.putExtra("exhibit", adapter.getItem(position) as Exhibit)
                                    startActivity(intent)
                                }
                            }
                        }
                    }
                })
    }

    private fun initScan(): ScanConfig? {
        val scanConfig = ScanBuilder()
                .setTitle(R.string.scan)
                .setScanTip(R.string.scan_tip)
                .create()
        return scanConfig
    }

    private fun initListener(scanConfig: ScanConfig?) {
        tv_clear_two.setOnClickListener {
            flow_search.removeAllViews()
            tv_clear_two.visibility = View.VISIBLE
            tv_clear_two.text = "暂无搜索记录"
            AppConfig.database.use {
                var exhibitHistory: List<Exhibit>?
                exhibitHistory = select("MUSEUM_EXHIBIT ").whereSimple("temp>30 ORDER BY pk DESC").parseList { Exhibit(it as MutableMap<String, Any?>) }
//                exhibitHistory = select("MUSEUM_EXHIBIT ").whereSimple("pk>30 ORDER BY pk DESC").parseList { Exhibit(it as MutableMap<String, Any?>) }
                exhibitHistory.forEach {
                    //                    it.pk = it.temp
                    it.temp = it.pk
                    val values = ContentValues()
                    values.put("temp", it.pk)
                    val whereClause = "FileNo=?"//where子句，表示要操作的位置
                    val whereArgs = arrayOf(it.FileNo)//
                    update("MUSEUM_EXHIBIT", values, whereClause, whereArgs)
                }
            }
        }
        iv_back_common.setOnClickListener { finish() }
        iv_scan.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_CANCELED && resultCode == Activity.RESULT_OK) {
            if (requestCode == ScannerActivity.REQUEST_CODE_SCANNER) {
                if (data != null) {
                    val result = data.getStringExtra(Intents.Scan.RESULT)
                    val isNum = result.matches("[0-9]{0,8}".toRegex())//数字+最大长度8
                    if (isNum) {
                        val exhibits = AppConfig.database.use {
                            select("MUSEUM_EXHIBIT").whereSimple("FileNo=?", result.toString()).parseList { Exhibit(it as MutableMap<String, Any?>) }
                        }
                        if (exhibits.size > 0) {
                            val exhibit = exhibits[0]
                            if (exhibit.AutoNum != 0) {
                                startActivity<PlayActivity>("exhibit" to exhibit)
                            } else {
                                toast("您扫描的二维不存在，请选择正确的二维码扫描")
                            }
                        } else {
                            toast("您扫描的二维不存在，请选择正确的二维码扫描")
                        }
                    }
                }
            }
        }
    }

    fun <T : Any> SelectQueryBuilder.parseList(parser: (Map<String, Any?>) -> T): List<T> =
            parseList(object : MapRowParser<T> {
                override fun parseRow(columns: Map<String, Any?>): T = parser(columns)
            })

}
