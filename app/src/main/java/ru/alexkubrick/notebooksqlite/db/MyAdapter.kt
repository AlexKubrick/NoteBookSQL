package ru.alexkubrick.notebooksqlite.db

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.alexkubrick.notebooksqlite.EditActivity
import ru.alexkubrick.notebooksqlite.R

class MyAdapter(listMain:ArrayList<ListItem>, contextM: Context) : RecyclerView.Adapter<MyAdapter.MyHolder>() {
    var listArray = listMain
    val context = contextM

    class MyHolder(itemView: View, contextV: Context) : RecyclerView.ViewHolder(itemView) {
        val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
        val context = contextV

        fun setData(item: ListItem) {
            tvTitle.text = item.title

            itemView.setOnClickListener {
                val intent = Intent(context, EditActivity::class.java).apply {
                    putExtra(MyIntentConstance.I_TITLE_KEY, item.title)
                    putExtra(MyIntentConstance.I_DESC_KEY, item.desc)
                    putExtra(MyIntentConstance.I_URI_KEY, item.uri)

                }
                context.startActivity(intent)
            }

        }
    }

    //берет то, что есть в rc_item.xml, и превращает его в объект на экране
    //готовим для рисования
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val inflater = LayoutInflater.from(parent.context) //берет xml и готовит его для рисования
        return MyHolder(inflater.inflate(R.layout.rc_item, parent, false), context) //готовый шаблон
    }

    //сколько элементов в списке. передаем размер массива
    override fun getItemCount(): Int {
        return listArray.size
    }

    //запускается когда MyHolder создан
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.setData(listArray[position])
    }

    //берет массив -- очищает, добавляет новый массив, и сообщает что данные изменились
    //адаптер перерисовывает
    fun updateAdapter(listItems:List<ListItem>) {
        listArray.clear()
        listArray.addAll(listItems)
        notifyDataSetChanged()
    }

}