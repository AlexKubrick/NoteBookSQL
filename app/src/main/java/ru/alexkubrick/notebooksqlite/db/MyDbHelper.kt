package ru.alexkubrick.notebooksqlite.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
// SQLiteOpenHelper - вспомогательный объект для создания, открытия базы данных и/или управления ею.
// Этот метод всегда возвращает результат очень быстро.
// База данных фактически не создается и не открывается до тех пор,
// пока не будет вызван один из getWritableDatabase или getReadableDatabase.
class MyDbHelper(context: Context) : SQLiteOpenHelper(context, MyDbNameClass.DATABASE_NAME,
    null, MyDbNameClass.DATABASE_VERSION) {
    // здесь создается
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(MyDbNameClass.CREATE_TABLE)
    }
    // execSQL - Execute a single SQL statement that is NOT a SELECT or any other SQL statement that returns data.

    // здесь обновляется
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(MyDbNameClass.SQL_DELETE_TABLE)
        onCreate(db)

    }
}