package com.example.mp3playerproject

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DBOpenHelper(context: Context, dbName:String, version: Int): SQLiteOpenHelper(context, dbName, null, version) {
    override fun onCreate(db: SQLiteDatabase?) {
        val musicTbl = "create table musicTBL (id TEXT primary key, title TEXT, artist TEXT, albumId TEXT, duration INTEGER, likes INTEGER)"
        db?.execSQL(musicTbl)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTable = "drop table musicTBL"
        db?.execSQL(dropTable)
        this.onCreate(db)
    }

    fun insertMusicTBL(musicData: MusicData){
        val insertMusic = "insert into musicTBL values ('${musicData.id}','${musicData.title}','${musicData.artist}'," +
                "'${musicData.albumId}',${musicData.duration}, ${musicData.likes})"
        val db = this.writableDatabase
        db.execSQL(insertMusic)
    }

    fun selectAllMusicTBL():MutableList<MusicData>?{
        var musicDataList:MutableList<MusicData>? = null
        val selectAll = "select * from musicTBL"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectAll, null)
        if(cursor.count <= 0) return null
        musicDataList = mutableListOf<MusicData>()

        while(cursor.moveToNext()){
            var musicData = MusicData(cursor.getString(0),cursor.getString(1),
                cursor.getString(2),cursor.getString(3),cursor.getInt(4),cursor.getInt(5))
            musicDataList.add(musicData)
        }
        return musicDataList
    }

    fun updateLike(musicData: MusicData):Boolean {
        var errorFlag = false
        val updateMusic = """update musicTBL set likes = ${musicData.likes} where id = '${musicData.id}'""".trimIndent()
        val db = this.writableDatabase
        try {
            db.execSQL(updateMusic)
        }catch (e:Exception){
            Log.e("DBOpenHelper", "updateLike error ${e.printStackTrace()}")
            errorFlag = true
        }
        return errorFlag
    }

    fun selectMusicLike(): MutableList<MusicData>? {
        var musicList: MutableList<MusicData>? = mutableListOf<MusicData>()
        var cursor: Cursor? = null
        val query = """select * from musicTBL where likes = 1 """.trimIndent()
        val db = this.readableDatabase

        try {
            cursor = db.rawQuery(query, null)
            if(cursor.count > 0){
                while(cursor.moveToNext()){
                    val id = cursor.getString(0)
                    val title = cursor.getString(1)
                    val artist = cursor.getString(2)
                    val albumId = cursor.getString(3)
                    val duration = cursor.getInt(4)
                    val likes = cursor.getInt(5)
                    val music = MusicData(id, title, artist, albumId, duration, likes)
                    musicList?.add(music)
                }
            }else{
                musicList = null
            }
        }catch (e: Exception){
            Log.d("chap17mp3_db", "DBHelper.selectMusicLike() 예외발생 ${e.printStackTrace()}")
            musicList = null
        }finally {
            cursor?.close()
        }
        return musicList
    }
}