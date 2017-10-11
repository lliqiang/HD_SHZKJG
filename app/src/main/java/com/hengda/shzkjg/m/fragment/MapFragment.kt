package com.hengda.shzkjg.m.fragment


import android.animation.ValueAnimator
import android.content.Intent
import android.database.Cursor
import android.media.Image
import android.os.Bundle
import android.os.Handler
import android.support.annotation.IntegerRes
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.hengda.frame.numreceiver.listener.NumManager
import com.hengda.frame.tileview.HDTileView
import com.hengda.frame.tileview.effect.animation.LoadAnimFactory

import com.hengda.shzkjg.m.R
import com.hengda.shzkjg.m.base.AppConfig
import com.hengda.shzkjg.m.bean.Exhibit
import com.hengda.shzkjg.m.bean.MapInfo
import com.hengda.shzkjg.m.ui.PlayActivity
import kotlinx.android.synthetic.main.mark_layout.*
import kotlinx.android.synthetic.main.tab_layout.*
import org.altbeacon.beacon.Beacon
import org.jetbrains.anko.*
import org.jetbrains.anko.db.*
import kotlin.concurrent.fixedRateTimer
import kotlin.properties.Delegates


/**
 * A simple [Fragment] subclass.
 */
class MapFragment : Fragment(), AnkoLogger, NumManager.OnNumChangeListener {

    var tileView: HDTileView? = null
    var path: String by Delegates.notNull()
    var path1: String by Delegates.notNull()
    var mapList: List<MapInfo> by Delegates.notNull()
    var exhibitList: List<Exhibit>? = null
    var floor: Int = 1
    var type: Int = 0
    var route: Int = 0
    val viewList = mutableListOf<View>()
    var lastNum: Int = 0
    lateinit var imgRoute: ImageView
    var isStartBle = false

    companion object {
        fun newInstance(floor: Int, type: Int, route: Int): MapFragment {
            val fragment: MapFragment = MapFragment()
            val bundle = Bundle()
            bundle.putInt("floor", floor)
            bundle.putInt("type", type)
            bundle.putInt("route", route)
            fragment.setArguments(bundle)
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tileView = HDTileView(activity)
        floor = arguments.getInt("floor")
        type = arguments.getInt("type")
        route = arguments.getInt("route")
        AppConfig.CONSTANT == 1
        imgRoute = ImageView(activity)
        NumManager.registerNumChangeListener(this);
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (type == 0) {
            if (route == 1) {
                when (floor) {
                    1 -> Glide.with(activity).load(AppConfig.getMapPath(floor) + "/" + "route_one.png").into(imgRoute)
                    2 -> Glide.with(activity).load(AppConfig.getMapPath(floor) + "/" + "route_two.png").into(imgRoute)
                    3 -> Glide.with(activity).load(AppConfig.getMapPath(floor) + "/" + "route_three.png").into(imgRoute)
                }
            }

        }
        loadData()
        initTileView()
        addMark(exhibitList!!)
        return tileView

    }

    private fun loadData() {
        AppConfig.database.use {
            mapList = select("MAP_INFO").whereSimple("id=?", floor.toString()).parseList { MapInfo(it as MutableMap<String, Any?>) }
        }
        AppConfig.database.use {
                exhibitList = select("MUSEUM_EXHIBIT").whereSimple("MapId=? AND IsExhibit=?", floor.toString(), type.toString()).parseList { Exhibit(it as MutableMap<String, Any?>) }

        }
    }

    private fun initTileView() {
        tileView!!.setMinimumScaleFullScreen()
        path = AppConfig.getMapPath(floor)
        tileView!!.init(4.0f, 0.5f, mapList.get(0).Width, mapList.get(0).Height, path)
        tileView!!.loadMapFromDisk()
        tileView!!.setMinimumScaleFullScreen()
        tileView!!.addSample(path + "/img.png", false)
        if (imgRoute.parent != null) {
            (imgRoute.parent as ViewGroup).removeView(imgRoute)
        }
        tileView!!.addView(imgRoute)
        Handler().postDelayed(Runnable {
            if (tileView != null) {
                tileView!!.slideToAndCenterWithScale((mapList.get(0).Width), (mapList.get(0).Height),if (floor==3) 1.5f else 2.0f)
                isStartBle = true
            }
        }, 100)


    }

    fun addMark(exhibits: List<Exhibit>) {
        if (type == 0) {
            exhibits.forEach { it ->
                var markView: View = View.inflate(activity, R.layout.mark_layout, null)
                var markImg: ImageView = markView.find<ImageView>(R.id.iv_mark)
                var tv_Name = markView.find<TextView>(R.id.tv_markName)
                tv_Name.isSelected = true
                var dotImg = markView.find<ImageView>(R.id.iv_dot)
                tv_Name.text = it.Name
                tv_Name.isSelected = true

                if (it.IsExhibit == 0) {
                    if (it.IsRead == 0) {
                        dotImg.setImageResource(R.mipmap.img_dot_no)
                    } else {
                        dotImg.setImageResource(R.mipmap.img_dot_yes)
                    }
                } else {
                    dotImg.visibility = View.INVISIBLE
                }

                Glide.with(activity).load(AppConfig.getMarkPath(it.FileNo))
                        .placeholder(R.mipmap.img_mark_default).error(R.mipmap.img_mark_default).into(markImg)
                markView.tag = it.AutoNum
                viewList.add(markView)
                if (it.LocX != 0) {
                    tileView!!.addMarker(markView, it.LocX.toDouble(), it.LocY.toDouble(), -0.5f, -1.0f)
                }
                if (type == 0) {
                    markView.setOnClickListener { v ->
                        activity.startActivity<PlayActivity>("exhibit" to it)
                    }
                } else {
                    tv_Name.visibility = View.VISIBLE

                }
            }
        } else {
            exhibits.forEach { it ->
                var markView: View = View.inflate(activity, R.layout.device_layout, null)
                var tv_Name = markView.find<TextView>(R.id.tv_device_name)
                tv_Name.isSelected = true
                tv_Name.text = it.Name
                if (it.LocX != 0) {
                    tileView!!.addMarker(markView, it.LocX.toDouble(), it.LocY.toDouble(), -0.5f, -1.0f)
                }
            }
        }

    }

    override fun OnBeaconListChange(beacons: MutableList<Beacon>?) {

    }

    override fun OnNumChange(num: Int) {
        if (exhibitList!!.filter { it.AutoNum == num }.size > 0 && AppConfig.AUTO == 1 && isStartBle) {
            if (isReplay(num)) {
                viewList.forEach {
                    recoverAnim(it)
                    it.find<TextView>(R.id.tv_markName).visibility = View.GONE
                }
                viewList.filter { it.tag == num }.forEach {
                    scaleAnim(it)
                }
                if (exhibitList!!.filter { it.AutoNum == num }[0].LocX != 0) {
                    tileView!!.slideToPositionWithScale(exhibitList!!.filter { it.AutoNum == num }[0].LocX.toDouble(), exhibitList!!.filter { it.AutoNum == num }[0].LocY.toDouble(), 2.0f, 50)
                }
            }
        }
    }

    fun <T : Any> SelectQueryBuilder.parseList(parser: (Map<String, Any?>) -> T): List<T> =
            parseList(object : MapRowParser<T> {
                override fun parseRow(columns: Map<String, Any?>): T = parser(columns)
            })

    fun scaleAnim(view: View) {
        var anim: ValueAnimator = ValueAnimator.ofFloat(1.5f)
        anim.setDuration(200)
        anim.start()
        anim.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation ->
            view.scaleX = animation.animatedValue as Float
            view.scaleY = animation.animatedValue as Float
            view.find<TextView>(R.id.tv_markName).visibility = View.VISIBLE
        })
    }

    fun recoverAnim(view: View) {

        var anim: ValueAnimator = ValueAnimator.ofFloat(1.0f)
        anim.setDuration(200)
        anim.start()
        anim.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation ->
            view.scaleX = animation.animatedValue as Float
            view.scaleY = animation.animatedValue as Float

        })
    }

    fun isReplay(num: Int): Boolean {
        var temp_flag = false
        if (num != 0 && num != lastNum) {
            lastNum = num
            temp_flag = true
        }
        return temp_flag
    }

    override fun onDestroy() {
        super.onDestroy()
        NumManager.unregisterNumChangeListener(this)
        if (tileView != null) {
            tileView!!.removeAllViews()
            tileView = null
        }
    }

}
