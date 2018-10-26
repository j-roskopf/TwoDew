package com.example.z003b2z.twodew.main.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.z003b2z.twodew.android.extensions.plusAssign
import com.example.z003b2z.twodew.db.entity.Task
import com.example.z003b2z.twodew.main.adapter.BottomSheetAdapter
import com.example.z003b2z.twodew.main.model.GenericItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.main_bottom_sheet.view.*
import timber.log.Timber

class MainBottomSheet(context: Context, attributeSet: AttributeSet) : ConstraintLayout(context, attributeSet) {

    private var adapter: BottomSheetAdapter = BottomSheetAdapter(arrayListOf())

    override fun onFinishInflate() {
        super.onFinishInflate()
        bottomSheetContent.adapter = adapter
        bottomSheetContent.layoutManager = LinearLayoutManager(context)
    }

    fun updateData(newData: ArrayList<Task>) {
        adapter.updateData(newData)
    }

    fun setStateVisibility(
            loading: Int = View.GONE,
            base: Int = View.GONE
    ) {
        bottomSheetProgress.visibility = loading
        bottomSheetContent.visibility = base
    }
}