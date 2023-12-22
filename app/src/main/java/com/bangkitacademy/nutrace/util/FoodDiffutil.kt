package com.bangkitacademy.nutrace.util

import androidx.recyclerview.widget.DiffUtil
import com.bangkitacademy.nutrace.data.model.Food

class FoodDiffutils(
    private val oldList: List<Food>,
    private val newList: List<Food>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].foodName == newList[newItemPosition].foodName

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem == newItem
    }
}
