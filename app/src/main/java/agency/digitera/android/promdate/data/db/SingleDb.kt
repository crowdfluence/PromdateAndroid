package agency.digitera.android.promdate.data.db

import agency.digitera.android.promdate.data.dao.SingleDao
import agency.digitera.android.promdate.data.entities.User
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [User::class],
    version = 1,
    exportSchema = false
)
abstract class SingleDb : RoomDatabase() {

    companion object {
        fun create(context: Context): SingleDb {
            val databaseBuilder = Room.databaseBuilder(context, SingleDb::class.java, "single_users.db")
            return databaseBuilder.build()
        }
    }

    abstract fun singleDao(): SingleDao
}