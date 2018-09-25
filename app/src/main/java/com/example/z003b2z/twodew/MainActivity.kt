package com.example.z003b2z.twodew

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.z003b2z.twodew.adapter.MainAdapter
import com.example.z003b2z.twodew.android.extensions.focus
import com.example.z003b2z.twodew.di.TaskItemProvider
import com.example.z003b2z.twodew.di.WhoItemProvider
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import android.animation.Animator
import android.view.ViewAnimationUtils
import com.example.z003b2z.twodew.R.id.fab
import androidx.core.content.res.ResourcesCompat
import android.content.res.ColorStateList
import androidx.core.view.ViewCompat.setBackgroundTintList
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.test.*


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

    private var isOpen = false

    @SuppressLint("SetTextI18n")
    override fun itemClicked(text: String) {
        mainText.setText(mainText.text.toString() + text + " ")

        adapter.updateData(taskItemProvider.provideListOfTaskItems())

        viewMenu()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //make it uneditable
        mainText.isEnabled = false

        fab.setOnClickListener {
            mainText.focus()
        }

        mainRecyclerView.layoutManager = GridLayoutManager(this, 4)
        adapter = MainAdapter(whoItemProvider.provideListOfWhoItems(), this)
        mainRecyclerView.adapter = adapter
    }

    private fun viewMenu() {
        val x = fab.bottom
        val y = fab.right

        val startRadius = 0
        val endRadius = Math.hypot(baseLayout.width.toDouble(), baseLayout.height.toDouble()).toInt()

        val anim = ViewAnimationUtils.createCircularReveal(baseLayout, x, y, startRadius.toFloat(), endRadius.toFloat())

        anim.start()

/*        if(isOpen) {
            baseLayout.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark))
            fab.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources,android.R.color.holo_red_dark,null));
        } else {
            baseLayout.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            fab.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources,android.R.color.holo_blue_dark,null));
        }*/
        isOpen = !isOpen
    }

}

