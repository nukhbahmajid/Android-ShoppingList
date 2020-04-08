package hu.ait.shoppinglist

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import hu.ait.shoppinglist.adapter.ShoppingListAdapter
import hu.ait.shoppinglist.data.AppDatabase
import hu.ait.shoppinglist.data.ShoppingItem
import hu.ait.shoppinglist.touch.ListRecyclerTouchCallback
import kotlinx.android.synthetic.main.activity_scrolling.*

class ScrollingActivity : AppCompatActivity(), ItemDialog.ShoppingListHandler  {


    lateinit var shoppingListAdapter: ShoppingListAdapter


    companion object{

        const val KEY_EDIT = "KEY_EDIT"
        const val PREF_NAME = "PREF_TODO"
        const val KEY_STARTED = "KEY_STARTED"
        const val KEY_LAST_USED = "LAST_USED"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar)


        initRecyclerView()


        addBtn.setOnClickListener { view ->
           showAddItemDialog()
        }

        dltAllBtn.setOnClickListener { view ->
            shoppingListAdapter.deleteAll()
        }
    }


    private fun initRecyclerView() {
        Thread {
            var shoppingList = AppDatabase.getInstance(this).shoppingListDAO().getAllItems()

            runOnUiThread {
                shoppingListAdapter = ShoppingListAdapter(this, shoppingList)
                recyclerShopping.adapter = shoppingListAdapter

                // Extra feature - swipe to move and dismiss.

                val touchCallbackList = ListRecyclerTouchCallback(shoppingListAdapter)
                val itemTouchHelper = ItemTouchHelper(touchCallbackList)
                itemTouchHelper.attachToRecyclerView(recyclerShopping)

            }
        }.start()
    }

    public fun showAddItemDialog() {
        ItemDialog().show(supportFragmentManager, "CREATE_DIALOG")
    }

    var editIndex : Int = -1


    public fun showEditItemDialog(itemToEdit : ShoppingItem, index : Int) {
        editIndex = index

        val editItemDialog = ItemDialog()

        val bundle = Bundle()
        bundle.putSerializable(KEY_EDIT, itemToEdit)
        editItemDialog.arguments = bundle

        editItemDialog.show(supportFragmentManager, "EDIT_DIALOG")
    }


    fun saveItem(item : ShoppingItem) {
        Thread {
            item.item_id = AppDatabase.getInstance(this).shoppingListDAO().insertItem(item)
            runOnUiThread {
                shoppingListAdapter.addItem(item)
            }

        }.start()

    }

    override fun itemCreated(item: ShoppingItem) {
        saveItem(item)
    }

    override fun itemUpdated(item: ShoppingItem) {
        Thread {
            AppDatabase.getInstance(this).shoppingListDAO().updateItem(item)

            runOnUiThread {
                shoppingListAdapter.updateItem(item, editIndex)
            }
        }.start()

    }






}
