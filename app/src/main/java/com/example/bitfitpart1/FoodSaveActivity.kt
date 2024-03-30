package com.example.bitfitpart1

import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.codepath.articlesearch.FoodEntity
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.example.bitfitpart1.databinding.RecordFoodBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Headers

class FoodSaveActivity : AppCompatActivity() {
    private lateinit var binding: RecordFoodBinding
    private lateinit var recordButton: Button
    private lateinit var food: FoodEntity
    private lateinit var foodList: MutableList<FoodEntity>
    var num=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RecordFoodBinding.inflate(layoutInflater)
        val view = binding.root
        recordButton = binding.recordButton
        foodList= mutableListOf()
        setContentView(view)
        setButtonListener()
        fetchAndDisplayDailyPhoto()
    }

    private fun setButtonListener() {

        recordButton.setOnClickListener {
            it.hideKeyboard()

            if(validateInput()){
                val foodName = binding.foodInput.getText().toString().trim()
                val calorieNumber = binding.caloriesInput.getText().toString().trim().toInt()

                food = FoodEntity(id = num, foodName, calorieNumber)
                lifecycleScope.launch(Dispatchers.IO) {
                    (application as FoodApplication).db.foodDao().insert(
                        FoodEntity(
                            num,
                            foodName,
                            calorieNumber
                        )
                    )
                    foodList.add(food)
                    num++
                }
                intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
            }

        }


    }
    private fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }
    private fun validateInput(): Boolean {
        val foodName = binding.foodInput.text.toString().trim()
        val calorieNumber = binding.caloriesInput.getText().toString().trim()


        when {
            foodName.isEmpty() -> {
                Toast.makeText(this, "Please fill in the food name", Toast.LENGTH_LONG).show()
                return false
            }

            !foodName.any { it.isLetter() } -> {
                Toast.makeText(
                    this,
                    "food name must contain at least one letter.",
                    Toast.LENGTH_LONG
                ).show()
                return false
            }

            calorieNumber.isEmpty() -> {
                Toast.makeText(this, "Please fill in the calorie number.", Toast.LENGTH_LONG).show()
                return false
            }

            calorieNumber.toIntOrNull() == null || calorieNumber.toInt() < 0 -> {
                Toast.makeText(
                    this,
                    "Calorie number must be greater than or equal to 0.",
                    Toast.LENGTH_LONG
                ).show()
                return false
            }


            else -> return true
        }

    }
    private fun fetchAndDisplayDailyPhoto() {
        val sharedPrefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val lastUpdated = sharedPrefs.getLong("lastUpdated", 0)

        // Create Calendar instances for now and for the last update time
        val now = Calendar.getInstance()
        val lastUpdateCalendar = Calendar.getInstance().apply {
            timeInMillis = lastUpdated
        }

        // Check if today is a different day from the last updated day
        val isNewDay = now.get(Calendar.YEAR) != lastUpdateCalendar.get(Calendar.YEAR) ||
                now.get(Calendar.DAY_OF_YEAR) != lastUpdateCalendar.get(Calendar.DAY_OF_YEAR)

        if (isNewDay) {
            // It's a new day, fetch new image
            val client = AsyncHttpClient()
            client["https://foodish-api.com/api/", object : JsonHttpResponseHandler() {
                override fun onFailure(
                    statusCode: Int,
                    headers: Headers?,
                    errorResponse: String,
                    throwable: Throwable?
                ) {
                    Log.d("Photo Fetch Failure","Cannot fetch photo")
                }

                override fun onSuccess(statusCode: Int, headers: Headers, response: JSON) {

                    val imageUrl = response.jsonObject.getString("image")

                    // Run the UI-related code on the main thread
                    runOnUiThread {
                        // Display the image using Glide or Picasso
                        Glide.with(this@FoodSaveActivity).load(imageUrl).into(binding.dailyPhotoImage)

                        // Save the new URL and timestamp
                        val currentTimestamp = System.currentTimeMillis()
                        sharedPrefs.edit().apply {
                            putLong("lastUpdated", currentTimestamp)
                            putString("dailyImageUrl", imageUrl)
                            apply()
                        }
                    }
                }

            }]
        } else {
            // Not a new day, load the existing image URL from SharedPreferences
            val imageUrl = sharedPrefs.getString("dailyImageUrl", "")
            Glide.with(this).load(imageUrl).into(binding.dailyPhotoImage)
        }
    }

   /* private fun fetchAndDisplayDailyPhoto() {
        val sharedPrefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val lastUpdated = sharedPrefs.getLong("lastUpdated", 0)
        val currentTimestamp = System.currentTimeMillis()

        if (currentTimestamp - lastUpdated >= 24 * 60 * 60 * 1000) {
            // It's been more than 24 hours, fetch new image
            val client = AsyncHttpClient()
            client["https://foodish-api.com/api/", object : JsonHttpResponseHandler() {
                override fun onFailure(
                    statusCode: Int,
                    headers: Headers?,
                    errorResponse: String,
                    throwable: Throwable?
                ) {
                    Log.d("Photo Fetch Failure","Cannot fetch photo")
                }

                override fun onSuccess(statusCode: Int, headers: Headers, response: JSON) {

                    val imageUrl = response.jsonObject.getString("image")

                    // Run the UI-related code on the main thread
                    runOnUiThread {
                        // Display the image using Glide or Picasso
                        Glide.with(this@FoodSaveActivity).load(imageUrl).into(binding.dailyPhotoImage)

                        // Save the new URL and timestamp
                        sharedPrefs.edit().apply {
                            putLong("lastUpdated", currentTimestamp)
                            putString("dailyImageUrl", imageUrl)
                            apply()
                        }
                    }
                }

            }]
        } else {
            // Load the existing image URL from SharedPreferences
            val imageUrl = sharedPrefs.getString("dailyImageUrl", "")
            Glide.with(this).load(imageUrl).into(binding.dailyPhotoImage)
        }
    }

    */
}