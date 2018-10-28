package com.example.z003b2z.twodew.main.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.z003b2z.twodew.R
import com.example.z003b2z.twodew.animation.Reveal
import com.example.z003b2z.twodew.di.tasks.TaskItemProvider
import com.example.z003b2z.twodew.di.tasks.WhenItemProvider
import com.example.z003b2z.twodew.di.tasks.WhoItemProvider
import com.example.z003b2z.twodew.main.adapter.MainAdapter
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import android.view.animation.OvershootInterpolator
import jp.wasabeef.recyclerview.animators.LandingAnimator
import kotlinx.android.synthetic.main.main_confirmation.*
import androidx.core.content.ContextCompat
import com.example.z003b2z.twodew.android.ClickListener
import com.example.z003b2z.twodew.android.PromptDialog
import com.example.z003b2z.twodew.android.extensions.plusAssign
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import com.example.z003b2z.twodew.db.entity.Task
import com.example.z003b2z.twodew.main.MainAction
import com.example.z003b2z.twodew.main.MainScreenState
import com.example.z003b2z.twodew.main.MainViewModel
import com.example.z003b2z.twodew.notification.NotificationBuilder
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.main_bottom_sheet.*
import android.view.WindowManager
import android.app.Activity
import androidx.annotation.ColorInt
import com.example.z003b2z.twodew.android.extensions.changeStatusBarColor


//koin
//new material design
//room
//coroutines
//make sure to handle app restart for notifications
//allow user defined action words / time units
//who -> what -> when
//firebase

//todo yet
//settings page - pref for closing , custom tiles
//reminder notification
//manage current notificationsc

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: MainAdapter

    private val mainViewModel: MainViewModel by viewModel()
    private val notificationBuilder: NotificationBuilder by inject()
    private val taskItemProvider: TaskItemProvider by inject()
    private val whoItemProvider: WhoItemProvider by inject()
    private val whenItemProvider: WhenItemProvider by inject()
    private val revealAnimation: Reveal by inject()

    private val compositeDisposable = CompositeDisposable()

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(bottomAppBar)

        setupRecyclerView()

        setupBottomSheet()

        render(mainViewModel.currentState)

        compositeDisposable += mainViewModel.databaseSubject.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it > 0) {
                        buildNotification(it)
                    } else {

                    }
                }, {
                    Timber.e(it)
                })

        compositeDisposable += mainViewModel.bottomSheetDatabaseBehaviorSubject.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mainViewModel.currentState = mainViewModel.previousState
                    mainBottomSheet.updateData(ArrayList(it))
                    render(mainViewModel.reduce(MainAction.DrawerOpened))
                }, {
                    Timber.e(it)
                })

        mainMenuIcon.setOnClickListener {
            render(mainViewModel.reduce(MainAction.FetchTasks))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(mainBottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun buildNotification(id: Long) {
        val builder = notificationBuilder.build(this,
                id.toInt(),
                mainViewModel.buildNotificationText()
        )
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        mainViewModel.scheduleJob(id)

        mNotificationManager.notify(id.toInt(), builder.build())
    }

    private fun setupRecyclerView() {
        mainRecyclerView.layoutManager = GridLayoutManager(this, 4)
        adapter = MainAdapter(arrayListOf(), this::navigationAction, mainViewModel.currentState)
        mainRecyclerView.adapter = adapter

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
            R.id.menu_back -> {
                render(mainViewModel.getPreviousStateFromCurrentState())
                setTextFromAction(MainAction.BackClicked())
            }
        }
        return true
    }

    private fun navigationAction(action: MainAction) {
        if(action.displayText) {
            setTextFromAction(action)
        }
        render(mainViewModel.reduce(action))
    }

    private fun setTextFromAction(action: MainAction) {
        when (action) {
            is MainAction.WhoClicked -> setWhoText(action.text)
            is MainAction.WhatClicked -> setWhatText(action.text)
            is MainAction.WhenClicked -> setWhenText(action.text)
            is MainAction.BackClicked -> {
                when (mainViewModel.currentState) {
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
            is MainScreenState.DrawerOpen -> renderDrawerOpen()
            is MainScreenState.LoadingTasks -> renderLoadingTasks()
        }
    }

    private fun renderLoadingTasks() {
        if(mainViewModel.previousState == MainScreenState.Confirmation) {
            setStateVisibility(confirmation = View.VISIBLE)
        } else {
            setStateVisibility(base = View.VISIBLE)
        }
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        mainViewModel.fetchTasks(mainViewModel.bottomSheetDatabaseBehaviorSubject)
    }

    private fun renderDrawerOpen() {
        mainBottomSheet.setStateVisibility(base = View.VISIBLE)
    }

    private fun renderConfirmationState() {
        setStateVisibility(confirmation = View.VISIBLE)
        confirmationWhoText.post {
            revealAnimation.reveal(confirmationWhoText, baseRevealConfirmationLayout)
        }
    }

    private fun renderWhenState() {
        setStateVisibility(base = View.VISIBLE)
        adapter.updateData(whenItemProvider.provideListOfWhenItems(), mainViewModel.currentState)
    }

    private fun renderWhatState() {
        setStateVisibility(base = View.VISIBLE)
        adapter.updateData(taskItemProvider.provideListOfTaskItems(), mainViewModel.currentState)
    }

    private fun renderWhoState() {
        setStateVisibility(base = View.VISIBLE)
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
        mainViewModel.updateCurrentTask(confirmationWhoText.text, confirmationWhatText.text, confirmationWhenText.text)

        mainFab.setFabState(mainViewModel)

        baseRevealConfirmationLayout.visibility = confirmation
        mainMenuIcon.visibility = base
        mainBaseContent.visibility = base

        if(confirmation == View.GONE) {
            changeStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark), true)
        } else {
            changeStatusBarColor(ContextCompat.getColor(this, R.color.confirmation), false)
        }
    }

}