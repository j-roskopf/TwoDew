package com.example.z003b2z.twodew

import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.z003b2z.twodew.adapter.MainAdapter
import com.example.z003b2z.twodew.di.TaskItemProvider
import com.example.z003b2z.twodew.di.WhenItemProvider
import com.example.z003b2z.twodew.di.WhoItemProvider
import com.example.z003b2z.twodew.model.ScreenState
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject


//koin
//new material design
//room
//coroutines
//make sure to handle app restart for notifications
//allow user defined action words / time units

class MainActivity : AppCompatActivity(), MainAdapter.OnItemClickListener {
    lateinit var adapter: MainAdapter

    private val taskItemProvider: TaskItemProvider by inject()
    private val whoItemProvider: WhoItemProvider by inject()
    private val whenItemProvider: WhenItemProvider by inject()

    private var isOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainWhoTextSwitcher.setFactory { getView() }
        mainWhenTextSwitcher.setFactory { getView() }

        val `in` = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
        mainWhoTextSwitcher.inAnimation = `in`
        mainWhenTextSwitcher.inAnimation = `in`

        val out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right)
        mainWhoTextSwitcher.outAnimation = out
        mainWhenTextSwitcher.outAnimation = out

        mainWhatEditText.isEnabled = false

        mainRecyclerView.layoutManager = GridLayoutManager(this, 4)
        adapter = MainAdapter(whoItemProvider.provideListOfWhoItems(), this, ScreenState.WHO)
        mainRecyclerView.adapter = adapter

        mainWhoTextSwitcher.setText("Who?")
    }

    override fun whoItemClicked(text: String) {
        adapter.updateData(taskItemProvider.provideListOfTaskItems(), ScreenState.WHAT)
        mainWhoTextSwitcher.setText(text)

        mainWhatEditText.setText("What?")
    }

    override fun whatItemClicked(text: String) {
        adapter.updateData(whenItemProvider.provideListOfWhenItems(), ScreenState.WHEN)
        mainWhatEditText.setText(text)
        mainWhenTextSwitcher.setText("When?")
    }

    override fun whenItemClicked(text: String) {
        mainWhenTextSwitcher.setText(text)
    }

    private fun getView(): View {
        val t = TextView(this)
        t.textSize = 22f
        return t
    }

    private fun viewMenu() {
        val x = fab.bottom
        val y = fab.right

        val startRadius = 0
        val endRadius = Math.hypot(baseLayout.width.toDouble(), baseLayout.height.toDouble()).toInt()

        val anim = ViewAnimationUtils.createCircularReveal(baseLayout, x, y, startRadius.toFloat(), endRadius.toFloat())

        anim.start()

        isOpen = !isOpen
    }

}

