package com.hengda.shzkjg.m.fragment


import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.hengda.shzkjg.m.R
import com.hengda.shzkjg.m.tool.TabAdapter
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.find
import org.jetbrains.anko.info
/**
 * A simple [Fragment] subclass.
 */
class ListFragment : Fragment(), AnkoLogger {

    var fragmentList = mutableListOf<Fragment>()
    lateinit var tab_list: TabLayout

    companion object {
        fun newInstance(): ListFragment {
            return ListFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        initFragment()
        val tabAdapter = TabAdapter(childFragmentManager, fragmentList, null)
        val view: View = inflater!!.inflate(R.layout.fragment_list, container, false)
        val viewPager = view.find<ViewPager>(R.id.viewPager_list)
        tab_list = view.find<TabLayout>(R.id.tab_list)
        val (tabView: View, tabView1: View, tabView2: View) = initTab()
        viewPager.adapter = tabAdapter
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageSelected(position: Int) {
                info { "onPageSelected: -----------${position}" }
                initTabSelect(position)
            }

            private fun initTabSelect(position: Int) {
                when (position) {
                    0 -> {
                        tabView.find<ImageView>(R.id.iv_tab).visibility = View.VISIBLE
                        tabView1.find<ImageView>(R.id.iv_tab).visibility = View.GONE
                        tabView2.find<ImageView>(R.id.iv_tab).visibility = View.GONE
                    }
                    1 -> {
                        tabView.find<ImageView>(R.id.iv_tab).visibility = View.GONE
                        tabView1.find<ImageView>(R.id.iv_tab).visibility = View.VISIBLE
                        tabView2.find<ImageView>(R.id.iv_tab).visibility = View.GONE
                    }
                    2 -> {
                        tabView.find<ImageView>(R.id.iv_tab).visibility = View.GONE
                        tabView1.find<ImageView>(R.id.iv_tab).visibility = View.GONE
                        tabView2.find<ImageView>(R.id.iv_tab).visibility = View.VISIBLE
                    }
                }
            }
        })
        tabAdapter.notifyDataSetChanged()
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tab_list))
        tab_list.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewPager))
        return view
    }

    private fun initTab(): Triple<View, View, View> {
        val tabView: View = View.inflate(activity, R.layout.tab_layout, null)
        var tv_tab = tabView.find<TextView>(R.id.tv_tab)
        tv_tab.text = "1F"
        tab_list.addTab(tab_list.newTab().setCustomView(tabView))
        tabView.find<ImageView>(R.id.iv_tab).visibility = View.VISIBLE

        val tabView1: View = View.inflate(activity, R.layout.tab_layout, null)
        var tv_tab1 = tabView1.find<TextView>(R.id.tv_tab)
        tv_tab1.text = "2F"
        tab_list.addTab(tab_list.newTab().setCustomView(tabView1))

        val tabView2: View = View.inflate(activity, R.layout.tab_layout, null)
        var tv_tab2 = tabView2.find<TextView>(R.id.tv_tab)
        tv_tab2.text = "3F"
        tab_list.addTab(tab_list.newTab().setCustomView(tabView2))
        return Triple(tabView, tabView1, tabView2)
    }

    private fun initFragment() {
        fragmentList.clear()
        val commonFragment1: CommonListFragment = CommonListFragment.newInstance(1)
        val commonFragment2: CommonListFragment = CommonListFragment.newInstance(2)
        val commonFragment3: CommonListFragment = CommonListFragment.newInstance(3)
        fragmentList.add(commonFragment1)
        fragmentList.add(commonFragment2)
        fragmentList.add(commonFragment3)
    }

}
