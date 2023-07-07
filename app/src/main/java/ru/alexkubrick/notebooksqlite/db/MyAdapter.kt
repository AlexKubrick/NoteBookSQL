package ru.alexkubrick.notebooksqlite.db

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.alexkubrick.notebooksqlite.R

class MyAdapter(listMain:ArrayList<String>) : RecyclerView.Adapter<MyAdapter.MyHolder>() {
    var listArray = listMain

    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)

        fun setData(title: String) {
            tvTitle.text = title

        }
    }

    //берет то что есть в иксмл итем и превращат его в объект на экране
    //готовим для рисования
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyHolder(inflater.inflate(R.layout.rc_item, parent, false))
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
    fun updateAdapter(listItems:List<String>) {
        listArray.clear()
        listArray.addAll(listItems)
        notifyDataSetChanged()
    }

}