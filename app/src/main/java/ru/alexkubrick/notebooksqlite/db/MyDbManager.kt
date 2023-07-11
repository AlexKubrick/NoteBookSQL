package ru.alexkubrick.notebooksqlite.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns

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
    fun readDbData(searchText: String): ArrayList<ListItem> { // передаем текст, кот. будем искать
        val dataList = ArrayList<ListItem>()
        val selection = "${MyDbNameClass.COLUMN_NAME_TITLE} like ?" // запрос -- ищем в колонке с заголовками
        // если то, что написали, совпадет с БД -- покажет часть, где есть совпадение
        // like ? -- запрос к SQLite
        val cursor = db?.query(MyDbNameClass.TABLE_NAME, null,
            selection, arrayOf("%$searchText%"), // поиск по одной букве
            null, null, null ) // selection -- колонка, в кот. будем искать; selectionArg -- текст, кот. ищем

        with(cursor) { //считываем элемент из БД
            while(this?.moveToNext()!!) {

                val dataTitle = cursor?.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_TITLE))
                val dataContent = cursor?.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_CONTENT))
                val dataUri = cursor?.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_IMAGE_URI))
                val dataId = cursor?.getInt(cursor.getColumnIndex(BaseColumns._ID))

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

                if (dataId != null) {
                    item.id = dataId
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

    fun removeItemFromDb(id: String) { // delete according to id
        val selection = BaseColumns._ID + "=$id"
        // делаем свайп, берем идентиф (id: String) по кот. сделали свайп
        // сравнить наш идентификатор с BaseColumns._ID. найти в БД такой же ID
        // удалить элемент по его идентификатору (BaseColumns._ID)
        db?.delete(MyDbNameClass.TABLE_NAME, selection, null)

    }
}