package ru.alexkubrick.notebooksqlite

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.alexkubrick.notebooksqlite.databinding.EditActivityBinding
import ru.alexkubrick.notebooksqlite.db.MyDbManager
import ru.alexkubrick.notebooksqlite.db.MyIntentConstants
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditActivity : AppCompatActivity() {
    val imageRequestCode = 10
    var tempImageUri = "empty"
    lateinit var bindingClass: EditActivityBinding
    val myDbManager = MyDbManager(this)
    var id = 0
    var isEditState = false // для проверки -- зашли в EditActivity для создания новой замиетки или для редактирования старой

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        bindingClass = EditActivityBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)
        getMyIntents()


    }

    override fun onDestroy() {

        super.onDestroy()
        myDbManager.closeDb()
    }

    override fun onResume() {

        super.onResume()
        myDbManager.openDb()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == imageRequestCode) {
            bindingClass.imMainImage.setImageURI(data?.data)
            tempImageUri = data?.data.toString()
            contentResolver.takePersistableUriPermission(data?.data!!, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            // CR -- класс, кот. дает ссылку + указать флаг
            // нужно, чтобы ссылка на картинку оставалась после перезагрузки телефона
            // 10.07.2023 -- пока что нет доступа к картинкам из камеры
        }
    }

    fun onClickAddImage(view: View) {

        bindingClass.mainImageLayout.visibility = View.VISIBLE
        bindingClass.fbAddImage.visibility = View.GONE

    }

    fun onClickDeleteImage(view: View) {

        bindingClass.mainImageLayout.visibility = View.GONE
        bindingClass.fbAddImage.visibility = View.VISIBLE
        tempImageUri = "empty"

    }

    fun onClickChooseImage(view: View) {

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT) // постоянная ссылка
        intent.type = "image/*" // все картинки
        startActivityForResult(intent,imageRequestCode)
    }

    fun onClickSave(view: View) { // по полученному id -- обновляем

        val myTitle = bindingClass.edTitle.text.toString() // из editable в текст
        val myDesc = bindingClass.edDesc.text.toString()
        if (myTitle != "" && myDesc != "") {

            CoroutineScope(Dispatchers.Main).launch {
                if (isEditState) {
                    myDbManager.updateItem(myTitle, myDesc, tempImageUri, id, getCurrentTime())
                } else {
                    myDbManager.insertToDb(myTitle, myDesc, tempImageUri, getCurrentTime()) // добавляем в БД
                }
                finish()
            }


        }


    }

    fun onEditEnable(view: View) { // редактирование заметки
        bindingClass.edTitle.isEnabled = true
        bindingClass.edDesc.isEnabled = true
        bindingClass.fbEdit.visibility = View.GONE
        bindingClass.fbAddImage.visibility = View.VISIBLE // редактирование картинки
        if (tempImageUri == "empty") return // если изначально пусто, то не запускаем imButtonEditImage и imDeleteButtonImage

        bindingClass.imButtonEditImage.visibility = View.VISIBLE
        bindingClass.imDeleteButtonImage.visibility = View.VISIBLE


    }
// https://developer.alexanderklimov.ru/android/theory/intent.php

    fun getMyIntents() { // получаем данные
        bindingClass.fbEdit.visibility = View.GONE

        val i = intent

        if (i != null) {

            if (i.getStringExtra(MyIntentConstants.I_TITLE_KEY) != null) {

                bindingClass.fbAddImage.visibility = View.GONE

                bindingClass.edTitle.setText(i.getStringExtra(MyIntentConstants.I_TITLE_KEY))
                isEditState = true // только когда получили интенты. при создании новой заметки -- ничего не передается
                    // нужна, для того чтобы узнать, что делаем в функции onClickSave -- сохраняем или перезаписываем
                bindingClass.edTitle.isEnabled = false // заблокировать элемент
                bindingClass.edDesc.isEnabled = false // заблокировать элемент
                bindingClass.fbEdit.visibility = View.VISIBLE
                bindingClass.edDesc.setText(i.getStringExtra(MyIntentConstants.I_DESC_KEY))

                id = i.getIntExtra(MyIntentConstants.I_ID_KEY, 0)


                if (i.getStringExtra(MyIntentConstants.I_URI_KEY) != "empty") {

                    bindingClass.mainImageLayout.visibility = View.VISIBLE
                    tempImageUri = i.getStringExtra(MyIntentConstants.I_URI_KEY)!!
                    bindingClass.imMainImage.setImageURI(Uri.parse(tempImageUri))
                    bindingClass.imDeleteButtonImage.visibility = View.GONE
                    bindingClass.imButtonEditImage.visibility = View.GONE

                }
            }
        }
    }

    private fun getCurrentTime(): String { // берем реальное время из устройства
        val time = Calendar.getInstance().time // берем время
        val format = SimpleDateFormat("dd-MM-yy kk:mm:ss", Locale.getDefault()) // задаем формат
        val fTime = format.format(time) // приводит в нужный формат
        return fTime
    }


}