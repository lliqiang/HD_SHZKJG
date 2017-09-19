package com.hengda.shzkjg.m.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.hengda.shzkjg.m.R
import com.hengda.shzkjg.m.base.AppConfig

import org.jetbrains.anko.db.SelectQueryBuilder

import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.widget.LinearLayout
import com.hengda.shzkjg.m.adapter.ExhibitionAdapter
import com.hengda.shzkjg.m.bean.Exhibit
import com.hengda.shzkjg.m.bean.Exhibition
import com.hengda.shzkjg.m.tool.TabAdapter
import com.hengda.shzkjg.m.ui.ListDetailActivity
import kotlinx.android.synthetic.main.fragment_common_list.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.db.MapRowParser
import org.jetbrains.anko.db.select
import org.jetbrains.anko.find
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity
import kotlin.properties.Delegates


/**
 * A simple [Fragment] subclass.
 */
class CommonListFragment : Fragment(), AnkoLogger {
    var class_id: Int by Delegates.notNull()


    companion object {
        fun newInstance(class_id: Int): CommonListFragment {
            val fragment: CommonListFragment = CommonListFragment()
            val bundle = Bundle()
            bundle.putInt("class_id", class_id)
            fragment.setArguments(bundle)
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        class_id = arguments.getInt("class_id")
    }

    var exhibitionList: List<Exhibition>? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater!!.inflate(R.layout.fragment_common_list, container, false)
        val rw_commonList = view.find<RecyclerView>(R.id.rw_commonList)
        val manager = LinearLayoutManager(activity, OrientationHelper.VERTICAL, false)
        rw_commonList.layoutManager = manager
        exhibitionList = mutableListOf<Exhibition>()
        AppConfig.database.use {
            exhibitionList = select("EXHIBIT_ROOM").whereSimple("MapId=?", class_id.toString()).parseList { Exhibition(it as MutableMap<String, Any?>) }
        }
        val adapter = ExhibitionAdapter(R.layout.list_layout, exhibitionList as MutableList<Exhibition>)
        adapter.openLoadAnimation()
        rw_commonList.adapter = adapter
        adapter.notifyDataSetChanged()
        adapter.setOnItemClickListener { adapter, view, position ->
          activity.startActivity<ListDetailActivity>("Exhibition" to (adapter.getItem(position) as Exhibition))
        }
        return view
    }

    fun <T : Any> SelectQueryBuilder.parseList(parser: (Map<String, Any?>) -> T): List<T> =
            parseList(object : MapRowParser<T> {
                override fun parseRow(columns: Map<String, Any?>): T = parser(columns)
            })
}
