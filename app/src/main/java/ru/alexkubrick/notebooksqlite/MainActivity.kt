package ru.alexkubrick.notebooksqlite

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import ru.alexkubrick.notebooksqlite.databinding.ActivityMainBinding
import ru.alexkubrick.notebooksqlite.db.MyAdapter
import ru.alexkubrick.notebooksqlite.db.MyDbManager

class MainActivity : AppCompatActivity() {
    lateinit var bindingClass: ActivityMainBinding

    val myDbManager = MyDbManager(this)
    val myAdapter = MyAdapter(ArrayList(), this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)
        init()
    }

    // открываю БД не в onCreate, т.к. запустилось бы один раз (согласно циклу жизни активити)
    override fun onResume() {
        super.onResume()
        myDbManager.openDb()
        fillAdapter()
    }

    //добавляем новую заметку
    fun onClickNew(view: View) {
        val i = Intent(this, EditActivity::class.java)
        startActivity(i)

    }

    // здесь иницилизируем RecyclerView
    fun init() {
        bindingClass.rcView.layoutManager = LinearLayoutManager(this) // элементы по вертикали, как в обычном списке
        bindingClass.rcView.adapter = myAdapter
    }

    fun fillAdapter() {

        val list = myDbManager.readDbData()
        myAdapter.updateAdapter(list)

        if(list.size > 0) {
            bindingClass.tvNoElements.visibility = View.GONE
        } else {
            bindingClass.tvNoElements.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDb()
    }


}