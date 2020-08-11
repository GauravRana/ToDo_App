package com.assignment.todo.ui.todoList

import android.app.Activity
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
import com.assignment.todo.R
import com.assignment.todo.data.db.TodoRecord
import com.assignment.todo.ui.createTodo.CreateTodoActivity
import com.assignment.todo.utils.Constants
import kotlinx.android.synthetic.main.activity_todo_list.*

class ToDoListFragment : Fragment(), TodoListAdapter.TodoEvents {

    private lateinit var todoViewModel: TodoViewModel
    private lateinit var todoAdapter: TodoListAdapter
    private lateinit var rv_todo_list: RecyclerView

    companion object {
        private lateinit var listener: TodoClicked
        fun newInstance(listener: TodoClicked) : Fragment {
            val myFragment = ToDoListFragment()
            this.listener = listener
            return  myFragment
        }
    }

    private lateinit var viewModel: TodoListViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        //Setting up RecyclerView
        return inflater.inflate(R.layout.todo_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_todo_list = view.findViewById(R.id.rv_todo_list)
        rv_todo_list.layoutManager = LinearLayoutManager(activity)
        todoAdapter = TodoListAdapter(this)
        rv_todo_list.adapter = todoAdapter


        //Setting up ViewModel and LiveData
        todoViewModel = ViewModelProviders.of(this).get(TodoViewModel::class.java)
        todoViewModel.getAllTodoList().observe(this, Observer {
            todoAdapter.setAllTodoItems(it)
        })

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

    override fun onDeleteClicked(todoRecord: TodoRecord) {
        todoViewModel.deleteTodo(todoRecord)
    }

    override fun onViewClicked(todoRecord: TodoRecord) {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putLong("id", todoRecord.id!!)
        editor.apply()
        listener.onViewClicked()
    }

    override fun onEditClicked(todoRecord: TodoRecord) {
        val intent = Intent(activity, CreateTodoActivity::class.java)
        intent.putExtra(Constants.INTENT_OBJECT, todoRecord)
        startActivityForResult(intent, Constants.INTENT_UPDATE_TODO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val todoRecord = data?.getParcelableExtra<TodoRecord>(Constants.INTENT_OBJECT)!!
            when (requestCode) {
                Constants.INTENT_CREATE_TODO -> {
                    todoViewModel.saveTodo(todoRecord)
                }
                Constants.INTENT_UPDATE_TODO -> {
                    todoViewModel.updateTodo(todoRecord)
                }
            }
        }
    }

    interface TodoClicked {
        fun onViewClicked()
    }

}