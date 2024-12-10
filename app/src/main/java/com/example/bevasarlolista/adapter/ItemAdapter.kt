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
    private val userMap: Map<Int, String>,
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
        holder.itemPrice.text = "Price: ${item.price} Ft"
        holder.itemCategory.text = if (item.forUserId == null) {
            "Shared"
        } else {
            "For: ${userMap[item.forUserId] ?: "Unknown"}"
        }

        // Resolve `checkedBy` to username or mark as "Unchecked"
        holder.itemCheckedBy.text = if (item.checkedById != null) {
            "Bought: ${userMap[item.checkedById] ?: "Unknown"}"
        } else {
            "Unchecked"
        }
        holder.itemCheckbox.isChecked = item.checkedById == currentUser.id

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
