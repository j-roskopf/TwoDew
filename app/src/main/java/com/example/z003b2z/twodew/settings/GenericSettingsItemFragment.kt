package com.example.z003b2z.twodew.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.z003b2z.twodew.R
import com.example.z003b2z.twodew.db.TaskRepository
import com.example.z003b2z.twodew.db.entity.GenericSettingsEntity
import com.example.z003b2z.twodew.db.entity.Type
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator
import kotlinx.android.synthetic.main.activity_main.mainRecyclerView
import kotlinx.android.synthetic.main.generic_settings_fragment.genericSettingRecyclerView
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch
import org.koin.android.ext.android.inject

class GenericSettingsItemFragment : Fragment() {

  private val repository: TaskRepository by inject()
  val adapter = GenericSettingsItemAdapter(ArrayList()) { it -> removeItem(it) }

  private val type: Type by lazy { arguments?.getSerializable(ARG_TYPE) as Type }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.generic_settings_fragment, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupRecyclerView()
    fetchData(false)
  }

  private fun setupRecyclerView() {
    genericSettingRecyclerView.adapter = adapter
    genericSettingRecyclerView.layoutManager = GridLayoutManager(context, 4)
  }

  fun fetchData(scrollToBottom: Boolean) {
    GlobalScope.launch {
      val data = when (type) {
        Type.WHEN -> repository.selectAllWhen()
        Type.WHAT -> repository.selectAllWhat()
        Type.WHO -> repository.selectAllWho()
      }

      GlobalScope.launch(Dispatchers.Main) {

        adapter.updateData(data)

        if (scrollToBottom) {
          genericSettingRecyclerView.scrollToPosition(adapter.itemCount - 1)
        }
      }
    }
  }

  private fun removeItem(it: GenericSettingsEntity) {
    GlobalScope.launch {
      repository.delete(it)
      fetchData(false)
    }
  }

  companion object {

    private const val ARG_TYPE = "type"

    fun newInstance(type: Type): GenericSettingsItemFragment {
      val fragment = GenericSettingsItemFragment()
      val args = Bundle()
      args.putSerializable(ARG_TYPE, type)
      fragment.arguments = args
      return fragment
    }
  }
}