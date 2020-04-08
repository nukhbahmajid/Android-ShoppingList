package hu.ait.shoppinglist.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "shoppingList")
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true) var item_id : Long?,
    @ColumnInfo(name = "category") var category_item : String,
    @ColumnInfo(name = "name") var name : String,
    @ColumnInfo(name = "description") var desc : String,
    @ColumnInfo(name = "price") var price : Int,
    @ColumnInfo(name = "bought") var bought : Boolean

): Serializable