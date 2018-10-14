package com.example.z003b2z.twodew.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.example.z003b2z.twodew.R
import com.example.z003b2z.twodew.animation.Reveal
import com.example.z003b2z.twodew.di.TaskItemProvider
import com.example.z003b2z.twodew.di.WhenItemProvider
import com.example.z003b2z.twodew.di.WhoItemProvider
import com.example.z003b2z.twodew.main.adapter.MainAdapter
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import android.view.animation.OvershootInterpolator
import com.example.z003b2z.twodew.main.model.GenericItem
import jp.wasabeef.recyclerview.animators.LandingAnimator
import kotlinx.android.synthetic.main.main_confirmation.*

//koin
//new material design
//room
//coroutines
//make sure to handle app restart for notifications
//allow user defined action words / time units
//who -> what -> when
//figure out why circular animation doesn't work first time

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: MainAdapter

    private val mainViewModel: MainViewModel by viewModel()

    private val taskItemProvider: TaskItemProvider by inject()
    private val whoItemProvider: WhoItemProvider by inject()
    private val whenItemProvider: WhenItemProvider by inject()

    private val revealAnimation: Reveal by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(bottomAppBar)

        mainWhoTextSwitcher.setFactory { getView() }
        mainWhenTextSwitcher.setFactory { getView() }
        mainWhatTextSwitcher.setFactory { getView() }

        mainRecyclerView.layoutManager = GridLayoutManager(this, 4)
        adapter = MainAdapter(arrayListOf(), this::navigationAction, mainViewModel.currentState)
        mainRecyclerView.adapter = adapter

        render(mainViewModel.currentState)

        val animator = LandingAnimator(OvershootInterpolator(1f))
        mainRecyclerView.itemAnimator = animator
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_back ->{
                render(mainViewModel.getPreviousStateFromCurrentState())
                setTextFromAction(MainAction.BackClicked())
            }
        }
        return true
    }

    private fun navigationAction(action: MainAction) {
        setTextFromAction(action)
        render(mainViewModel.reduce(action))
    }

    private fun setTextFromAction(action: MainAction) {
        when(action) {
            is MainAction.WhoClicked -> setWhoText(action.text)
            is MainAction.WhatClicked -> setWhatText(action.text)
            is MainAction.WhenClicked -> setWhenText(action.text)
            is MainAction.BackClicked -> {
                when(mainViewModel.currentState) {
                    is MainScreenState.Who -> setTextFromAction(MainAction.WhoClicked())
                    is MainScreenState.What -> setTextFromAction(MainAction.WhatClicked())
                    is MainScreenState.When -> setTextFromAction(MainAction.WhenClicked())
                }
            }
        }
    }

    private fun render(currentState: MainScreenState) {
        when (currentState) {
            is MainScreenState.Who -> renderWhoState()
            is MainScreenState.What -> renderWhatState()
            is MainScreenState.When -> renderWhenState()
            is MainScreenState.Confirmation -> renderConfirmationState()
            else -> throw IllegalArgumentException("What state are you?")
        }
    }

    private fun renderConfirmationState() {
        setStateVisibility(confirmation = View.VISIBLE)
        confirmationWhoText.post {
            revealAnimation.reveal(confirmationWhoText, baseRevealConfirmationLayout)
        }
    }

    private fun renderWhenState() {
        setStateVisibility(base = View.VISIBLE)
        setWhenText("When?")
        adapter.updateData(whenItemProvider.provideListOfWhenItems(), mainViewModel.currentState)
    }

    private fun renderWhatState() {
        setStateVisibility(base = View.VISIBLE)
        setWhatText("What?")
        adapter.updateData(taskItemProvider.provideListOfTaskItems(), mainViewModel.currentState)
    }

    private fun renderWhoState() {
        setStateVisibility(base = View.VISIBLE)
        setWhoText("Who?")
        adapter.updateData(whoItemProvider.provideListOfWhoItems(), mainViewModel.currentState)
    }

    private fun setWhoText(text: String) {
        mainWhoTextSwitcher.setText(text)
        confirmationWhoText.text = text
    }

    private fun setWhatText(text: String) {
        mainWhatTextSwitcher.setText(text)
        confirmationWhatText.text = text
    }

    private fun setWhenText(text: String) {
        mainWhenTextSwitcher.setText(text)
        confirmationWhenText.text = text
    }

    private fun setStateVisibility(
            confirmation: Int = View.GONE,
            base: Int = View.GONE
    ) {
        baseRevealConfirmationLayout.visibility = confirmation
        mainBaseContent.visibility = base
    }

    private fun getView(): View {
        val t = TextView(this)
        t.textSize = 22f
        return t
    }

}

