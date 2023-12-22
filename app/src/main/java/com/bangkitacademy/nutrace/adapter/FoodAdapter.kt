package com.bangkitacademy.nutrace.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bangkitacademy.nutrace.data.model.Food
import com.bangkitacademy.nutrace.data.model.dummyFoodList
import com.bangkitacademy.nutrace.databinding.ItemResepBinding
import com.bangkitacademy.nutrace.util.FoodDiffutils
import com.bumptech.glide.Glide

/*class FoodAdapter: RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    private var list = emptyList<Food>()
    private var onItemClickCallback: OnItemClickCallback? = null

    fun updateList(newList: List<Food>) {
        val foodDiff = FoodDiffutils(list, newList)
        val result = DiffUtil.calculateDiff(foodDiff)
        list = newList
        result.dispatchUpdatesTo(this)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class FoodViewHolder(private val binding: ItemResepBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(food: Food) {
            binding.apply {
                root.setOnClickListener { onItemClickCallback?.onItemClicked(food) }
                titleTextView.text = food.foodName
                descriptionTextView.text = food.calories.toString()
                Glide.with(itemView)
                    .load(food.images)
                    //.centerCrop()
                    .into(recipeImageView)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemResepBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    interface OnItemClickCallback {
        fun onItemClicked(data: Food)
    }

}*/

class FoodAdapter : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    private var list = emptyList<Food>()
    private var onItemClickCallback: OnItemClickCallback? = null

    fun updateList(newList: List<Food>) {
        val foodDiff = FoodDiffutils(list, newList)
        val result = DiffUtil.calculateDiff(foodDiff)
        list = newList
        result.dispatchUpdatesTo(this)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class FoodViewHolder(private val binding: ItemResepBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(food: Food) {
            binding.apply {
                root.setOnClickListener { onItemClickCallback?.onItemClicked(food) }
                titleTextView.text = food.foodName
                jumlahCalori.text = food.calories.toString()
                Glide.with(itemView)
                    .load(food.images)
                    .into(recipeImageView)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemResepBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val dummyFood = getDummyFood(position)
        holder.bind(dummyFood)
    }

    override fun getItemCount(): Int = list.size

    private fun getDummyFood(position: Int): Food {
        return if (position in 0 until dummyFoodList.size) {
            dummyFoodList[position]
        } else {
            Food(
                calories = 0.0,
                carbohydrateContent = 0.0,
                cookTime = "",
                fatContent = 0.0,
                images = "",
                prepTime = "",
                proteinContent = 0.0,
                recipeIngredientParts = emptyList(),
                recipeInstructions = emptyList(),
                recipeServings = 0,
                totalTime = "",
                foodName = ""
            )
        }
    }


    interface OnItemClickCallback {
        fun onItemClicked(data: Food)
    }
}
