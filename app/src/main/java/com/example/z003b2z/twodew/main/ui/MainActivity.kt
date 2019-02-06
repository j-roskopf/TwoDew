package com.example.z003b2z.twodew.main.ui

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.z003b2z.twodew.R
import com.example.z003b2z.twodew.android.RESULT_DATA_CHANGED
import com.example.z003b2z.twodew.android.WhenDialog
import com.example.z003b2z.twodew.android.extensions.changeStatusBarColor
import com.example.z003b2z.twodew.android.extensions.plusAssign
import com.example.z003b2z.twodew.android.extensions.setDuration
import com.example.z003b2z.twodew.android.startActivityWithAnimation
import com.example.z003b2z.twodew.animation.Reveal
import com.example.z003b2z.twodew.db.TaskDatabase
import com.example.z003b2z.twodew.main.MainAction
import com.example.z003b2z.twodew.main.MainScreenState
import com.example.z003b2z.twodew.main.MainViewModel
import com.example.z003b2z.twodew.main.adapter.MainAdapter
import com.example.z003b2z.twodew.main.adapter.SwipeToDismissCallback
import com.example.z003b2z.twodew.main.adapter.SwipeToSnoozeCallback
import com.example.z003b2z.twodew.notification.NotificationBuilder
import com.example.z003b2z.twodew.settings.PERSISTENCE_KEY
import com.example.z003b2z.twodew.settings.SettingsActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_confirmation.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity : AppCompatActivity() {
  private lateinit var adapter: MainAdapter

  @Volatile
  private var savedInstanceStateCalled = false

  private val mainViewModel: MainViewModel by viewModel()
  private val notificationBuilder: NotificationBuilder by inject()
  private val revealAnimation: Reveal by inject()
  private val sharedPreferences: SharedPreferences by inject()

  private val compositeDisposable = CompositeDisposable()

  private val bottomSheetFragment = MainBottomSheetFragment()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    setSupportActionBar(bottomAppBar)

    setupRecyclerView()

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
        bottomSheetFragment.updateData(it)
      }, {
        Timber.e(it)
      })

    mainMenuIcon.setOnClickListener {
      if(!bottomSheetFragment.isAdded) {
        render(mainViewModel.reduce(MainAction.FetchTasks))
      }
    }

    mainMenuSettings.setOnClickListener {
      startActivityWithAnimation(Intent(this, SettingsActivity::class.java))
    }

    compositeDisposable += TaskDatabase.dbReadySubject.observeOn(AndroidSchedulers.mainThread())
      .subscribe({
        render(mainViewModel.currentState)
      }, {
        Timber.e(it)
      })
  }

  private fun setupBottomSheet() {
    val swipeToDismissHandler = object : SwipeToDismissCallback(this) {
      override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        mainViewModel.deleteItem(viewHolder, bottomSheetFragment)
      }
    }
    val swipeToSnoozeHandler = object : SwipeToSnoozeCallback(this) {
      override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (mainViewModel.validTask(bottomSheetFragment.adapter, viewHolder)) {
          WhenDialog(this@MainActivity) { selectedItem ->
            if(selectedItem != null) {
              mainViewModel.snoozeItem(viewHolder, bottomSheetFragment, selectedItem)
            } else {
              bottomSheetFragment.adapter.notifyItemChanged(viewHolder.adapterPosition)
            }
          }.show()
        } else {
          Toast.makeText(this@MainActivity, "You can't snooze an item with no reminder", Toast.LENGTH_SHORT).show()
          bottomSheetFragment.adapter.notifyItemChanged(viewHolder.adapterPosition)
        }
      }
    }

    bottomSheetFragment.setSwipeListeners(swipeToDismissHandler, swipeToSnoozeHandler)
  }

  private fun buildNotification(id: Long) {
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val builder = notificationBuilder.build(
      this,
      id.toInt(),
      mainViewModel.buildNotificationText(),
      notificationManager,
      sharedPreferences.getBoolean(PERSISTENCE_KEY, false)
    )
    notificationManager.notify(id.toInt(), builder.build())

    if (!mainViewModel.currentTask.`when`.equals("never", ignoreCase = true)) {
      mainViewModel.scheduleJob(id)
    }
  }

  private fun setupRecyclerView() {
    mainRecyclerView.layoutManager = GridLayoutManager(this, 4)
    adapter = MainAdapter(arrayListOf(), this::navigationAction, mainViewModel.currentState)
    mainRecyclerView.adapter = adapter

    val animator = SlideInLeftAnimator(OvershootInterpolator(1f)).setDuration()
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
    if (action.displayText) {
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
      is MainScreenState.LoadingTasks -> renderLoadingTasks()
    }
  }

  private fun renderLoadingTasks() {
    if(savedInstanceStateCalled) return

    if (mainViewModel.previousState == MainScreenState.Confirmation) {
      setStateVisibility(confirmation = View.VISIBLE)
    } else {
      setStateVisibility(base = View.VISIBLE)
    }

    mainViewModel.fetchTasks(mainViewModel.bottomSheetDatabaseBehaviorSubject)

    bottomSheetFragment.show(supportFragmentManager, "")
    setupBottomSheet()
  }

  private fun renderConfirmationState() {
    setStateVisibility(confirmation = View.VISIBLE)
    confirmationWhoText.post {
      revealAnimation.reveal(confirmationWhoText, baseRevealConfirmationLayout)
    }
  }

  private fun renderWhenState() {
    GlobalScope.launch {
      val data = mainViewModel.provideListOfWhenItems()
      GlobalScope.launch(Dispatchers.Main){
        setStateVisibility(base = View.VISIBLE)
        adapter.updateData(data, mainViewModel.currentState)
      }
    }
  }

  private fun renderWhatState() {
    GlobalScope.launch {
      val data = mainViewModel.provideListOfTaskItems()
      GlobalScope.launch(Dispatchers.Main){
        setStateVisibility(base = View.VISIBLE)
        adapter.updateData(data, mainViewModel.currentState)
      }
    }
  }

  private fun renderWhoState() {
    GlobalScope.launch {
      val data = mainViewModel.provideListOfWhoItems()
      GlobalScope.launch(Dispatchers.Main){
        setStateVisibility(base = View.VISIBLE)
        adapter.updateData(data, mainViewModel.currentState)
      }
    }
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
    mainMenuSettings.visibility = base
    mainBaseContent.visibility = base

    if (confirmation == View.GONE) {
      changeStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark), true)
    } else {
      changeStatusBarColor(ContextCompat.getColor(this, R.color.confirmation), false)
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

    if (requestCode == RESULT_DATA_CHANGED && resultCode == Activity.RESULT_OK) {
      render(mainViewModel.currentState)
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    compositeDisposable.dispose()
  }

  override fun onSaveInstanceState(outState: Bundle?) {
    super.onSaveInstanceState(outState)
    savedInstanceStateCalled = true
  }

  override fun onResume() {
    super.onResume()
    savedInstanceStateCalled = false
  }
}