package com.assignment.todo.ui.todoList

import android.os.Bundle
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.assignment.todo.R
import com.assignment.todo.ui.adapters.ViewPagerAdapter
import com.assignment.todo.ui.utils.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity(), ToDoListFragment.TodoClicked{
    private var adapter : ViewPagerAdapter ?= null
    private var viewClicked : Boolean = false
    private var pageSelected : String ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adapter = ViewPagerAdapter(supportFragmentManager)
        adapter?.addFragment(ToDoListFragment.newInstance(this), "Task")
        adapter?.addFragment(ToDoDetailFragment.newInstance(""), "Details")
        viewPager.adapter = adapter
        viewPager.currentItem = 0
        tabs.setupWithViewPager(viewPager)
        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                viewClicked = false
            }
            override fun onPageSelected(position: Int) {
                if(position == 1){
                    pageSelected = "Detail"
                    if(viewClicked!!) {
                        ToDoDetailFragment.newInstance("onChildClick")
                    }else{
                        ToDoDetailFragment.newInstance("")
                    }
                    adapter?.notifyDataSetChanged()
                }else{
                    pageSelected = "Task"
                }
            }
        })
    }

    override fun onViewClicked() {
        viewClicked = true
        viewPager.setCurrentItem(1, true)
    }

    override fun onBackPressed() {
        if(pageSelected == "Detail"){
            viewPager.setCurrentItem(0, true)
        }else{
            super.onBackPressed()
        }
    }
}