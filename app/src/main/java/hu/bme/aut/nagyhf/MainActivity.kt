package hu.bme.aut.nagyhf

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import hu.bme.aut.nagyhf.adapter.TodoAdapter
import hu.bme.aut.nagyhf.data.TodoData
import hu.bme.aut.nagyhf.data.TodoDatabase
import hu.bme.aut.nagyhf.fragments.EditTodoItemFragment
import hu.bme.aut.nagyhf.fragments.NewTodoItemFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), NewTodoItemFragment.NewTodoItemDialogListener,
    TodoAdapter.TodoAdapterListener, EditTodoItemFragment.ChangedTodoItemDialogListener {

    private lateinit var adapter: TodoAdapter
    private lateinit var sharedP: SharedPreferences

    companion object {
        val KEY_LOG = "LOG_PROVIDER"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        initRecycler()


        sharedP = PreferenceManager.getDefaultSharedPreferences(this)
        val nightTime = sharedP.getBoolean("night_mode", true)
        if (nightTime) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        requestNeededPermission()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_item -> {
                NewTodoItemFragment().show(supportFragmentManager, NewTodoItemFragment.TAG)
                true
            }
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun initRecycler() {
        adapter = TodoAdapter(this)
        initListOfTodos()
        recycler_todo.adapter = adapter
    }

    private fun initListOfTodos() {
        thread {
            val items = TodoDatabase.getInstance(this).todoDataDao().getAll()
            runOnUiThread {
                adapter.fillList(items)
            }
        }
    }

    private fun calendarInsert(newItem: TodoData): Long? {
        try {
            val values = ContentValues()
            val dateParts = newItem.todoDate.split(".")
            val sdf = SimpleDateFormat("dd-M-yyyy hh:mm:ss")
            val dateString =
                dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0] + " 00:00:00"
            val date = sdf.parse(dateString);
            values.put(CalendarContract.Events.DTSTART, date.time + 24 * 3600 * 1000)
            values.put(CalendarContract.Events.DTEND, date.time + 25 * 3600 * 1000)
            values.put(CalendarContract.Events.ALL_DAY, 1)
            values.put(CalendarContract.Events.TITLE, newItem.todoName)
            values.put(CalendarContract.Events.DESCRIPTION, newItem.todoDescription)
            values.put(CalendarContract.Events.CALENDAR_ID, 1)
            values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)

            val uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)

            return uri!!.lastPathSegment!!.toLong()
        }catch (e : SecurityException){
            e.printStackTrace()
            return null;
        }


    }

    @SuppressLint("SimpleDateFormat")
    override fun onTodoItemCreated(newItem: TodoData) {
        thread {
            var eventID: Long? = null
            if (sharedP.getBoolean("calendar_save", true)) {
                eventID = calendarInsert(newItem)
            }
                newItem.calendarEventID = eventID
                val newId = TodoDatabase.getInstance(this).todoDataDao().insert(newItem)
                val newTodoData = newItem.copy(
                    id = newId
                )
                runOnUiThread {
                    adapter.addItem(newTodoData, 0)

                }
            }
        }

    private fun requestNeededPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CALENDAR
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_CALENDAR
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR),
                101
            )
        } else {
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            101 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(this@MainActivity, "Permissions granted", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Permissions are NOT granted", Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
    }

    override fun onTodoItemDeleted(item: TodoData) {
        thread {
            TodoDatabase.getInstance(this).todoDataDao().deleteItem(item)
            runOnUiThread() {
                try {
                    contentResolver.delete(CalendarContract.Events.CONTENT_URI, CalendarContract.Events._ID+"="+ item.calendarEventID.toString(), null)
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }

            }
        }
    }

    override fun onTodoItemChanged(item: TodoData) {
        EditTodoItemFragment(item).show(supportFragmentManager, EditTodoItemFragment.TAG)
    }

    override fun onTodoItemEdited(oldData: TodoData, newData: TodoData) {
        thread {
            try{
                contentResolver.delete(CalendarContract.Events.CONTENT_URI, CalendarContract.Events._ID+"="+ oldData.calendarEventID.toString(), null)
            }catch (e: SecurityException){
                e.printStackTrace()
            }
            val newTodoData = newData.copy(
                id = oldData.id
            )
            if (newTodoData.todoName.isEmpty())
                newTodoData.todoName = oldData.todoName
            if (newTodoData.todoDescription.isEmpty())
                newTodoData.todoDescription = oldData.todoDescription
            val eventID = calendarInsert(newTodoData)

            newTodoData.calendarEventID = eventID
            TodoDatabase.getInstance(this).todoDataDao().update(newTodoData)
            runOnUiThread {
                adapter.updateItem(oldData, newTodoData)
            }
        }
    }

}