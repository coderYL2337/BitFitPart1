package com.example.bitfitpart1
import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable


data class Food(
    @SerialName("foodName")
    val foodName: String="",
    @SerialName("calorieNumber")
    val calorieNumber:Int=0
)

