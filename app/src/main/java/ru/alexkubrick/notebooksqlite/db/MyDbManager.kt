package ru.alexkubrick.notebooksqlite.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
// context -- обеспечивает доступ к ресурсам и классам, специфичным для конкретного приложения,
// а также к вызовам для операций на уровне приложения
class MyDbManager(context: Context) {
    val myDbHelper = MyDbHelper(context)
    var db: SQLiteDatabase? = null

    fun openDb() { //открываем БД
        db = myDbHelper.writableDatabase
    }

    // записываем внутрь БД
   suspend fun insertToDb(title: String, content: String, uri: String, time: String) = withContext(Dispatchers.IO){
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_NAME_TITLE, title)
            put(MyDbNameClass.COLUMN_NAME_CONTENT, content)
            put(MyDbNameClass.COLUMN_NAME_IMAGE_URI, uri)
            put(MyDbNameClass.COLUMN_NAME_TIME, time) // добавили время.
        // перед этим обновили файл MyDbNameClass. там указали новую колонку и обновили версию БД
        }
        db?.insert(MyDbNameClass.TABLE_NAME, null, values)

    }
    // cursor -  интерфейс обеспечивает произвольный доступ для чтения и записи
    // к результирующему множеству, возвращаемому запросом к базе данных.
    //Реализации курсора не обязательно синхронизировать, поэтому код,
    // использующий курсор из нескольких потоков,
    // должен выполнять свою собственную синхронизацию при использовании курсора.

    @SuppressLint("Range")
    // передаем текст, кот. будем искать
    suspend fun readDbData(searchText: String): ArrayList<ListItem> = withContext(Dispatchers.IO){ // второстепенный поток
        // + блокирует корутину на основном потоке
        val dataList = ArrayList<ListItem>()
        val selection = "${MyDbNameClass.COLUMN_NAME_TITLE} like ?" // запрос -- ищем в колонке с заголовками
        // если то, что написали, совпадет с БД -- покажет часть, где есть совпадение
        // like ? -- запрос к SQLite
        val cursor = db?.query(MyDbNameClass.TABLE_NAME, null,
            selection, arrayOf("%$searchText%"), // поиск по одной букве
            null, null, null ) // selection -- колонка, в кот. будем искать; selectionArg -- текст, кот. ищем

        with(cursor) { // считываем элемент из БД
            while(this?.moveToNext()!!) {

                val dataTitle = cursor?.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_TITLE))
                val dataContent = cursor?.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_CONTENT))
                val dataUri = cursor?.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_IMAGE_URI))
                val dataId = cursor?.getInt(cursor.getColumnIndex(BaseColumns._ID))
                val time = cursor?.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_TIME)) // добавили время.
                // перед этим обновили ListItem -- добавили переменную time = ""

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

                if (time != null) {
                    item.time = time
                }

                dataList.add(item)
            }
            cursor?.close()

        }
        return@withContext dataList
    }
    // искать по id
    suspend fun updateItem(title: String, content: String, uri: String, id: Int, time: String) = withContext(Dispatchers.IO) {
        val selection = BaseColumns._ID + "=$id"
        val values = ContentValues().apply { // что обновить
            put(MyDbNameClass.COLUMN_NAME_TITLE, title)
            put(MyDbNameClass.COLUMN_NAME_CONTENT, content)
            put(MyDbNameClass.COLUMN_NAME_IMAGE_URI, uri)
            put(MyDbNameClass.COLUMN_NAME_TIME, time)
        }
        db?.update(MyDbNameClass.TABLE_NAME, values, selection, null) // как в удаление -- selection,
    // какой именно элемент обновить

    }

    fun removeItemFromDb(id: String) { // delete according to id
        val selection = BaseColumns._ID + "=$id"
        // делаем свайп, берем идентиф (id: String) по кот. сделали свайп
        // сравнить наш идентификатор с BaseColumns._ID. найти в БД такой же ID
        // удалить элемент по его идентификатору (BaseColumns._ID)
        db?.delete(MyDbNameClass.TABLE_NAME, selection, null)

    }

    fun closeDb() {
        myDbHelper.close()
    }

}