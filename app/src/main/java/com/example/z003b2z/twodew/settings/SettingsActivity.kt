package com.example.z003b2z.twodew.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.z003b2z.twodew.R
import com.example.z003b2z.twodew.android.InputDialog
import com.example.z003b2z.twodew.android.extensions.changeStatusBarColor
import com.example.z003b2z.twodew.db.TaskRepository
import com.example.z003b2z.twodew.db.entity.GenericSettingsEntity
import com.example.z003b2z.twodew.db.entity.Type
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

const val ARG_DATA_CHANGED = "data_changed"

//TODO JOE REFACTOR WITH VIEWMODEL
class SettingsActivity : AppCompatActivity() {

  private val pagerAdapter: SectionsPagerAdapter by lazy { SectionsPagerAdapter(supportFragmentManager) }

  private val repository: TaskRepository by inject()

  private var currentType = Type.WHO

  private val returnIntent = Intent()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_settings)

    setSupportActionBar(settingsAppBar)

    changeStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark), true)

    settingsViewPager.adapter = pagerAdapter
    settingsViewPager.offscreenPageLimit = 4

    settingsViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
      override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        settingsTabLayout.setScrollPosition(position, positionOffset, true)
      }

      override fun onPageScrollStateChanged(state: Int) {}

      override fun onPageSelected(position: Int) {
        toggleFabVisibility(position)
        updateCurrentType(position)
      }
    })

    settingsTabLayout.addOnTabSelectedListener(object : TabLayout.BaseOnTabSelectedListener<TabLayout.Tab> {
      override fun onTabReselected(p0: TabLayout.Tab?) {
        settingsViewPager.currentItem = p0?.position ?: 0
      }

      override fun onTabSelected(p0: TabLayout.Tab?) {
        settingsViewPager.currentItem = p0?.position ?: 0
      }

      override fun onTabUnselected(p0: TabLayout.Tab?) {}
    })

    settingsFab.setOnClickListener { fab ->
      InputDialog(fab.context, currentType.toString().toLowerCase().capitalize(), currentType) { text ->
        if(text?.isNotEmpty() == true) {
          insertItem(text)
        }
      }.show()
    }
  }

  private fun insertItem(text: String) {
    GlobalScope.launch {
      repository.insertCustomItem(GenericSettingsEntity(0, text, currentType))
      GlobalScope.launch(Dispatchers.Main) {
        val fragment =
          (supportFragmentManager.fragments[settingsViewPager.currentItem] as GenericSettingsItemFragment)
        fragment.fetchData(true)
        returnIntent.putExtra(ARG_DATA_CHANGED, true)
      }
    }
  }

  private fun toggleFabVisibility(position: Int) {
    if (position == pagerAdapter.count - 1) {
      settingsFab.hide()
    } else {
      settingsFab.show()
    }
  }

  private fun updateCurrentType(position: Int) {
    currentType = SectionsPagerAdapter.getTypeFromPosition(position)
  }

  override fun onDestroy() {
    super.onDestroy()
    settingsViewPager.clearOnPageChangeListeners()
  }

  override fun onPause() {
    super.onPause()
    if (isFinishing) {
      overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    val inflater = menuInflater
    inflater.inflate(R.menu.main_menu, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.menu_back -> {
        val result = if (returnIntent.hasExtra(ARG_DATA_CHANGED)) Activity.RESULT_OK else Activity.RESULT_OK
        setResult(result, returnIntent)
        finish()
      }
    }
    return true
  }
}

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

  companion object {
    fun getItem(position: Int): Fragment {
      return when (position) {
        0 -> GenericSettingsItemFragment.newInstance(getTypeFromPosition(position))
        1 -> GenericSettingsItemFragment.newInstance(getTypeFromPosition(position))
        2 -> GenericSettingsItemFragment.newInstance(getTypeFromPosition(position))
        3 -> GeneralSettingsFragment.newInstance(position)
        else -> throw RuntimeException("There aren't this many fragments to serve")
      }
    }

    fun getTypeFromPosition(position: Int): Type {
      return when (position) {
        0 -> Type.WHO
        1 -> Type.WHAT
        2 -> Type.WHEN
        3 -> Type.WHO
        else -> throw RuntimeException("There aren't this many fragments to serve")
      }
    }
  }

  override fun getItem(position: Int): Fragment {
    return SectionsPagerAdapter.getItem(position)
  }

  override fun getCount(): Int {
    return 4
  }
}