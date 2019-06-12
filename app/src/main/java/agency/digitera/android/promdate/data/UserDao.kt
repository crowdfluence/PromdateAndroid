package agency.digitera.android.promdate.data

import androidx.paging.DataSource
import androidx.room.*

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(posts : List<User>)

    @Query("SELECT * FROM User ORDER BY id DESC")
    fun users() : DataSource.Factory<Int, User>

    @Query("DELETE FROM User")
    fun clearDatabase()
}