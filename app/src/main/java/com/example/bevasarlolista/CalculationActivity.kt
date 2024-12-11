package com.example.bevasarlolista

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bevasarlolista.adapter.CalculationAdapter
import com.example.bevasarlolista.databinding.ActivityCalculationBinding
import com.example.bevasarlolista.model.User
import com.example.bevasarlolista.network.NetworkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.await

class CalculationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCalculationBinding
    private lateinit var currentUser: User
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val calculations = mutableListOf<Pair<String, Double>>()
    private lateinit var calculationAdapter: CalculationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalculationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentUser = intent.getSerializableExtra("currentUser") as User

        // Set up RecyclerView
        calculationAdapter = CalculationAdapter(calculations)
        binding.recyclerViewCalculations.apply {
            layoutManager = LinearLayoutManager(this@CalculationActivity)
            adapter = calculationAdapter
        }

        // Load calculations
        binding.btnLoadCalculations.setOnClickListener {
            val year = binding.etYear.text.toString().toIntOrNull()
            if (year == null || year < 1) {
                Toast.makeText(this, "Invalid year.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            loadCalculations(year)
        }
    }

    private fun loadCalculations(year: Int) {
        ioScope.launch {
            try {
                calculations.clear()
                for (month in 1..12) {
                    try {
                        val result = NetworkManager.calculateMonthlyExpense(year, month, currentUser.id).await()
                        calculations.add(Pair("Month $month", result))
                    } catch (ex: Exception) {
                        calculations.add(Pair("Month $month", 0.0)) // Fallback value if an error occurs
                    }
                }
                withContext(Dispatchers.Main) {
                    calculationAdapter.notifyDataSetChanged()
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CalculationActivity, "Error: ${ex.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
