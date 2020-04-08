package hu.ait.shoppinglist.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase



@Database(entities = arrayOf(ShoppingItem::class), version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun shoppingListDAO(): ShoppingListDAO

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase::class.java, "shopping_list.db").fallbackToDestructiveMigration()
                    .build() //fallback thing drops the previous table (all previous outdated data lost)
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
