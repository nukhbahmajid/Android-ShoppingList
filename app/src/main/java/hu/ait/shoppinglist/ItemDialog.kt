package hu.ait.shoppinglist

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.view.View
import android.widget.*
import androidx.core.view.get
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import hu.ait.shoppinglist.adapter.ShoppingListAdapter
import hu.ait.shoppinglist.data.ShoppingItem
import kotlinx.android.synthetic.main.item_dialog.*
import kotlinx.android.synthetic.main.item_dialog.view.*
import kotlinx.android.synthetic.main.list_item_row.*

class ItemDialog : DialogFragment(), AdapterView.OnItemSelectedListener {

    companion object {
        val MISC = "Miscellaneous"
        val CLOTHES = "Clothes"
        val ELECTRONICS = "Electronics"
        val FOOD = "Food"
    }

    var categories = listOf(MISC, CLOTHES, ELECTRONICS, FOOD)
    var spinner: Spinner? = null
    var selectedCategory = MISC

    interface ShoppingListHandler{
        fun itemCreated(item: ShoppingItem)
        fun itemUpdated(item: ShoppingItem)
    }


    lateinit var shoppingListHandler: ShoppingListHandler

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is ShoppingListHandler){
            shoppingListHandler = context
        } else {
            throw RuntimeException(
                "The Activity is not implementing the ShoppingListAdapter interface.")
        }
    }

    lateinit var etNameItem: EditText
    lateinit var etItemPrice : EditText
    lateinit var etItemDesc : EditText
    lateinit var categorySpinner : Spinner
    lateinit var cbItemBought: CheckBox


    private fun ItemDialog.initializeSpinnerAdapter(): ArrayAdapter<String> {
        val arrayAdapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, categories)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner!!.setAdapter(arrayAdapter)
        return arrayAdapter
    }




    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())

        initializeDialogBuilder(dialogBuilder)

        val arrayAdapter = initializeSpinnerAdapter()

        // to know if in edit mode or not in edit mode.
        val arguments = this.arguments
        if(arguments != null && arguments.containsKey(ScrollingActivity.KEY_EDIT)) {
            val shoppingItem = arguments.getSerializable(ScrollingActivity.KEY_EDIT) as ShoppingItem

            initializeEditDialogBuilder(shoppingItem, arrayAdapter, dialogBuilder)
        }

        dialogBuilder.setPositiveButton("Done") {
                dialog, which ->

        }
        dialogBuilder.setNegativeButton("Cancel") {
                dialog, which ->
        }


        return dialogBuilder.create()
    }


    fun initializeDialogBuilder(dialogBuilder : AlertDialog.Builder) {
        dialogBuilder.setTitle("Add Item")
        val dialogView = requireActivity().layoutInflater.inflate(
            R.layout.item_dialog, null
        )

        // link item properties to dialog elements
        etNameItem = dialogView.etNameItem
        etItemPrice = dialogView.etItemPrice
        etItemDesc = dialogView.etItemDesc
        categorySpinner = dialogView.categorySpinner
        cbItemBought = dialogView.cbItemBought

        spinner = dialogView.categorySpinner
        spinner!!.setOnItemSelectedListener(this)


        dialogBuilder.setView(dialogView)
    }

    fun initializeEditDialogBuilder(item : ShoppingItem, arrayAdapter: ArrayAdapter<String>, dialogBuilder: AlertDialog.Builder) {
        etNameItem.setText(item.name)
        etItemPrice.setText(item.price.toString())
        etItemDesc.setText(item.desc)
        cbItemBought.isChecked = item.bought

        spinner!!.setSelection(arrayAdapter.getPosition(item.category_item))

        dialogBuilder.setTitle("Edit Item")



    }

    override fun onResume() {
        super.onResume()

        val positiveButton = (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            if (etNameItem.text.isNotEmpty()) {
                val arguments = this.arguments
                // IF EDIT MODE
                if (arguments != null && arguments.containsKey(ScrollingActivity.KEY_EDIT)) {
                    handleItemEdit()
                } else {
                    handleItemCreate()
                }

                dialog!!.dismiss()
            } else {
                etNameItem.error = "This field can not be empty"
            }
        }
    }

    private fun handleItemCreate() {
        shoppingListHandler.itemCreated(
            ShoppingItem(
                null,
                categorySpinner.selectedItem.toString(),
                etNameItem.text.toString(),
                etItemDesc.text.toString(),
                etItemPrice.text.toString().toInt(),
                false
            )
        )
    }

    private fun handleItemEdit() {
        val itemToEdit = arguments?.getSerializable(
            ScrollingActivity.KEY_EDIT
        ) as ShoppingItem
        itemToEdit.name = etNameItem.text.toString()
        itemToEdit.bought = cbItemBought.isChecked
        itemToEdit.desc = etItemDesc.text.toString()
        itemToEdit.price = etItemPrice.text.toString().toInt()
        itemToEdit.category_item = categorySpinner.selectedItem.toString()

        shoppingListHandler.itemUpdated(itemToEdit)
    }


    override fun onNothingSelected(parent: AdapterView<*>?) {
        selectedCategory = MISC
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectedCategory = parent!!.getItemAtPosition(position).toString()
    }


}