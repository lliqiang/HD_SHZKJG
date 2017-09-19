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
import org.jetbrains.anko.find


/**
 * A simple [Fragment] subclass.
 */
class AboutFragment : Fragment() {
    companion object {
        fun newInstance(): AboutFragment {
            return AboutFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater!!.inflate(R.layout.fragment_about, container, false)
        var webAbout = view.find<WebView>(R.id.web_about)
        webAbout.setBackgroundResource(0)
        webAbout.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
            }
        })
        val path = AppConfig.getWebPath("about")
        Log.i("about", "about:         ${path}")
        webAbout.loadUrl("file:///" + path)
        return view
    }

}// Required empty public constructor
