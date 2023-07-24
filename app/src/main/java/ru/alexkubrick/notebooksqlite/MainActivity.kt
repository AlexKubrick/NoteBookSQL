package ru.alexkubrick.notebooksqlite

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.alexkubrick.notebooksqlite.databinding.ActivityMainBinding
import ru.alexkubrick.notebooksqlite.db.MyAdapter
import ru.alexkubrick.notebooksqlite.db.MyDbManager

class MainActivity : AppCompatActivity() {
    lateinit var bindingClass: ActivityMainBinding

    val myDbManager = MyDbManager(this)
    val myAdapter = MyAdapter(ArrayList(), this)
    private var job: Job? = null // для остановки корутины. job -- специальный класс


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)
        init()
        initSearchView()
    }

    // открываю БД не в onCreate, т.к. запустилось бы один раз (согласно циклу жизни активити)
    override fun onResume() {
        super.onResume()
        myDbManager.openDb()
        fillAdapter("") // ищет все элементы
    }

    //добавляем новую заметку
    fun onClickNew(view: View) { // переходим на второй экран
        val i = Intent(this, EditActivity::class.java)
        startActivity(i)
    }

    // здесь иницилизируем RecyclerView
    fun init() {
        bindingClass.rcView.layoutManager = LinearLayoutManager(this) // элементы по вертикали, как в обычном списке
        val swapHelper = getSwapMg() // свайп вправо
        swapHelper.attachToRecyclerView(bindingClass.rcView) // прикрепляем к RecyclerView
        bindingClass.rcView.adapter = myAdapter
    }

    fun initSearchView() {
        bindingClass.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean { // поиск по нажатию на кнопку
                return true
            }
            // слушатель, который замечает любые изменения
            // и это будем передавать в БД -- ищем совпадения
            override fun onQueryTextChange(newText: String?): Boolean { // поиск по написанию
                fillAdapter(newText!!) // ищет по слову или букве

                /**
                 * coroutines
                 *
                 * val list = myDbManager.readDbData(newText!!) // считывает из БД
                 * считывание из БД -- трудоемко.
                 * возможно большое кол-во элементов и
                 * считывание может заблокироваь осн. поток
                 *
                 * myAdapter.updateAdapter(list) // и обновляет Адаптер
                 *
                 * обновление адаптера -- не трудоемко, происходит на осн. потоке.
                 * осн. поток блокировать нельзя. осн. поток отвечает за рисование UI.
                 * если заблокировать -- onDraw не запуститься и не закончит рисование.
                 * застынет экран и система может застывший процесс закрыть (приложение упадет).
                */
                return true
            }
        })
    }

    private fun fillAdapter(text: String) { // считывает из БД текст и обновляет адаптер
        // создаем корутину. в скобках указываем поток
        // (в данном случае это основной поток)
        job?.cancel() // если job еще не запущен, то создадим корутину. иначе прекратим процесс
        job = CoroutineScope(Dispatchers.Main).launch {
            val list = myDbManager.readDbData(text)

            // можно сделать suspend. эта функция заблокирует корутину.
            // в таком случае корутина будет ждать пока выполниться эта функция
            myAdapter.updateAdapter(list)

            if (list.size > 0) { // если в БД есть элементы -- прячем надпись "пусто"
                bindingClass.tvNoElements.visibility = View.GONE
            } else {
                bindingClass.tvNoElements.visibility = View.VISIBLE
            }
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
            // viewHolder -- class describes an item view and metadata about its place within the RecyclerView.
            // у каждого элемента есть свой viewHolder. из него берем позицию элемента

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) { // here delete
                myAdapter.removeItem(viewHolder.adapterPosition, myDbManager)
            }
        })
    }
}