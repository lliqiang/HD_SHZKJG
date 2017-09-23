package com.hengda.shzkjg.m.ui

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.*

import com.hengda.frame.numreceiver.HDNumService
import com.hengda.frame.numreceiver.listener.NumManager
import com.hengda.shzkjg.m.R
import com.hengda.shzkjg.m.base.AppConfig
import com.hengda.shzkjg.m.bean.Exhibit
import com.hengda.shzkjg.m.bean.MapInfo
import com.hengda.shzkjg.m.fragment.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.head_search.*
import org.jetbrains.anko.*
import com.othershe.nicedialog.BaseNiceDialog
import com.othershe.nicedialog.ViewConvertListener
import com.othershe.nicedialog.NiceDialog
import com.othershe.nicedialog.ViewHolder
import com.tencent.bugly.crashreport.CrashReport
import kotlinx.android.synthetic.main.dialog_switch_floor.*
import org.altbeacon.beacon.Beacon
import org.jetbrains.anko.db.MapRowParser
import org.jetbrains.anko.db.SelectQueryBuilder
import org.jetbrains.anko.db.select
import kotlin.properties.Delegates


class MainActivity : UpdateActivity(), AnkoLogger, NumManager.OnNumChangeListener {
    lateinit var tipView: View
    lateinit var tvTip: TextView
    val inroFrg: IntroFragment = IntroFragment.newInstance()
    val scanFrg: ScanFragment = ScanFragment.newInstance()
    val callFrg: CallFragment = CallFragment.newInstance()
    val mapFrg: MapFragment = MapFragment.newInstance(1, 0, 0)
    val listFrg: ListFragment = ListFragment.newInstance()
    val scrolltFrg: ScrollFragment = ScrollFragment.newInstance()
    val aboutFrg: AboutFragment = AboutFragment.newInstance()
    val settingFrg: SettingFragment = SettingFragment.newInstance()
    var dialog: NiceDialog = NiceDialog.init()
    var lastTime: Long = 0
    var lastNum: Int = 0
    var mapList: List<MapInfo> by Delegates.notNull()
    var floor: Int = 1
    var flag: Boolean = false
    var type: Int = 0
    var route: Int = 0

    var exhibitList: List<Exhibit>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        checkUpdata()
        HDNumService.startService(this, 1, 0, 5.0, -90)
        NumManager.registerNumChangeListener(this)
        tipView = View.inflate(this, R.layout.layout_guide_tip, null)
        tvTip = tipView.find<TextView>(R.id.tv_tip)
        setListener()
        iv_socket_map.visibility = View.VISIBLE
        ll_swipe.visibility = View.VISIBLE
        initTip()
        //查询展厅和展品
        AppConfig.database.use {
            mapList = select("MAP_INFO").parseList { MapInfo(it as MutableMap<String, Any?>) }
        }
        AppConfig.CONSTANT = 0
        tv_search.visibility = View.VISIBLE
        tv_common_title.visibility = View.GONE
        ll_swipe.visibility = View.GONE
        tv_search.visibility = View.GONE
        iv_search_common.visibility = View.VISIBLE
        draw_layout.closeDrawers()
        showSocket(iv_socket_list)
        supportFragmentManager.beginTransaction().replace(R.id.container_main,scrolltFrg).commitAllowingStateLoss()
    }

    private fun initTip() {
        if (AppConfig.FirstConfig) {
            iv_flow.visibility = View.VISIBLE
            btn_jump_tip.visibility = View.VISIBLE
            AppConfig.FirstConfig = false
        } else {

            iv_flow.visibility = View.GONE
            btn_jump_tip.visibility = View.GONE
        }
        iv_flow.setOnClickListener {
            iv_flow.setImageResource(R.mipmap.bg_flow_route)
            btn_jump_tip.text = "跳过"
            iv_flow.setOnClickListener {
                iv_flow.setImageResource(R.mipmap.bg_flow_device)
                btn_jump_tip.text = "确定"
                iv_flow.setOnClickListener {
                    iv_flow.visibility = View.GONE
                    btn_jump_tip.visibility = View.GONE
                }
            }
        }
        btn_jump_tip.setOnClickListener {
            iv_flow.visibility = View.GONE
            btn_jump_tip.visibility = View.GONE
        }
    }

    fun setListener() {
        iv_menu.setOnClickListener { draw_layout.openDrawer(Gravity.LEFT) }
        tv_search.setOnClickListener { startActivity<SearchActivity>() }

        iv_switch_floor.setOnClickListener {
            AppConfig.ISHOME = false
            var dialog1: NiceDialog = NiceDialog.init()
            dialog1.setLayoutId(R.layout.dialog_switch_floor)
                    .setConvertListener(object : ViewConvertListener {
                        override fun convertView(holder: ViewHolder, dialog: BaseNiceDialog) {
                            holder.convertView.find<ImageView>(R.id.iv_close_dialog).setOnClickListener { dialog.dismiss() }
                            holder.convertView.find<RadioButton>(R.id.rb_floor_one).setOnClickListener { dialog.dismiss() }
                            holder.convertView.find<RadioButton>(R.id.rb_floor_two).setOnClickListener { dialog.dismiss() }
                            holder.convertView.find<RadioButton>(R.id.rb_floor_three).setOnClickListener { dialog.dismiss() }
                            holder.convertView.find<RadioGroup>(R.id.rp_switch_floor).setOnCheckedChangeListener { group, checkedId ->
                                when (checkedId) {

                                    R.id.rb_floor_one -> {
                                        iv_switch_floor.setImageResource(R.mipmap.img_floor_one)
                                        AppConfig.CONSTANT = 1
                                        val mapFrg: MapFragment = MapFragment.newInstance(1, type, route)
                                        floor = 1
                                        holder.convertView.find<RadioButton>(R.id.rb_floor_one).setTextColor(resources.getColor(R.color.white))
                                        holder.convertView.find<RadioButton>(R.id.rb_floor_two).setTextColor(resources.getColor(R.color.black))
                                        holder.convertView.find<RadioButton>(R.id.rb_floor_three).setTextColor(resources.getColor(R.color.black))
                                        supportFragmentManager.beginTransaction().replace(R.id.container_main, mapFrg).commit()

                                    }

                                    R.id.rb_floor_two -> {
                                        iv_switch_floor.setImageResource(R.mipmap.img_floor_two)
                                        AppConfig.CONSTANT = 1
                                        floor = 2
                                        holder.convertView.find<RadioButton>(R.id.rb_floor_one).setTextColor(resources.getColor(R.color.black))
                                        holder.convertView.find<RadioButton>(R.id.rb_floor_two).setTextColor(resources.getColor(R.color.white))
                                        holder.convertView.find<RadioButton>(R.id.rb_floor_three).setTextColor(resources.getColor(R.color.black))
                                        val mapFrg: MapFragment = MapFragment.newInstance(2, type, route)
                                        supportFragmentManager.beginTransaction().replace(R.id.container_main, mapFrg).commit()

                                    }
                                    R.id.rb_floor_three -> {
                                        iv_switch_floor.setImageResource(R.mipmap.img_floor_three)
                                        AppConfig.CONSTANT = 1
                                        floor = 3
                                        holder.convertView.find<RadioButton>(R.id.rb_floor_one).setTextColor(resources.getColor(R.color.black))
                                        holder.convertView.find<RadioButton>(R.id.rb_floor_two).setTextColor(resources.getColor(R.color.black))
                                        holder.convertView.find<RadioButton>(R.id.rb_floor_three).setTextColor(resources.getColor(R.color.white))
                                        val mapFrg: MapFragment = MapFragment.newInstance(3, type, route)
                                        supportFragmentManager.beginTransaction().replace(R.id.container_main, mapFrg).commit()
                                    }

                                }

                            }
                            when (floor) {
                                1 -> holder.convertView.find<RadioButton>(R.id.rb_floor_one).isChecked = true
                                2 -> holder.convertView.find<RadioButton>(R.id.rb_floor_two).isChecked = true
                                3 -> holder.convertView.find<RadioButton>(R.id.rb_floor_three).isChecked = true
                            }

                        }
                    })
                    .setDimAmount(0.3f)
                    .setShowBottom(false)
                    .setOutCancel(true)
                    .show(supportFragmentManager)

        }


        iv_route.setOnClickListener {
            var dialog1: NiceDialog = NiceDialog.init()
            dialog1.setLayoutId(R.layout.dialog_switch_route)
                    .setConvertListener(object : ViewConvertListener {
                        override fun convertView(holder: ViewHolder, dialog: BaseNiceDialog) {
                            holder.convertView.find<ImageView>(R.id.iv_close_dialog_route).setOnClickListener { dialog.dismiss() }
                            holder.convertView.find<RadioButton>(R.id.rb_route_all).setOnClickListener { dialog.dismiss() }
                            holder.convertView.find<RadioButton>(R.id.rb_route_classics).setOnClickListener { dialog.dismiss() }
                            holder.convertView.find<RadioGroup>(R.id.rp_route).setOnCheckedChangeListener { group, checkedId ->
                                when (checkedId) {
                                    R.id.rb_route_all -> {
                                        AppConfig.CONSTANT = 1
                                        route = 0
                                        val mapFrg: MapFragment = MapFragment.newInstance(floor, type, route)
                                        holder.convertView.find<RadioButton>(R.id.rb_route_all).setTextColor(resources.getColor(R.color.white))
                                        holder.convertView.find<RadioButton>(R.id.rb_route_classics).setTextColor(resources.getColor(R.color.black))
                                        supportFragmentManager.beginTransaction().replace(R.id.container_main, mapFrg).commit()
                                    }
                                    R.id.rb_route_classics -> {
                                        AppConfig.CONSTANT = 1
                                        route = 1
                                        holder.convertView.find<RadioButton>(R.id.rb_route_all).setTextColor(resources.getColor(R.color.black))
                                        holder.convertView.find<RadioButton>(R.id.rb_route_classics).setTextColor(resources.getColor(R.color.white))
                                        val mapFrg: MapFragment = MapFragment.newInstance(floor, type, route)
                                        supportFragmentManager.beginTransaction().replace(R.id.container_main, mapFrg).commit()
                                    }
                                }
                            }
                            when (route) {
                                0 -> holder.convertView.find<RadioButton>(R.id.rb_route_all).isChecked = true
                                1 -> holder.convertView.find<RadioButton>(R.id.rb_route_classics).isChecked = true
                            }

                        }
                    })
                    .setDimAmount(0.3f)
                    .setShowBottom(false)
                    .setOutCancel(true)
                    .show(supportFragmentManager)


        }

        iv_common_device.setOnClickListener {
            AppConfig.CONSTANT = 1
            if (!flag) {
                AppConfig.ISHOME = false
                val mapFrg: MapFragment = MapFragment.newInstance(floor, 1, route)
                supportFragmentManager.beginTransaction().replace(R.id.container_main, mapFrg).commit()
                type = 1
            } else {
                AppConfig.ISHOME = false
                val mapFrg: MapFragment = MapFragment.newInstance(floor, 0, route)
                supportFragmentManager.beginTransaction().replace(R.id.container_main, mapFrg).commit()
                type = 0
            }
            flag = !flag
        }
        tv_slide_map_guide.setOnClickListener {
            AppConfig.CONSTANT = 1
            AppConfig.TYPECONFIG = 1

            supportFragmentManager.beginTransaction().replace(R.id.container_main, mapFrg).commit()
            draw_layout.closeDrawers()
            tv_search.visibility = View.VISIBLE
            tv_common_title.visibility = View.GONE
            ll_swipe.visibility = View.VISIBLE
            AppConfig.ISHOME = false
            showSocket(iv_socket_map)
        }
        tv_slide_list_guide.setOnClickListener {
            AppConfig.CONSTANT = 0
            supportFragmentManager.beginTransaction().replace(R.id.container_main, scrolltFrg).commit()
            tv_search.visibility = View.VISIBLE
            tv_common_title.visibility = View.GONE
            ll_swipe.visibility = View.GONE
            tv_search.visibility = View.GONE
            iv_search_common.visibility = View.VISIBLE
            draw_layout.closeDrawers()
            showSocket(iv_socket_list)
        }
        tv_slide_intro.setOnClickListener {
            AppConfig.CONSTANT = 0
            supportFragmentManager.beginTransaction().replace(R.id.container_main, inroFrg).commit()
            tv_search.visibility = View.GONE
            iv_search_common.visibility = View.GONE
            tv_common_title.visibility = View.VISIBLE
            ll_swipe.visibility = View.GONE
            tv_common_title.text = "科技馆简介"
            draw_layout.closeDrawers()
            showSocket(iv_socket_intro)
        }
        tv_slide_about.setOnClickListener {
            AppConfig.CONSTANT = 0
            supportFragmentManager.beginTransaction().replace(R.id.container_main, aboutFrg).commit()
            draw_layout.closeDrawers()
            tv_search.visibility = View.GONE
            tv_common_title.visibility = View.VISIBLE
            ll_swipe.visibility = View.GONE
            iv_search_common.visibility = View.GONE
            tv_common_title.text = "关于科技馆"
            showSocket(iv_socket_about)
        }
        tv_slide_scan_guide.setOnClickListener {
            AppConfig.CONSTANT = 0
            supportFragmentManager.beginTransaction().replace(R.id.container_main, scanFrg).commit()
            tv_search.visibility = View.GONE
            tv_common_title.visibility = View.VISIBLE
            ll_swipe.visibility = View.GONE
            tv_common_title.text = "二维码扫描"
            iv_search_common.visibility = View.GONE
            draw_layout.closeDrawers()
            showSocket(iv_socket_scan)
        }
        tv_slide_call.setOnClickListener {
            AppConfig.CONSTANT = 0
            supportFragmentManager.beginTransaction().replace(R.id.container_main, callFrg).commit()
            tv_search.visibility = View.GONE
            tv_common_title.visibility = View.VISIBLE
            ll_swipe.visibility = View.GONE
            tv_common_title.text = "联系我们"
            iv_search_common.visibility = View.GONE
            draw_layout.closeDrawers()
            showSocket(iv_socket_call)
        }

        tv_setting.setOnClickListener {
            AppConfig.CONSTANT = 0
            supportFragmentManager.beginTransaction().replace(R.id.container_main, settingFrg).commit()
            draw_layout.closeDrawers()
            tv_search.visibility = View.GONE
            iv_search_common.visibility = View.GONE
            tv_common_title.visibility = View.VISIBLE
            ll_swipe.visibility = View.GONE
            tv_common_title.text = "设置"
        }
        iv_search_common.setOnClickListener {
            startActivity<SearchActivity>()
        }
    }

    private fun setColor(resId: Int) {
        when (resId) {
            R.id.rb_floor_one -> {
                rb_floor_one.setTextColor(resources.getColor(R.color.white))
                rb_floor_two.setTextColor(resources.getColor(R.color.black))
                rb_floor_three.setTextColor(resources.getColor(R.color.black))
            }
            R.id.rb_floor_two -> {
                rb_floor_one.setTextColor(resources.getColor(R.color.black))
                rb_floor_two.setTextColor(resources.getColor(R.color.white))
                rb_floor_three.setTextColor(resources.getColor(R.color.black))
            }
            R.id.rb_floor_three -> {
                rb_floor_one.setTextColor(resources.getColor(R.color.black))
                rb_floor_two.setTextColor(resources.getColor(R.color.black))
                rb_floor_three.setTextColor(resources.getColor(R.color.white))
            }
        }

    }

    fun showSocket(img: ImageView) {
        when (img) {
            iv_socket_map -> {
                iv_socket_map.visibility = View.VISIBLE
                iv_socket_about.visibility = View.INVISIBLE
                iv_socket_intro.visibility = View.INVISIBLE
                iv_socket_list.visibility = View.INVISIBLE
                iv_socket_call.visibility = View.INVISIBLE
                iv_socket_scan.visibility = View.INVISIBLE

            }
            iv_socket_about -> {
                iv_socket_map.visibility = View.INVISIBLE
                iv_socket_about.visibility = View.VISIBLE
                iv_socket_intro.visibility = View.INVISIBLE
                iv_socket_list.visibility = View.INVISIBLE
                iv_socket_call.visibility = View.INVISIBLE
                iv_socket_scan.visibility = View.INVISIBLE
            }
            iv_socket_intro -> {
                iv_socket_map.visibility = View.INVISIBLE
                iv_socket_about.visibility = View.INVISIBLE
                iv_socket_intro.visibility = View.VISIBLE
                iv_socket_list.visibility = View.INVISIBLE
                iv_socket_call.visibility = View.INVISIBLE
                iv_socket_scan.visibility = View.INVISIBLE
            }
            iv_socket_list -> {
                iv_socket_map.visibility = View.INVISIBLE
                iv_socket_about.visibility = View.INVISIBLE
                iv_socket_intro.visibility = View.INVISIBLE
                iv_socket_list.visibility = View.VISIBLE
                iv_socket_call.visibility = View.INVISIBLE
                iv_socket_scan.visibility = View.INVISIBLE
            }
            iv_socket_scan -> {
                iv_socket_map.visibility = View.INVISIBLE
                iv_socket_about.visibility = View.INVISIBLE
                iv_socket_intro.visibility = View.INVISIBLE
                iv_socket_list.visibility = View.INVISIBLE
                iv_socket_call.visibility = View.INVISIBLE
                iv_socket_scan.visibility = View.VISIBLE
            }
            iv_socket_call -> {
                iv_socket_map.visibility = View.INVISIBLE
                iv_socket_about.visibility = View.INVISIBLE
                iv_socket_intro.visibility = View.INVISIBLE
                iv_socket_list.visibility = View.INVISIBLE
                iv_socket_call.visibility = View.VISIBLE
                iv_socket_scan.visibility = View.INVISIBLE
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (draw_layout.isDrawerOpen(Gravity.LEFT)) {
                draw_layout.closeDrawers()
            } else {
                exitBy2Click()
            }
            return false
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            AppConfig.ISHOME = true
            AppConfig.CONSTANT = 0
            return false
        } else {
            return super.onKeyDown(keyCode, event)
        }
    }


    override fun OnBeaconListChange(beacons: MutableList<Beacon>?) {

    }

    override fun OnNumChange(num: Int) {
        if (isReplay(num) && AppConfig.CONSTANT == 1 && !AppConfig.ISPLAY && AppConfig.AUTO == 1) {
            AppConfig.database.use {
                exhibitList = select("MUSEUM_EXHIBIT").whereSimple("AutoNum=?", num.toString()).parseList { Exhibit(it as MutableMap<String, Any?>) }
                if ((exhibitList as List<Exhibit>).size > 0) {
                    val exhibit: Exhibit = (exhibitList as List<Exhibit>).first()
                    if (exhibit != null) {
                        if (floor != exhibit.MapId && !dialog.isVisible) {
                            //切换楼层弹框
                            dialog.setLayoutId(R.layout.dialog_map)
                                    .setConvertListener(object : ViewConvertListener {
                                        override fun convertView(p0: ViewHolder?, p1: BaseNiceDialog?) {
                                            p0!!.convertView.find<TextView>(R.id.tv_swipFloor).text = "您当前地显示为${floor}楼，您所在地图为${exhibit.MapId}楼，是否需要帮您跳转到${exhibit.MapId}楼地图？"
                                            p0!!.convertView.find<ImageView>(R.id.iv_close_map_dialog).setOnClickListener { dialog.dismiss() }
                                            p0!!.convertView.find<Button>(R.id.btn_swipFloor).setOnClickListener {
                                                floor = exhibit.MapId
                                                val mapFrg: MapFragment = MapFragment.newInstance(floor, 0, route)
                                                iv_switch_floor.visibility = View.VISIBLE
                                                iv_common_device.visibility = View.VISIBLE
                                                supportFragmentManager.beginTransaction().replace(R.id.container_main, mapFrg).commitAllowingStateLoss()
                                                p1!!.dismiss()
                                            }
                                        }
                                    }).setDimAmount(0.3f)
                                    .setShowBottom(false)
                                    .setMargin(30)
                                    .setOutCancel(false)
                                    .show(supportFragmentManager)
                        }
                    }
                }
            }
        }
    }

    private fun exitBy2Click() {
        if (System.currentTimeMillis() - lastTime > 2000) {
            toast("再次点击退出应用")
            lastTime = System.currentTimeMillis()
        } else {
            AppConfig.CONSTANT = 0
            finish()
        }
    }

    fun <T : Any> SelectQueryBuilder.parseList(parser: (Map<String, Any?>) -> T): List<T> =
            parseList(object : MapRowParser<T> {
                override fun parseRow(columns: Map<String, Any?>): T = parser(columns)
            })


    fun isReplay(num: Int): Boolean {
        var temp_flag = false
        if (num != 0 && num != lastNum) {
            lastNum = num
            temp_flag = true
        }
        return temp_flag
    }

    override fun onResume() {
        super.onResume()
        if (AppConfig.TYPECONFIG == 1 && AppConfig.CONSTANT == 1) {
            val mapFrg: MapFragment = MapFragment.newInstance(floor, type, route)
            supportFragmentManager.beginTransaction().replace(R.id.container_main, mapFrg).commitAllowingStateLoss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        HDNumService.stopService(this);
        NumManager.unregisterNumChangeListener(this);
    }


}


