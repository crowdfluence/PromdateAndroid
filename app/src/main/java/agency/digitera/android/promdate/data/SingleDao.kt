package agency.digitera.android.promdate.data

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SingleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(posts : List<User>)

    @Query("SELECT * FROM User ORDER BY id DESC")
    fun singles() : DataSource.Factory<Int, User>

    @Query("DELETE FROM User")
    fun clearDatabase()
}