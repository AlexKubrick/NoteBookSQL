package ru.alexkubrick.notebooksqlite.db

import android.provider.BaseColumns

object MyDbNameClass : BaseColumns { // 0 title content
    const val TABLE_NAME = "my_table"
    const val COLUMN_NAME_TITLE = "title"
    const val COLUMN_NAME_CONTENT = "content"
    const val COLUMN_NAME_IMAGE_URI = "uri"

    const val DATABASE_VERSION = 3
    const val DATABASE_NAME = "MyLessonDb.db"

    const val CREATE_TABLE = "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY, $COLUMN_NAME_TITLE TEXT, $COLUMN_NAME_CONTENT TEXT, $COLUMN_NAME_IMAGE_URI TEXT)"
    const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS ${TABLE_NAME}"

}