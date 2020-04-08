package hu.ait.shoppinglist.touch

import java.text.FieldPosition

interface ListTouchHelperCallback {
    fun onDismissed(position: Int)
    fun onItemMoved(fromPosition: Int, toPosition: Int)
}