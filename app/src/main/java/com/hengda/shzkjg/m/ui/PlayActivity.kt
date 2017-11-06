package com.hengda.shzkjg.m.ui

import android.content.*
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.SeekBar
import com.hengda.frame.hdplayer.BasePlayerActivity
import com.hengda.frame.hdplayer.HDExoPlayer
import com.hengda.frame.hdplayer.MusicService
import com.hengda.frame.hdplayer.model.MusicTrack
import com.hengda.frame.hdplayer.util.ControllerUtil
import com.hengda.shzkjg.m.R
import com.hengda.shzkjg.m.base.AppConfig
import com.hengda.shzkjg.m.base.BaseActivity
import com.hengda.shzkjg.m.bean.Exhibit
import com.hengda.shzkjg.m.tool.StatusBarCompat
import kotlinx.android.synthetic.main.activity_play.*
import kotlinx.android.synthetic.main.head_common.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.update
import org.jetbrains.anko.info
import kotlin.properties.Delegates
import android.provider.SyncStateContract.Helpers.update
import android.provider.SyncStateContract.Helpers.update
import android.view.KeyEvent
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hengda.shzkjg.m.base.App
import com.jaeger.library.StatusBarUtil
import org.jetbrains.anko.toast


class PlayActivity : BasePlayerActivity(), AnkoLogger {
    var exhibit: Exhibit by Delegates.notNull()
    var dragging: Boolean = false
    var flag = false
    var path: String by Delegates.notNull()
    var voicePath: String by Delegates.notNull()
    var mPlaybackStatus: PlaybackStatus? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarCompat.setStatusBarColor(this, resources.getColor(R.color.blue1))
        setContentView(R.layout.activity_play)
        AppConfig.ISPLAY = true
        exhibit = intent.getSerializableExtra("exhibit") as Exhibit
        flag = intent.getBooleanExtra("flag", false)
        AppConfig.database.use {
            val values = ContentValues()
            values.put("IsRead", 1)
            if (flag) {
                values.put("temp", AppConfig.Count)
            }
            AppConfig.Count++
            val whereClause = "FileNo=?"//where子句，表示要操作的位置
            val whereArgs = arrayOf(exhibit.FileNo)//
            update("MUSEUM_EXHIBIT", values, whereClause, whereArgs)
        }
        Glide.with(this).load(AppConfig.getImgPath(exhibit.FileNo)).placeholder(R.mipmap.img_list_def).diskCacheStrategy(DiskCacheStrategy.NONE).into(iv_play_detail)
        iv_back.setOnClickListener {
            AppConfig.ISPLAY = false
            finish()
        }
        tv_title_common.setText(exhibit.Name)
        initWeb()
        initControl()
        initPlaybackStatus()
        voicePath = AppConfig.getVoicePath(exhibit.FileNo, ".mp3")

        if (HDExoPlayer.isPlaying()) {
            iv_play.setImageResource(R.mipmap.img_pause)
        } else {
            if (HDExoPlayer.getPosition().toInt() == 0) {
                Handler().postDelayed(Runnable {
                    //MusicTrack 最后一项参数为通知栏的图标，如果设置成-1 则不显示通知栏播放状态
                    HDExoPlayer.prepare(MusicTrack(exhibit.ExhibitId, exhibit.Name, voicePath, R.drawable.def_placeholder), true)
                    iv_play.setImageResource(R.mipmap.img_pause)
                }, 200)
            } else {
                if (HDExoPlayer.getPosition() >= HDExoPlayer.getDuration()) {
                    HDExoPlayer.prepare(MusicTrack(exhibit.ExhibitId, exhibit.Name, voicePath, R.drawable.def_placeholder), true)
                    iv_play.setImageResource(R.mipmap.img_pause)
                } else {
                    seekBar.setProgress(ControllerUtil.progressBarValue(HDExoPlayer.getDuration(), HDExoPlayer.getPosition()))
                    iv_play.setImageResource(R.mipmap.img_pause)

                }
            }
        }
    }

    private fun initWeb() {
        web_play.setBackgroundResource(0)
        web_play.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
            }
        })
        path = AppConfig.getVoicePath(exhibit.FileNo, ".html")
        web_play.loadUrl("file:///" + path)
    }

    inner class PlaybackStatus : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == MusicService.POSITION_CHANGED) {
                val duration = intent.extras.getLong("duration")
                val position = intent.extras.getLong("position")
                val bufferedPosition = intent.extras.getLong("bufferedPosition")
                if (!dragging) {
                    seekBar.setProgress(ControllerUtil.progressBarValue(duration, position))
                    seekBar.setSecondaryProgress(ControllerUtil.progressBarValue(duration, bufferedPosition))
                    tv_endTime.setText(ControllerUtil.stringForTime(duration - position))
                }
            } else if (action == MusicService.STATE_BUFFERING) {

            } else if (action == MusicService.STATE_ERROR) {

            } else if (action == MusicService.STATE_COMPLETED) {
                iv_play.setImageResource(R.mipmap.img_play)
                tv_endTime.setText(ControllerUtil.stringForTime(0))
                HDExoPlayer.prepare(MusicTrack(exhibit.ExhibitId, exhibit.Name, voicePath, R.drawable.def_placeholder), false)
            } else if (action == MusicService.TOGGLEPAUSE_ACTION) {
                if (!HDExoPlayer.isPlaying()) {
                    iv_play.setImageResource(R.mipmap.img_play)
                } else {
                    iv_play.setImageResource(R.mipmap.img_pause)
                }
            }
        }
    }

    private fun initPlaybackStatus() {
        val filter = IntentFilter()
        filter.addAction(MusicService.POSITION_CHANGED)
        filter.addAction(MusicService.STATE_BUFFERING)
        filter.addAction(MusicService.STATE_ERROR)
        filter.addAction(MusicService.STATE_COMPLETED)
        filter.addAction(MusicService.TOGGLEPAUSE_ACTION)
        mPlaybackStatus = PlaybackStatus()
        registerReceiver(mPlaybackStatus, filter)
    }

    private fun initControl() {
//        seekBar.setMax(ControllerUtil.PROGRESS_BAR_MAX)
        seekBar.setMax(920)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                dragging = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                dragging = false
                HDExoPlayer.seekTo(seekBar.progress * HDExoPlayer.getDuration() / ControllerUtil.PROGRESS_BAR_MAX)
//                HDExoPlayer.seekTo(seekBar.progress * HDExoPlayer.getDuration() / 1000)
            }
        })
        iv_play.setOnClickListener(View.OnClickListener {
            if (!HDExoPlayer.isPlaying()) {
                iv_play.setImageResource(R.mipmap.img_pause)
                HDExoPlayer.play()
            } else {
                iv_play.setImageResource(R.mipmap.img_play)
                HDExoPlayer.pause()
            }
        })
    }

    override fun onStop() {
        super.onStop()
        HDExoPlayer.pause()
        iv_play.setImageResource(R.mipmap.img_play)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AppConfig.ISPLAY = false
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mPlaybackStatus)
        mPlaybackStatus = null
        if (web_play != null) {
            web_play.removeAllViews();
            web_play.destroy();
        }

    }
}
