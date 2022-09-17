package com.sounekatlogo.ertlhbojonegoro.history_data

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sounekatlogo.ertlhbojonegoro.R
import com.sounekatlogo.ertlhbojonegoro.databinding.ItemSurvey2Binding
import com.sounekatlogo.ertlhbojonegoro.databinding.ItemSurveyBinding
import com.sounekatlogo.ertlhbojonegoro.survey.SurveyModel

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private val surveyList = ArrayList<SurveyModel>()
    private var option = ""
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<SurveyModel>, option: String) {
        surveyList.clear()
        surveyList.addAll(items)
        this.option = option
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemSurvey2Binding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bind(model: SurveyModel) {
            with(binding) {


                nik.text = "NIK: ${model.nik1}"
                status.text = model.status1
                textView9.text = model.nama1
                address.text = "${model.alamat1}, ${model.desa1}, ${model.kecamatan1}"
                date.text = model.date1
                Glide.with(itemView.context)
                    .load(R.drawable.home)
                    .into(home)


                when (model.status1) {
                    "Belum Diupload" -> {
                        status.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.holo_red_dark))
                    }
                    "Ditolak" -> {
                        status.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.holo_red_dark))
                        nik.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.pig_pink))
                    }
                    else -> {
                        status.setTextColor(ContextCompat.getColor(itemView.context, R.color.green))
                    }
                }

                cv.setOnClickListener {
                    val intent = Intent(itemView.context, HistoryDetailActivity::class.java)
                    intent.putExtra(HistoryDetailActivity.EXTRA_DATA, model)
                    intent.putExtra(HistoryDetailActivity.OPTION, option)
                    itemView.context.startActivity(intent)
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSurvey2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(surveyList[position])
    }

    override fun getItemCount(): Int = surveyList.size
}