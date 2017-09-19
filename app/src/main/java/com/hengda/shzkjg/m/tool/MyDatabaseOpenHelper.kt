package com.hengda.shzkjg.m.tool

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.openDatabase
import com.hengda.shzkjg.m.base.AppConfig
import org.jetbrains.anko.db.ManagedSQLiteOpenHelper
import org.jetbrains.anko.db.dropTable

/**
 * Created by lenovo on 2017/8/7.
 */
class MyDatabaseOpenHelper(mContext:Context):ManagedSQLiteOpenHelper(mContext,AppConfig.getDefaultFileDir()+"filemanage.s3db",null,1) {
    companion object {
         var instance: MyDatabaseOpenHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): MyDatabaseOpenHelper {
            if (instance == null) {
                instance = MyDatabaseOpenHelper(ctx.getApplicationContext())
            }
            return instance!!
        }
    }
    override fun onCreate(db: SQLiteDatabase?) {

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

}