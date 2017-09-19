package com.hengda.shzkjg.m.tool

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * 作者：Tailyou （祝文飞）
 * 时间：2016/8/19 10:26
 * 邮箱：tailyou@163.com
 * 描述：
 */
class TabAdapter : FragmentPagerAdapter {

     var fragments: List<Fragment>? = null
     var tabTitles: List<String>? = null


    constructor(fm: FragmentManager, fragments: List<Fragment>, tabTitles: List<String>?) : super(fm) {
        this.fragments = fragments
        this.tabTitles = tabTitles
    }

//    override fun getPageTitle(position: Int): CharSequence {
//        return tabTitles!![position % tabTitles!!.size]
//    }

    override fun getCount(): Int {
        return fragments!!.size
    }

    override fun getItem(position: Int): Fragment {
        return fragments!![position]
    }

}
