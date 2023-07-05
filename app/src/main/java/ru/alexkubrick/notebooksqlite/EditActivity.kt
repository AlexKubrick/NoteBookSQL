package ru.alexkubrick.notebooksqlite

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout

class EditActivity : AppCompatActivity() {
    lateinit var mainImageLayout: ConstraintLayout
    lateinit var fbAddImage: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_activity)
        mainImageLayout = findViewById(R.id.mainImageLayout)
        fbAddImage = findViewById(R.id.fbAddImage)

    }

    fun onClickAddImage(view: View) {
        mainImageLayout.visibility = View.VISIBLE
        fbAddImage.visibility = View.GONE

    }

    fun onClickDeleteImage(view: View) {
        mainImageLayout.visibility = View.GONE
        fbAddImage.visibility = View.VISIBLE

    }
}