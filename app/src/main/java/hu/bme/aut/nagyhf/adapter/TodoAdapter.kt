package hu.bme.aut.nagyhf.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.nagyhf.R
import hu.bme.aut.nagyhf.data.TodoData
import hu.bme.aut.nagyhf.fragments.EditTodoItemFragment
import hu.bme.aut.nagyhf.fragments.NewTodoItemFragment
import kotlinx.android.synthetic.main.todo_row_item.view.*

class TodoAdapter(val context : TodoAdapterListener) : RecyclerView.Adapter<TodoAdapter.ViewHolder>() {

    var todoItems = mutableListOf<TodoData>()
    interface TodoAdapterListener{
        fun onTodoItemDeleted(item: TodoData)
        fun onTodoItemChanged(item: TodoData)
    }
    private lateinit var listener: TodoAdapterListener

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val todo_name = itemView.todo_name;
        val date_text = itemView.date_text;
        val todo_description = itemView.todo_description
        val deleteButton = itemView.delete_button
        val editBtn = itemView.edit_button
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_row_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return todoItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = todoItems[position]
        listener = context
        holder.date_text.text = item.todoDate
        holder.todo_name.text = item.todoName
        holder.todo_description.text = item.todoDescription
        var deleteButton = holder.deleteButton
        deleteButton.setOnClickListener(){
            removeItem(item)
            listener.onTodoItemDeleted(item)
        }
        var editButton = holder.editBtn
        editButton.setOnClickListener(){
            listener.onTodoItemChanged(item)
        }
    }
    private fun removeItem(item: TodoData){
        notifyItemRemoved(todoItems.indexOf(item))
        todoItems.remove(item);

    }
    fun addItem(item : TodoData, position: Int){
        todoItems.add(position, item)
        this.notifyItemInserted(position)
    }

    fun fillList(items: List<TodoData>) {
        todoItems.clear()
        todoItems.addAll(items)
        notifyDataSetChanged()

    }
    fun updateItem(odlItem : TodoData, newItem: TodoData){
        todoItems[todoItems.indexOf(odlItem)] = newItem
        notifyDataSetChanged()
    }
}