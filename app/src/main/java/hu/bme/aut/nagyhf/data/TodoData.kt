package hu.bme.aut.nagyhf.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TodoData")
data class TodoData(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "todoName") var todoName: String,
    @ColumnInfo(name = "todoDate") var todoDate: String,
    @ColumnInfo(name = "todoDescription") var todoDescription: String,
    @ColumnInfo(name = "calendarEventID") var calendarEventID: Long?
)