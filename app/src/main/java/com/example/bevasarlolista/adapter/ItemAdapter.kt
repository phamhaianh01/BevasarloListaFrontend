package com.example.bevasarlolista.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bevasarlolista.R
import com.example.bevasarlolista.model.Item
import com.example.bevasarlolista.model.User

class ItemAdapter(
    private val items: List<Item>,
    private val currentUser: User, // Logged-in user
    private val onItemChecked: (Item, Boolean) -> Unit,
    private val onItemClick: (Item) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.itemName)
        val itemAmount: TextView = itemView.findViewById(R.id.itemAmount)
        val itemPrice: TextView = itemView.findViewById(R.id.itemPrice)
        val itemCategory: TextView = itemView.findViewById(R.id.itemCategory)
        val itemCheckedBy: TextView = itemView.findViewById(R.id.itemCheckedBy)
        val itemCheckbox: CheckBox = itemView.findViewById(R.id.itemCheckbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shopping, parent, false)
        return ItemViewHolder(view)
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val item = items[position]

        holder.itemName.text = item.name
        holder.itemAmount.text = "Amount: ${item.amount}"
        holder.itemPrice.text = "Price: $${item.price}"
        holder.itemCategory.text = if (item.forUser == null) "Shared" else "For: ${item.forUser.username}"
        holder.itemCheckedBy.text = if (item.checkedBy != null) "Checked by: ${item.checkedBy!!.username}" else "Unchecked"
        holder.itemCheckbox.isChecked = item.checkedBy == currentUser

        // Handle checkbox interaction
        holder.itemCheckbox.setOnCheckedChangeListener { _, isChecked ->
            onItemChecked(item, isChecked)
        }

        // Handle click interaction
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }


    }

    override fun getItemCount(): Int = items.size
}
