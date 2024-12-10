package com.example.bevasarlolista

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bevasarlolista.adapter.ItemAdapter
import com.example.bevasarlolista.holder.UserLoggedIn
import com.example.bevasarlolista.model.Item
import com.example.bevasarlolista.model.User
import com.example.bevasarlolista.network.NetworkManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import retrofit2.await
import java.util.Date

class ListActivity : AppCompatActivity() {

    private lateinit var itemAdapter: ItemAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var currentUser: User
    private val items = mutableListOf<Item>()
    private val itemsToShow = mutableListOf<Item>()

    companion object {
        private const val REQUEST_ADD_ITEM = 1
        private const val REQUEST_EDIT_ITEM = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        /*
        val currentUser = intent.getSerializableExtra("currentUser") as? User
        if (currentUser == null) {
            Toast.makeText(this, "No user found, please log in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

         */

        currentUser = UserLoggedIn.getUser() ?: run {
            Toast.makeText(this, "No user found, please log in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        /*
        currentUser = intent.getSerializableExtra("currentUser") as? User ?: run {
            Toast.makeText(this, "No user found, please log in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        */




        recyclerView = findViewById(R.id.recyclerView)
        val fabAddItem: FloatingActionButton = findViewById(R.id.fabAddItem)

        // Sample data
        val user1 = User(1, "JohnDoe", "password123")
        items.add(Item(1, "Apple", 5.0, 1.5, Date(), currentUser, null))
        items.add(Item(2, "Banana", 3.0, 0.8, Date(), null, null))
        this.lifecycle.coroutineScope.launch {
            loadItems();
        }

        // RecyclerView setup
        itemAdapter = ItemAdapter(items, currentUser,
            onItemChecked = { item, isChecked ->
                item.checkedBy = if (isChecked) currentUser else null
            },
            onItemClick = { item ->
                val intent = Intent(this, AddEditItemActivity::class.java)
                intent.putExtra("item", item)
                intent.putExtra("currentUser", currentUser)
                startActivityForResult(intent, REQUEST_EDIT_ITEM)
            }

        )
        //itemsToShow.addAll(items.filter { it.purchaseDate.before() })
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = itemAdapter

        fabAddItem.setOnClickListener {
            val intent = Intent(this, AddEditItemActivity::class.java)
            intent.putExtra("currentUser", currentUser)
            startActivityForResult(intent, REQUEST_ADD_ITEM)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val updatedItem = data?.getSerializableExtra("item") as? Item
            when (requestCode) {
                REQUEST_ADD_ITEM -> {
                    updatedItem?.let {
                        items.add(it)
                        itemAdapter.notifyItemInserted(items.size - 1)
                    }
                }
                REQUEST_EDIT_ITEM -> {
                    updatedItem?.let { newItem ->
                        val index = items.indexOfFirst { it.id == newItem.id }
                        if (index != -1) {
                            items[index] = newItem
                            itemAdapter.notifyItemChanged(index)
                        }
                    }
                }
            }
        }
    }

    private suspend fun loadItems(){
        try {
            val items = NetworkManager.getItems().await();
            this.items.clear();
            this.items.addAll(items);
        } catch (ex: Exception){
            Toast.makeText(this, ex.message ?: "Failed to load items", Toast.LENGTH_SHORT).show()
        }
    }
}

