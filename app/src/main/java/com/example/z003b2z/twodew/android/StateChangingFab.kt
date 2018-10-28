package com.example.z003b2z.twodew.android

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextSwitcher
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.z003b2z.twodew.R
import com.example.z003b2z.twodew.db.entity.Task
import com.example.z003b2z.twodew.main.MainScreenState
import com.example.z003b2z.twodew.main.MainViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_confirmation.*

class StateChangingFab(context: Context, attributeSet: AttributeSet): FloatingActionButton(context, attributeSet) {

    fun setFabState(mainViewModel: MainViewModel) {
        if (mainViewModel.currentState == MainScreenState.Confirmation) {
            setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_check_white_24dp))
            setOnClickListener {
                mainViewModel.insertTask(
                        who = mainViewModel.currentTask.who,
                        what = mainViewModel.currentTask.what,
                        `when` = mainViewModel.currentTask.`when`
                )
            }
        } else {
            setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_edit_white_24dp))
            setOnClickListener {
                PromptDialog(context, android.R.string.paste, android.R.string.httpErrorUnsupportedScheme, object : ClickListener {
                    override fun clicked(text: String) = if(!text.isBlank()) {
                        //TODO JOE add custom text field and still make the user choose a time
/*                            mainViewModel.currentTask = Task(text)
                            mainViewModel.insertTask(mainViewModel.currentTask.text)*/
                    } else {
                        Toast.makeText(context, "Please enter some text", Toast.LENGTH_SHORT).show()
                    }
                }).show()
            }
        }
    }
}