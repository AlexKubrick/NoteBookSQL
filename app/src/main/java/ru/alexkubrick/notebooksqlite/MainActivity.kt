package ru.alexkubrick.notebooksqlite

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    fun onClickNew(view: View) { // переходим на второй экран

        val i = Intent(this, EditActivity::class.java)
        startActivity(i)

    }

    // здесь иницилизируем RecyclerView
    fun init() {

        bindingClass.rcView.layoutManager = LinearLayoutManager(this) // элементы по вертикали, как в обычном списке
        val swapHelper = getSwapMg()
        swapHelper.attachToRecyclerView(bindingClass.rcView) // прикрепляем к RecyclerView
        bindingClass.rcView.adapter = myAdapter
    }

    fun fillAdapter() {

        val list = myDbManager.readDbData()
        myAdapter.updateAdapter(list)

        if (list.size > 0) { // надпись "пусто"
            bindingClass.tvNoElements.visibility = View.GONE
        } else {
            bindingClass.tvNoElements.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {

        super.onDestroy()
        myDbManager.closeDb()
    }

    private fun getSwapMg(): ItemTouchHelper { // свайп вправо

        return ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return false
            }
            //viewHolder -- class describes an item view and metadata about its place within the RecyclerView.
            // у каждого элемента есть свой viewHolder. из него берем позицию элемента

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) { // here delete
                myAdapter.removeItem(viewHolder.adapterPosition, myDbManager)
            }
        })
    }


}