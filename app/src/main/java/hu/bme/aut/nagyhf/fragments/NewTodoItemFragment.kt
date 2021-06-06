package hu.bme.aut.nagyhf.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.android.material.internal.ViewUtils.getContentView
import hu.bme.aut.nagyhf.R
import hu.bme.aut.nagyhf.data.TodoData
import kotlinx.coroutines.NonCancellable.cancel
import java.time.format.DateTimeFormatter

class NewTodoItemFragment : DialogFragment(){

    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var todoDatePicker: DatePicker


    interface NewTodoItemDialogListener {
        fun onTodoItemCreated(newData: TodoData)
    }

    private lateinit var listener: NewTodoItemDialogListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? NewTodoItemDialogListener
            ?: throw RuntimeException("Activity must implement the NewTodoItemDialogListener interface!")
    }
    private fun isValid() = nameEditText.text.isNotEmpty()

    private fun getTodoItem(): TodoData {
        val year = todoDatePicker.year
        val monthNumber = todoDatePicker.month+1
        val month : String
        month = if(monthNumber < 10){
            "0" +(monthNumber).toString()
        } else{
             (monthNumber).toString()
        }
        val dayNumber  = todoDatePicker.dayOfMonth
        val day : String
        day = if(dayNumber < 10){
            "0" +(dayNumber).toString()
        } else{
            (dayNumber).toString()
        }
        return TodoData(
            id = null,
            todoName = nameEditText.text.toString(),
            todoDate = "$year.$month.$day",
            todoDescription = descriptionEditText.text.toString(),
            calendarEventID = null
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.new_todo_title)
            .setView(getContentView())
            .setPositiveButton(R.string.ok) { _, _ ->
                if (isValid()) {
                    listener.onTodoItemCreated(getTodoItem())
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .create()

    }
    private fun getContentView(): View {
        val contentView =
            LayoutInflater.from(context).inflate(R.layout.new_todo_item_fragment, null)
        nameEditText = contentView.findViewById(R.id.todoItemNameText)
        descriptionEditText = contentView.findViewById(R.id.todoItemDescription)
        todoDatePicker = contentView.findViewById(R.id.todoItemDatePicker)
        return contentView
    }
    companion object {
        const val TAG = "NewTodoItemFragment"
    }
}