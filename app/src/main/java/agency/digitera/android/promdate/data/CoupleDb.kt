package agency.digitera.android.promdate.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Couple::class],
    version = 1,
    exportSchema = false
)
abstract class CoupleDb : RoomDatabase() {

    companion object {
        fun create(context: Context): CoupleDb {
            val databaseBuilder = Room.databaseBuilder(context, CoupleDb::class.java, "couples.db")
            return databaseBuilder.build()
        }
    }

    abstract fun coupleDao(): CoupleDao
}