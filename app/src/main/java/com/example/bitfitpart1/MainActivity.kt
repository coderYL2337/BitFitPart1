package com.example.bitfitpart1

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.articlesearch.FoodEntity
import com.example.bitfitpart1.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var foodAdapter: FoodAdapter
    private lateinit var foodList: MutableList<FoodEntity>
    private lateinit var rvFoodList: RecyclerView
    private lateinit var food:FoodEntity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        // setContentView(R.layout.activity_main)
        val view = binding.root
        foodList = mutableListOf()
        /*foodAdapter = FoodAdapter(this@MainActivity, foodList){ foodEntity ->
            deleteFoodItem(foodEntity)}*/
        foodAdapter = FoodAdapter(this@MainActivity, foodList){ foodEntity ->
            showDeleteConfirmationDialog(foodEntity)}
        setContentView(view)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "BitFit"
        loadFoodItems()
        setaddButtonListener()
    }


    private fun loadFoodItems() {

        rvFoodList = binding.recordList
        rvFoodList.adapter = foodAdapter
        rvFoodList.layoutManager = LinearLayoutManager(this).also {
            val dividerItemDecoration = DividerItemDecoration(this, it.orientation)
            rvFoodList.addItemDecoration(dividerItemDecoration)
        }
        lifecycleScope.launch {
            (application as FoodApplication).db.foodDao().getAll().collect { databaseList ->
                databaseList.map { entity ->
                    FoodEntity(
                        entity.id,
                        entity.foodName,
                        entity.calorieNumber,
                    )
                }.also { mappedList ->
                    foodList.clear()
                    foodList.addAll(mappedList)
                    foodAdapter.notifyDataSetChanged()
                }
            }
        }

    }

    fun showDeleteConfirmationDialog(foodEntity: FoodEntity) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Delete ${foodEntity.foodName}?")
            // If the dialog is cancelable
            .setCancelable(false)
            // Positive button text and action
            .setPositiveButton("Delete") { dialog, id ->
                deleteFoodItem(foodEntity)
            }
            // Negative button text and action
            .setNegativeButton("Cancel") { dialog, id ->
                dialog.cancel()
            }

        // Create the AlertDialog object and return it
        val alert = dialogBuilder.create()
        alert.setTitle("Confirm Deletion")
        alert.show()
    }

    private fun deleteFoodItem(foodEntity: FoodEntity) {
        // Delete the food item from the database
        lifecycleScope.launch {
            (application as FoodApplication).db.foodDao().delete(foodEntity)
            foodList.remove(foodEntity) // Remove the item from the list
            foodAdapter.notifyDataSetChanged() // Notify the adapter of the change

            // Show a toast message
            Toast.makeText(this@MainActivity, "Deleted: ${foodEntity.foodName}", Toast.LENGTH_SHORT).show()
        }
    }



    private fun setaddButtonListener() {
        binding.addButton.setOnClickListener {
           intent = Intent(this,FoodSaveActivity::class.java)
            startActivity(intent)
        }
    }

}