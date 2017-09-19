package com.hengda.shzkjg.m.adapter

/**
 * Created by lenovo on 2017/8/9.
 */


import android.content.Context
import android.support.annotation.LayoutRes
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hengda.shzkjg.m.R
import com.hengda.shzkjg.m.base.AppConfig
import com.hengda.shzkjg.m.bean.Exhibit
import com.hengda.shzkjg.m.bean.Exhibition

/**
 * 创建人：lenovo
 * 创建时间：2017/8/9 11:49
 * 类描述：
 */
class ExhibitAdapter(layoutResId: Int, data: MutableList<Exhibit>?) : BaseQuickAdapter<Exhibit, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder?, item: Exhibit?) {
        helper!!.setText(R.id.tv_title_list, item!!.Name)
        Glide.with(mContext).load(AppConfig.getImgPath(item.FileNo)).placeholder(R.mipmap.img_list_defaut).into(helper!!.getView<ImageView>(R.id.iv_list))
    }

}

