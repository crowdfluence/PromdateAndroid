package agency.digitera.android.promdate.data.dao

import agency.digitera.android.promdate.data.entities.User
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(posts: List<User>)

    @Query("SELECT * FROM User ORDER BY id DESC")
    fun users(): DataSource.Factory<Int, User>

    @Query("DELETE FROM User")
    fun clearDatabase()
}