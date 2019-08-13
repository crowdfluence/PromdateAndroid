package agency.digitera.android.promdate.data.db

import agency.digitera.android.promdate.data.dao.UserDao
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
abstract class WishlistDb : RoomDatabase() {

    companion object {
        fun create(context: Context): WishlistDb {
            val databaseBuilder = Room.databaseBuilder(context, WishlistDb::class.java, "wishlist.db")
            return databaseBuilder.build()
        }
    }

    abstract fun userDao(): UserDao
}