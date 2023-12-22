package com.bangkitacademy.nutrace.ui.planner

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bangkitacademy.nutrace.R
import com.bangkitacademy.nutrace.data.model.Food
import com.bangkitacademy.nutrace.data.model.dummyFoodList
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PlannerFragment : Fragment() {

    private lateinit var textDate: TextView

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_planner, container, false)

            textDate = view.findViewById(R.id.textDate)

            val calendarBox = Calendar.getInstance()
            updateText(calendarBox)

            val dateBox = DatePickerDialog.OnDateSetListener { _, year, month, day ->
                calendarBox.set(Calendar.YEAR, year)
                calendarBox.set(Calendar.MONTH, month)
                calendarBox.set(Calendar.DAY_OF_MONTH, day)
                updateText(calendarBox)
            }

            textDate.setOnClickListener {
                DatePickerDialog(
                    requireContext(),
                    dateBox,
                    calendarBox.get(Calendar.YEAR),
                    calendarBox.get(Calendar.MONTH),
                    calendarBox.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            setFoodData(view, R.id.recipeImageView, R.id.titleTextView, R.id.descriptionTextView, R.id.jumlahCalori, dummyFoodList[0])
            setFoodData(view, R.id.recipeImageView2, R.id.titleTextView2, R.id.descriptionTextView2, R.id.jumlahCalori2, dummyFoodList[1])
            setFoodData(view, R.id.recipeImageView3, R.id.titleTextView3, R.id.descriptionTextView3, R.id.jumlahCalori3, dummyFoodList[2])

            return view
        }

        private fun setFoodData(view: View, imageViewId: Int, titleTextViewId: Int, descriptionTextViewId: Int, jumlahCaloriId: Int, food: Food) {
            val imageView = view.findViewById<ImageView>(imageViewId)
            Glide.with(this).load(food.images).into(imageView)
            view.findViewById<TextView>(titleTextViewId).text = food.foodName
            view.findViewById<TextView>(descriptionTextViewId).text = "Kalori"
            view.findViewById<TextView>(jumlahCaloriId).text = "${food.calories}"
        }

    private fun updateText(calendar: Calendar) {
        val dateFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(dateFormat, Locale.UK)
        textDate.text = sdf.format(calendar.time)
    }
}
