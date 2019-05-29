package agency.digitera.android.promdate.data

import androidx.paging.DataSource
import androidx.room.*

@Dao
interface SingleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(posts : List<User>)

    @Query("SELECT * FROM User ORDER BY id DESC")
    fun singles() : DataSource.Factory<Int, User>

    @Query("DELETE FROM User")
    fun clearDatabase()

    @Update
    fun updateUser(vararg user: User)
}