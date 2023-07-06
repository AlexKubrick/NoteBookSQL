package ru.alexkubrick.notebooksqlite

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import ru.alexkubrick.notebooksqlite.databinding.ActivityMainBinding
import ru.alexkubrick.notebooksqlite.db.MyDbManager

class MainActivity : AppCompatActivity() {
    lateinit var bindingClass: ActivityMainBinding

    val myDbManager = MyDbManager(this)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)


    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDb()
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDb()
    }

    fun onClickNew(view: View) {
        val i = Intent(this, EditActivity::class.java)
        startActivity(i)

    }


}