package com.hengda.shzkjg.m.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient

import com.hengda.shzkjg.m.R
import com.hengda.shzkjg.m.base.AppConfig
import kotlinx.android.synthetic.main.fragment_call.*
import kotlinx.android.synthetic.main.head_common.*


/**
 * A simple [Fragment] subclass.
 */
class CallFragment : Fragment() {
    companion object {
        fun newInstance(): CallFragment {
            return CallFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_call, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        wv_call.setBackgroundResource(0)
        wv_call.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
            }
        })
        val path = AppConfig.getWebPath("introduce")
        wv_call.loadUrl("file:///" + path)
    }

}// Required empty public constructor
