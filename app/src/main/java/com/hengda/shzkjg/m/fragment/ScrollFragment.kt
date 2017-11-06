package com.hengda.shzkjg.m.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hengda.shzkjg.m.R
import com.hengda.shzkjg.m.adapter.ScrollAdapter
import com.hengda.shzkjg.m.base.AppConfig
import com.hengda.shzkjg.m.bean.Exhibition
import com.hengda.shzkjg.m.ui.ListDetailActivity
import com.yarolegovich.discretescrollview.DiscreteScrollView
import kotlinx.android.synthetic.main.fragment_scroll.*
import org.jetbrains.anko.db.MapRowParser
import org.jetbrains.anko.db.SelectQueryBuilder
import org.jetbrains.anko.db.select
import org.jetbrains.anko.startActivity
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import kotlinx.android.synthetic.main.head_search_detail.*

/**
 * A simple [Fragment] subclass.
 */
class ScrollFragment : Fragment() {
    var currentPosition: Int = 0

    companion object {
        fun newInstance(): ScrollFragment {
            return ScrollFragment()
        }
    }

    var exhibitionList: List<Exhibition>? = null
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        exhibitionList = mutableListOf<Exhibition>()
        AppConfig.database.use {
            exhibitionList = select("EXHIBIT_ROOM ORDER BY MapId,ExhibitId ").parseList { Exhibition(it as MutableMap<String, Any?>) }
        }
        return inflater!!.inflate(R.layout.fragment_scroll, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rw_scroll.setItemTransformer(ScaleTransformer.Builder()
                .setMinScale(0.85f)
                .build())
        val adapter = ScrollAdapter(R.layout.item_scroll, exhibitionList as MutableList<Exhibition>)
        adapter.openLoadAnimation()
        rw_scroll.adapter = adapter
        adapter.notifyDataSetChanged()
        adapter.setOnItemClickListener { adapter, view, position ->
            activity.startActivity<ListDetailActivity>("Exhibition" to (adapter.getItem(position) as Exhibition))
        }
        rw_scroll.setCurrentItemChangeListener(DiscreteScrollView.CurrentItemChangeListener<RecyclerView.ViewHolder>
        { viewHolder, adapterPosition ->
            currentPosition = adapterPosition
            tv_currentNo.text = "${currentPosition+1}/${exhibitionList?.size}"
        })
    }

    fun <T : Any> SelectQueryBuilder.parseList(parser: (Map<String, Any?>) -> T): List<T> =
            parseList(object : MapRowParser<T> {
                override fun parseRow(columns: Map<String, Any?>): T = parser(columns)
            })
}
