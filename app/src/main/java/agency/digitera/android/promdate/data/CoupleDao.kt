package agency.digitera.android.promdate.data

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CoupleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(posts : List<Couple>)

    @Query("SELECT * FROM Couple")
    fun couples() : DataSource.Factory<Int, Couple>

    @Query("DELETE FROM Couple")
    fun clearDatabase()
}