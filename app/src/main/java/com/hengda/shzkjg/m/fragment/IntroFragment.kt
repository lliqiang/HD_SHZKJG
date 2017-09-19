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
import kotlinx.android.synthetic.main.activity_play.*
import kotlinx.android.synthetic.main.fragment_intro.*
import org.jetbrains.anko.find

/**
 * A simple [Fragment] subclass.
 */

class IntroFragment : Fragment() {

    companion object {
        fun newInstance(): IntroFragment {
            return IntroFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater!!.inflate(R.layout.fragment_intro, container, false)
        var webInfo = view.find<WebView>(R.id.web_intro)
        webInfo.setBackgroundResource(0)
        webInfo.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
            }
        })
        val path = AppConfig.getWebPath("introduce")
        Log.i("introduce", "introduce:         ${path}")
        webInfo.loadUrl("file:///" + path)

        return view
    }

}// Required empty public constructor
