package com.example.bevasarlolista

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
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

class ListActivity : AppCompatActivity() {

    private lateinit var itemAdapter: ItemAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var currentUser: User
    private val items = mutableListOf<Item>()
    private val userMap = mutableMapOf<Int, String>() // Map to resolve user IDs to usernames

    companion object {
        private const val REQUEST_ADD_ITEM = 1
        private const val REQUEST_EDIT_ITEM = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        currentUser = UserLoggedIn.getUser() ?: run {
            Toast.makeText(this, "No user found, please log in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        recyclerView = findViewById(R.id.recyclerView)
        val fabAddItem: FloatingActionButton = findViewById(R.id.fabAddItem)

        // RecyclerView setup
        itemAdapter = ItemAdapter(items, currentUser, userMap,
            onItemChecked = { item, isChecked ->
                item.checkedById = if (isChecked) currentUser.id else null
            },
            onItemClick = { item ->
                val intent = Intent(this, AddEditItemActivity::class.java)
                intent.putExtra("item", item)
                intent.putExtra("currentUser", currentUser)
                startActivityForResult(intent, REQUEST_EDIT_ITEM)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = itemAdapter

        fabAddItem.setOnClickListener {
            val intent = Intent(this, AddEditItemActivity::class.java)
            intent.putExtra("currentUser", currentUser)
            startActivityForResult(intent, REQUEST_ADD_ITEM)
        }

        // Load items and users sequentially
        this.lifecycle.coroutineScope.launch {
            loadUsers() // Ensure userMap is populated
            loadItems() // Now load items that depend on userMap
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

    private suspend fun loadItems() {
        try {
            val response = NetworkManager.getItems().await()
            Log.d("ListActivity", "Items loaded: $response")
            items.clear()
            items.addAll(response)
            itemAdapter.notifyDataSetChanged()
        } catch (ex: Exception) {
            Toast.makeText(this, ex.message ?: "Failed to load items", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun loadUsers() {
        try {
            val users = NetworkManager.getUsers().await()
            userMap.clear()
            userMap.putAll(users.associateBy({ it.id }, { it.username })) // Map user IDs to usernames
        } catch (ex: Exception) {
            Toast.makeText(this, ex.message ?: "Failed to load users", Toast.LENGTH_SHORT).show()
        }
    }
}
