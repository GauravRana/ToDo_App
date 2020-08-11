package com.assignment.todo.ui.todoList

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.assignment.todo.R
import com.assignment.todo.data.db.TodoRecord
import kotlinx.android.synthetic.main.todo_detail_item.view.*
import kotlinx.android.synthetic.main.todo_item.view.tv_item_content
import kotlinx.android.synthetic.main.todo_item.view.tv_item_title


class ToDoDetailAdapter(todoEvents: TodoDetailEvents) : RecyclerView.Adapter<ToDoDetailAdapter.ViewHolder>(), Filterable {

    private var todoList: List<TodoRecord> = arrayListOf()
    private var filteredTodoList: List<TodoRecord> = arrayListOf()
    private val listener: TodoDetailEvents = todoEvents

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_detail_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = filteredTodoList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredTodoList[position], listener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(todo: TodoRecord, listener: TodoDetailEvents) {
            itemView.tv_item_title.text = todo.title
            itemView.tv_item_content.text = todo.content

            itemView.tv_item_content.setTypeface(null, Typeface.BOLD)

            if(todo.status == 0){
                itemView.ivUndone.setImageResource(R.drawable.ic_undone_check)
                itemView.ivDone.setImageResource(R.drawable.ic_done_uncheck)
            }
            if(todo.status == 1){
                itemView.ivDone.setImageResource(R.drawable.ic_done_check)
                itemView.ivUndone.setImageResource(R.drawable.ic_undone_uncheck)
            }

            itemView.ivDone.setOnClickListener {
                itemView.ivDone.setImageResource(R.drawable.ic_done_check)
                itemView.ivUndone.setImageResource(R.drawable.ic_undone_uncheck)
                listener.onDoneClicked(todo)
            }

            itemView.ivUndone.setOnClickListener {
                itemView.ivDone.setImageResource(R.drawable.ic_done_uncheck)
                itemView.ivUndone.setImageResource(R.drawable.ic_undone_check)
                listener.onUnDoneClicked(todo)
            }

        }
    }
    /**
     * Search Filter implementation
     * */
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val charString = p0.toString()
                filteredTodoList = if (charString.isEmpty()) {
                    todoList
                } else {
                    val filteredList = arrayListOf<TodoRecord>()
                    for (row in todoList) {
                        if (row.title.toLowerCase().contains(charString.toLowerCase())
                                || row.content.contains(charString.toLowerCase())) {
                            filteredList.add(row)
                        }
                    }
                    filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = filteredTodoList
                return filterResults
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                filteredTodoList = p1?.values as List<TodoRecord>
                notifyDataSetChanged()
            }

        }
    }

    /**
     * Activity uses this method to update todoList with the help of LiveData
     * */
    fun setAllTodoItems(todoItems: List<TodoRecord>) {
        this.todoList = todoItems
        this.filteredTodoList = todoItems
        notifyDataSetChanged()
    }


    /**
     * RecycleView touch event callbacks
     * */
    interface TodoDetailEvents {
        fun onDoneClicked(todoRecord: TodoRecord)
        fun onUnDoneClicked(todoRecord: TodoRecord)
    }
}