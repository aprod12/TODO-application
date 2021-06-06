package hu.bme.aut.nagyhf.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities = [TodoData::class], version = 1)
abstract class TodoDatabase : RoomDatabase(){
    abstract fun todoDataDao(): TodoDataDao

    companion object {
        private var INSTANCE: TodoDatabase? = null

        fun getInstance(contex: Context): TodoDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    contex.getApplicationContext(),
                    TodoDatabase::class.java,
                    "todoItems.db"
                ).build()
            }
            return INSTANCE!!
        }
        fun destroyInstance(){
            INSTANCE = null
        }

    }

}