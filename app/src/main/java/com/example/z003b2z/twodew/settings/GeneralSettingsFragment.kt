package com.example.z003b2z.twodew.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.z003b2z.twodew.R
import kotlinx.android.synthetic.main.general_settings_fragment.persistentSettingCheck
import org.koin.android.ext.android.inject

const val PERSISTENCE_KEY = "persistent_notifications"

class GeneralSettingsFragment : Fragment() {

  private val sharedPreferences: SharedPreferences by inject()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.general_settings_fragment, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    persistentSettingCheck.isChecked = sharedPreferences.getBoolean(PERSISTENCE_KEY, false)
    persistentSettingCheck.setOnCheckedChangeListener { _, isChecked ->
      sharedPreferences.edit().putBoolean(PERSISTENCE_KEY, isChecked).apply()
    }
  }


  companion object {

    private val ARG_SECTION_NUMBER = "section_number"

    fun newInstance(sectionNumber: Int): GeneralSettingsFragment {
      val fragment = GeneralSettingsFragment()
      val args = Bundle()
      args.putInt(ARG_SECTION_NUMBER, sectionNumber)
      fragment.arguments = args
      return fragment
    }
  }
}