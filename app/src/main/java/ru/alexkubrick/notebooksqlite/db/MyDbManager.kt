package ru.alexkubrick.notebooksqlite.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase

class MyDbManager(context: Context) {
    val myDbHelper = MyDbHelper(context)
    var db: SQLiteDatabase? = null

    fun openDb() { //открываем БД
        db = myDbHelper.writableDatabase
    }

    //записываем внутрь БД
    fun insertToDb(title: String, content: String, uri: String) {
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_NAME_TITLE, title)
            put(MyDbNameClass.COLUMN_NAME_CONTENT, content)
            put(MyDbNameClass.COLUMN_NAME_IMAGE_URI, uri)
        }
        db?.insert(MyDbNameClass.TABLE_NAME, null, values)

    }

    @SuppressLint("Range")
    fun readDbData(): ArrayList<ListItem> {
        val dataList = ArrayList<ListItem>()
        val cursor = db?.query(MyDbNameClass.TABLE_NAME, null,
            null, null,
            null, null, null )

        with(cursor) {
            while(this?.moveToNext()!!) {
                val dataTitle = cursor?.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_TITLE))
                val dataContent = cursor?.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_CONTENT))
                val dataUri = cursor?.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_IMAGE_URI))
                var item = ListItem()
                if (dataTitle != null) {
                    item.title = dataTitle
                }
                if (dataContent != null) {
                    item.desc = dataContent
                }
                if (dataUri != null) {
                    item.uri = dataUri
                }
                dataList.add(item)
            }
            cursor?.close()

        }
        return dataList
    }

    fun closeDb() {
        myDbHelper.close()
    }
}