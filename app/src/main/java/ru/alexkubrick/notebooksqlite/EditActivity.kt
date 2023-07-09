package ru.alexkubrick.notebooksqlite

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import ru.alexkubrick.notebooksqlite.databinding.EditActivityBinding
import ru.alexkubrick.notebooksqlite.db.MyDbManager
import ru.alexkubrick.notebooksqlite.db.MyIntentConstance

class EditActivity : AppCompatActivity() {
    val imageRequestCode = 10
    var tempImageUri = "empty"
    lateinit var bindingClass: EditActivityBinding
    val myDbManager = MyDbManager(this)

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


        }
        if (data != null) { // важная часть, которая позволяет открывать изображения (нужны разрешения)
            this.contentResolver.takePersistableUriPermission(
                data.data!!,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
    }

    fun onClickAddImage(view: View) {

        bindingClass.mainImageLayout.visibility = View.VISIBLE
        bindingClass.fbAddImage.visibility = View.GONE

    }

    fun onClickDeleteImage(view: View) {

        bindingClass.mainImageLayout.visibility = View.GONE
        bindingClass.fbAddImage.visibility = View.VISIBLE

    }

    fun onClickChooseImage(view: View) {

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT) //временная сссылка
        intent.type = "image/*"
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION //постоянная ссылка от системы
        startActivityForResult(intent,imageRequestCode)
    }

    fun onClickSave(view: View) {

        val myTitle = bindingClass.edTitle.text.toString()
        val myDesc = bindingClass.edDesc.text.toString()
        if (myTitle != "" && myDesc != "") {
            myDbManager.insertToDb(myTitle, myDesc, tempImageUri)
            finish()

        }
    }

    fun getMyIntents() {

        val i = intent

        if (i != null) {

            if (i.getStringExtra(MyIntentConstance.I_TITLE_KEY) != null) {

                bindingClass.fbAddImage.visibility = View.GONE

                bindingClass.edTitle.setText(i.getStringExtra(MyIntentConstance.I_TITLE_KEY))
                bindingClass.edDesc.setText(i.getStringExtra(MyIntentConstance.I_DESC_KEY))

                if (i.getStringExtra(MyIntentConstance.I_URI_KEY) != "empty") {

                    bindingClass.mainImageLayout.visibility = View.VISIBLE
                    bindingClass.imMainImage.setImageURI(Uri.parse(i.getStringExtra(MyIntentConstance.I_URI_KEY)))
                    bindingClass.imDeleteButtonImage.visibility = View.GONE
                    bindingClass.imDeleteButtonImage.visibility = View.GONE

                }
            }
        }
    }
}