package hu.ait.shoppinglist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.ait.shoppinglist.ItemDialog
import hu.ait.shoppinglist.R
import hu.ait.shoppinglist.ScrollingActivity
import hu.ait.shoppinglist.data.AppDatabase
import hu.ait.shoppinglist.data.ShoppingItem
import hu.ait.shoppinglist.touch.ListTouchHelperCallback
import kotlinx.android.synthetic.main.list_item_row.view.*
import java.util.*

class ShoppingListAdapter : RecyclerView.Adapter<ShoppingListAdapter.ViewHolder>, ListTouchHelperCallback {

    var listItems = mutableListOf<ShoppingItem>()

    var context : Context
    constructor(context : Context, listItemsUI : List<ShoppingItem>) {
        this.context = context
        listItems.addAll(listItemsUI)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var shoppingListView = LayoutInflater.from(context).inflate(R.layout.list_item_row, parent, false)
        return ViewHolder(shoppingListView)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var currentItem  = listItems[position]
        linkProperties(currentItem, holder)

        holder.btnEdit.setOnClickListener {
            (this.context as ScrollingActivity).showEditItemDialog(listItems[holder.adapterPosition],
                holder.adapterPosition)
        }


        holder.btnDelete.setOnClickListener {
            deleteItem(holder.adapterPosition)
        }

        holder.cbBought.setOnClickListener {
            listItems[holder.adapterPosition].bought = holder.cbBought.isChecked
            Thread{
                AppDatabase.getInstance(context).shoppingListDAO().updateItem(listItems[holder.adapterPosition])

            }.start()
        }


    }


    override fun getItemCount(): Int {
        return listItems.size
    }


    private fun linkProperties(currentItem : ShoppingItem, holder: ViewHolder) {
        holder.itemName.text = currentItem.name
        holder.cbBought.isChecked = currentItem.bought
        holder.itemPrice.text = currentItem.price.toString()
        holder.itemDesc.text = currentItem.desc


        getCategoryIcon(currentItem, holder)

    }

    private fun getCategoryIcon(item : ShoppingItem, holder : ViewHolder)   {
        if (item.category_item == ItemDialog.FOOD) {
            holder.categoryIcon.setImageResource(R.drawable.ic_food)
        } else if (item.category_item == ItemDialog.CLOTHES) {
            holder.categoryIcon.setImageResource(R.drawable.ic_clothes)
        } else if (item.category_item == ItemDialog.ELECTRONICS) {
            holder.categoryIcon.setImageResource(R.drawable.ic_electronics)
        } else {
            holder.categoryIcon.setImageResource(R.drawable.ic_misc)
        }

    }

    public fun addItem(item : ShoppingItem) {
        listItems.add(item)
        // redraw the whole list
        notifyItemInserted(listItems.lastIndex)
    }

    public fun updateItem(item : ShoppingItem, editIndex : Int) {
        listItems.set(editIndex, item)
        notifyItemChanged(editIndex)
    }


    public fun deleteItem(position: Int) {
        Thread {
            AppDatabase.getInstance(context).shoppingListDAO().deleteItem(
                listItems.get(position)
            )

            (context as ScrollingActivity).runOnUiThread {
                listItems.removeAt(position)
                notifyItemRemoved(position)
            }
        }.start()
    }


    public fun deleteAll(){
        Thread {
            AppDatabase.getInstance(context).shoppingListDAO().deleteAllItems()

            (context as ScrollingActivity).runOnUiThread {
                listItems.clear()
                notifyDataSetChanged()
            }
        }.start()
    }

    override fun onDismissed(position: Int) {
        deleteItem(position)
    }


    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(listItems, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }




    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val itemName =  itemView.itemName
        val cbBought = itemView.cbBought
        val itemPrice = itemView.itemPrice
        val categoryIcon = itemView.categoryIcon
        val itemDesc = itemView.itemDesc
        val btnEdit = itemView.btnEdit
        val btnDelete = itemView.btnDelete


    }
}