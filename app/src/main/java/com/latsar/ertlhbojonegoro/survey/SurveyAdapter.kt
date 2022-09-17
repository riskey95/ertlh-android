package com.latsar.ertlhbojonegoro.survey

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.latsar.ertlhbojonegoro.R
import com.latsar.ertlhbojonegoro.databinding.ItemSurveyBinding

class SurveyAdapter : RecyclerView.Adapter<SurveyAdapter.ViewHolder>() {

    private val surveyList = ArrayList<SurveyModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<SurveyModel>) {
        surveyList.clear()
        surveyList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemSurveyBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bind(model: SurveyModel) {
            with(binding) {


                name.text = model.nama1
                nik.text = "NIK: ${model.nik1}"
                status.text = model.status1
                when (model.status1) {
                    "Belum Diupload" -> {
                       status.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.white))
                        cv.backgroundTintList = (ContextCompat.getColorStateList(itemView.context, R.color.orange))
                    }
                    else -> {
                        status.setTextColor(ContextCompat.getColor(itemView.context, R.color.green))
                        cv.backgroundTintList = (ContextCompat.getColorStateList(itemView.context, R.color.green_muda))
                    }
                }

                cv.setOnClickListener {

                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSurveyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(surveyList[position])
    }

    override fun getItemCount(): Int = surveyList.size
}