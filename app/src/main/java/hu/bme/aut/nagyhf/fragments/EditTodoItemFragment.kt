package hu.bme.aut.nagyhf.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import hu.bme.aut.nagyhf.R
import hu.bme.aut.nagyhf.data.TodoData
import kotlinx.android.synthetic.main.edit_todo_item.*

class EditTodoItemFragment(editableItem : TodoData) : DialogFragment(){

    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var todoDatePicker: DatePicker
    private lateinit var todoItmeCheckBox : CheckBox
    private var editable = editableItem

    interface ChangedTodoItemDialogListener {
        fun onTodoItemEdited(oldData:TodoData ,newData: TodoData)
    }

    private lateinit var listener: ChangedTodoItemDialogListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? ChangedTodoItemDialogListener
            ?: throw RuntimeException("Activity must implement the ChangedTodoItemDialogListener interface!")
    }

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
        } else {
            (dayNumber).toString()
        }
        val actualDate : String
        if(todoItmeCheckBox.isChecked)
            actualDate = "$year.$month.$day"
        else
            actualDate = editable.todoDate
        return TodoData(
            id = null,
            todoName = nameEditText.text.toString(),
            todoDate = actualDate,
            todoDescription = descriptionEditText.text.toString(),
            calendarEventID = null
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.edit_todo_title)
            .setView(getContentView())
            .setPositiveButton(R.string.save_btn) { _, _ ->
                    listener.onTodoItemEdited(editable ,this.getTodoItem())
            }
            .setNegativeButton(R.string.cancel, null)
            .create()

    }
    private fun getContentView(): View {
        val contentView =
            LayoutInflater.from(context).inflate(R.layout.edit_todo_item, null)
        nameEditText = contentView.findViewById(R.id.todoItemEditNameText)
        descriptionEditText = contentView.findViewById(R.id.todoItemEditDescription)
        todoDatePicker = contentView.findViewById(R.id.todoItemEditDatePicker)
        todoItmeCheckBox = contentView.findViewById(R.id.is_date_changed_cb)
        return contentView
    }
    companion object {
        const val TAG = "EditTodoItemFragment"
    }
}