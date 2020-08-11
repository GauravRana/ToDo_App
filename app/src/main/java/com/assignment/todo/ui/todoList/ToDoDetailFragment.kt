package com.assignment.todo.ui.todoList

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.assignment.todo.R
import com.assignment.todo.data.db.TodoRecord
import com.assignment.todo.ui.createTodo.CreateTodoActivity
import com.assignment.todo.utils.Constants

class ToDoDetailFragment : Fragment(), ToDoDetailAdapter.TodoDetailEvents {

    private lateinit var todoViewModel: TodoViewModel
    private lateinit var todoAdapter: ToDoDetailAdapter
    private lateinit var rv_todo_list: RecyclerView
    private lateinit var fab_new_todo: FloatingActionButton
    private lateinit var type : String

    companion object {
        private lateinit var type: String
        fun newInstance(type: String) : Fragment {
            val myFragment = ToDoDetailFragment()
            this.type = type
            return  myFragment
        }
    }


    private lateinit var viewModel: TodoListViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        //Setting up RecyclerView
        return inflater.inflate(R.layout.todo_list_fragment, container, false)
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab_new_todo =  view.findViewById(R.id.fab_new_todo)
        rv_todo_list = view.findViewById(R.id.rv_todo_list)
        rv_todo_list.layoutManager = LinearLayoutManager(activity)
        fab_new_todo!!.visibility = View.GONE
        todoAdapter = ToDoDetailAdapter(this)
        rv_todo_list.adapter = todoAdapter


        //Setting up ViewModel and LiveData
        todoViewModel = ViewModelProviders.of(this).get(TodoViewModel::class.java)
        if(ToDoDetailFragment.type == "onChildClick"){
            val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
            var id: Long = preferences.getLong("id", 0L)
            todoViewModel.getSelectedToDo(id).observe(this, Observer {
                todoAdapter.setAllTodoItems(it)
            })
        }else{
            todoViewModel.getAllTodoList().observe(this, Observer {
                todoAdapter.setAllTodoItems(it)
            })
        }
        //FAB click listener
        fab_new_todo.setOnClickListener {
            val intent = Intent(activity!!, CreateTodoActivity::class.java)
            startActivityForResult(intent, Constants.INTENT_CREATE_TODO)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TodoListViewModel::class.java)
    }

    override fun onDoneClicked(todoRecord: TodoRecord) {
        val id = if (todoRecord != null) todoRecord?.id else null
        val todo = TodoRecord(id = id, title = todoRecord.title, content = todoRecord.content, status = 1)
        todoViewModel.updateTodo(todo)
    }

    override fun onUnDoneClicked(todoRecord: TodoRecord) {
        val id = if (todoRecord != null) todoRecord?.id else null
        val todo = TodoRecord(id = id, title = todoRecord.title, content = todoRecord.content, status = 0)
        todoViewModel.updateTodo(todo)
    }


}