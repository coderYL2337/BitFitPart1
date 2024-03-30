package com.example.bitfitpart1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.codepath.articlesearch.FoodEntity

//const val ARTICLE_EXTRA = "FOOD_EXTRA"
//private const val TAG = "FoodAdapter"

class FoodAdapter(private val context: Context, private val foodList: MutableList<FoodEntity>,private val onItemLongClick: (FoodEntity) -> Unit ):RecyclerView.Adapter<FoodAdapter.ViewHolder>() {

   inner class ViewHolder(itemView: View,val onItemLongClick: (FoodEntity) -> Unit) : RecyclerView.ViewHolder(itemView) {
        /*init {
            itemView.setOnLongClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemLongClick(foodList[position]) // Call the lambda function
                }
                true // Indicate that the long click was handled
            }
        }*/

       // Inside FoodAdapter ViewHolder's init block
       init {
           itemView.setOnLongClickListener {
               val position = absoluteAdapterPosition
               if (position != RecyclerView.NO_POSITION) {
                   val foodEntity = foodList[position]
                   (context as MainActivity).showDeleteConfirmationDialog(foodEntity) // Assume context is MainActivity
               }
               true
           }
       }


       private var foodName=itemView.findViewById<TextView>(R.id.foodName)
       private var calorieNumber=itemView.findViewById<TextView>(R.id.calorieNumber)


        fun bind(food:FoodEntity){
            foodName.text=food.foodName
            calorieNumber.text = food.calorieNumber.toString()
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.food_item, parent, false)
        return ViewHolder(view,onItemLongClick)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val food = foodList[position]
        holder.bind(food)
        holder.itemView.setOnClickListener {
            onClick(holder, position)
        }

    }


    private fun onClick(holder: FoodAdapter.ViewHolder, position: Int) {
        val food = foodList[position]
        Toast.makeText(context,"ID: ${food.id}   ${food.foodName}  ${food.calorieNumber} calories", Toast.LENGTH_SHORT).show()

    }

    override fun getItemCount() = foodList.size
}