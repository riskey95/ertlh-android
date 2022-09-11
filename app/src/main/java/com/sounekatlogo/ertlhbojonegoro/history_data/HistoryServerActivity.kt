package com.sounekatlogo.ertlhbojonegoro.history_data

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sounekatlogo.ertlhbojonegoro.databinding.ActivityHistoryServerBinding

class HistoryServerActivity : AppCompatActivity() {

    private var _binding : ActivityHistoryServerBinding? = null
    private val binding get() = _binding!!
    private var adapter: HistoryAdapter? = null
    private var role = ""
    private var uid = ""

    override fun onResume() {
        super.onResume()
        initRecyclerView()
        initViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHistoryServerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val prefs = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)

        uid = prefs.getString("uid", "").toString()
        role = prefs.getString("role", "").toString()

        if(role == "user") {
            binding.textView8.text = "Riwayat Survey di Server"
        } else {
            binding.textView8.text = "Riwayat Seluruh Survey di Server"
        }

        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initRecyclerView() {
        binding.rvSurvery.layoutManager = LinearLayoutManager(this)
        adapter = HistoryAdapter()
        binding.rvSurvery.adapter = adapter
    }

    private fun initViewModel() {
        val viewModel = ViewModelProvider(this)[HistoryViewModel::class.java]

        binding.progressBar.visibility = View.VISIBLE
        when (role) {
            "user" -> {
                viewModel.setHistoryByUid(uid)
            }
            "admin" -> {
                viewModel.setHistory()
            }
        }
        viewModel.getHistory().observe(this) { historySurvey ->
            if (historySurvey.size > 0) {
                adapter?.setData(historySurvey, "server")
                binding.noData.visibility = View.GONE
            } else {
                binding.noData.visibility = View.VISIBLE
            }
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}