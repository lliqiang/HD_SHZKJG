package com.hengda.shzkjg.m.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.view.animation.ScaleAnimation
import com.chad.library.adapter.base.BaseQuickAdapter
import com.hengda.frame.tileview.effect.animation.ScaleIn
import com.hengda.shzkjg.m.R
import com.hengda.shzkjg.m.adapter.ExhibitAdapter
import com.hengda.shzkjg.m.base.AppConfig
import com.hengda.shzkjg.m.base.BaseActivity
import com.hengda.shzkjg.m.bean.Exhibit
import com.hengda.shzkjg.m.bean.Exhibition
import com.hengda.shzkjg.m.tool.SpacesItemDecoration
import kotlinx.android.synthetic.main.activity_list_detail.*
import kotlinx.android.synthetic.main.head_common.*
import org.jetbrains.anko.db.MapRowParser
import org.jetbrains.anko.db.SelectQueryBuilder
import org.jetbrains.anko.db.select
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity
import kotlin.properties.Delegates

class ListDetailActivity : BaseActivity() {
    var exhibition: Exhibition by Delegates.notNull()
    var exhibitList: List<Exhibit>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_detail)
        AppConfig.CONSTANT = 0
        exhibition = intent.getSerializableExtra("Exhibition") as Exhibition
        iv_back.setOnClickListener {
            AppConfig.TYPECONFIG=0
            finish() }
        tv_title_common.text = exhibition.ExhibitName
        val manager = LinearLayoutManager(this, OrientationHelper.VERTICAL, false)
        rw_listDetail.layoutManager = manager as RecyclerView.LayoutManager?

        rw_listDetail.addItemDecoration(SpacesItemDecoration(10))
        exhibitList = mutableListOf<Exhibit>()
        AppConfig.database.use {
            exhibitList = select("MUSEUM_EXHIBIT").whereSimple("IsExhibit=0 AND MapId=? AND ExhibitId=?", exhibition.MapId.toString(), exhibition.ExhibitId.toString()).parseList { Exhibit(it as MutableMap<String, Any?>) }
        }
        val adapter: ExhibitAdapter = ExhibitAdapter(R.layout.list_layout, exhibitList as MutableList<Exhibit>)
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM)
        info { "exhibitList: ${(exhibitList as MutableList<Exhibit>).size}" }
        rw_listDetail.adapter = adapter
        adapter.notifyDataSetChanged()
        adapter.setOnItemClickListener { adapter, view, position ->
            startActivity<PlayActivity>("exhibit" to adapter.getItem(position) as Exhibit)
        }
    }


    fun <T : Any> SelectQueryBuilder.parseList(parser: (Map<String, Any?>) -> T): List<T> =
            parseList(object : MapRowParser<T> {
                override fun parseRow(columns: Map<String, Any?>): T = parser(columns)
            })
}
