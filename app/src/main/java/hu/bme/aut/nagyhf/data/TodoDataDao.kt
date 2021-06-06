package hu.bme.aut.nagyhf.data

import androidx.room.*

@Dao
interface TodoDataDao {
    @Query("SELECT * FROM TodoData")
    fun getAll(): List<TodoData>

    @Insert
    fun insert(todoItems: TodoData): Long

    @Update
    fun update(todoItems: TodoData)

    @Delete
    fun deleteItem(todoItems: TodoData)
}