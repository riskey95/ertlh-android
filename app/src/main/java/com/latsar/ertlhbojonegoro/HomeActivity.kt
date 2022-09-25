package com.latsar.ertlhbojonegoro

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.latsar.ertlhbojonegoro.databinding.ActivityHomeBinding
import com.latsar.ertlhbojonegoro.history_data.HistoryActivity
import com.latsar.ertlhbojonegoro.history_data.HistoryServerActivity
import com.latsar.ertlhbojonegoro.register.RegisterUserActivity
import com.latsar.ertlhbojonegoro.survey.SurveyActivity
import com.latsar.ertlhbojonegoro.survey.SurveyAdapter
import com.latsar.ertlhbojonegoro.survey.SurveyModel
import com.latsar.ertlhbojonegoro.utils.DBHelper


class HomeActivity : AppCompatActivity() {
    private var _binding : ActivityHomeBinding? = null
    private val binding get() = _binding!!
    private var adapter: SurveyAdapter? = null
    private var uid = ""
    private var role = ""
    private var surveyList = ArrayList<SurveyModel>()

    override fun onResume() {
        super.onResume()
        checkRole()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.apply {
            otherData.setOnClickListener {
                startActivity(Intent(this@HomeActivity, RegisterUserActivity::class.java))
            }
            logout.setOnClickListener {
                showLogoutDialog()
            }
            addData.setOnClickListener {
                startActivity(Intent(this@HomeActivity, SurveyActivity::class.java))
            }
            historyData.setOnClickListener {
                val intent = Intent(this@HomeActivity, HistoryActivity::class.java)
//                intent.putExtra(HistoryActivity.UID, uid)
                startActivity(intent)
            }
            statusData.setOnClickListener {
                Log.e("Cek", "OK")
                startActivity(Intent(this@HomeActivity, HistoryServerActivity::class.java))
            }
            syncData.setOnClickListener {
                if(binding.textView4.text.toString().toInt() > 0) {
                    confirmSyncData()
                } else {
                    Toast.makeText(this@HomeActivity, "Tidak ada data yang perlu diupload", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun confirmSyncData() {
        if(isOnline(this)) {

            val mProgressDialog = ProgressDialog(this)
            mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...")
            mProgressDialog.setCanceledOnTouchOutside(false)
            mProgressDialog.show()

            val finalSurveyList = ArrayList<SurveyModel>()
            surveyList.forEach {
                if(it.status1 == "Belum Diupload") {
                    finalSurveyList.add(it)
                }
            }


            for(i in 0 until finalSurveyList.size) {
                Log.e("dasa", i.toString())
                if (finalSurveyList[i].status1 == "Belum Diupload") {
                    var ktp = ""
                    var samping = ""
                    var dalam = ""

                    val options = arrayOf("tampak_depan_rumah", "tampak_samping_rumah", "tampak_dalam_rumah")
                    val images = arrayOf(finalSurveyList[i].ktp1, finalSurveyList[i].samping1, finalSurveyList[i].dalamRumah1)
                    for (j in 0 until 3) {
                        val imageFileName =
                            "${options[j]}/image_"+ uid +"_"+ System.currentTimeMillis() + ".png"
                        /// proses upload gambar ke databsae
                        val mStorageRef = FirebaseStorage.getInstance().reference
                        mStorageRef.child(imageFileName).putFile(Uri.parse(images[j]))
                            .addOnSuccessListener { _ ->
                                mStorageRef.child(imageFileName).downloadUrl
                                    .addOnSuccessListener { uri: Uri ->
                                        when (j) {
                                            0 -> {
                                                ktp = uri.toString()
                                            }
                                            1 -> {
                                                samping = uri.toString()
                                            }
                                            else -> {
                                                dalam = uri.toString()

                                                val data = mapOf(
                                                    "id" to finalSurveyList[i].id1,
                                                    "uid" to finalSurveyList[i].uid1,
                                                    "serverUid" to finalSurveyList[i].serverUid1,
                                                    "nama" to finalSurveyList[i].nama1,
                                                    "nik" to finalSurveyList[i].nik1,
                                                    "noKK" to finalSurveyList[i].noKK1,
                                                    "alamat" to finalSurveyList[i].alamat1,
                                                    "desa" to finalSurveyList[i].desa1,
                                                    "kecamatan" to finalSurveyList[i].kecamatan1,
                                                    "jumlahKK" to finalSurveyList[i].jumlahKK1,
                                                    "jumlahPenghuni" to finalSurveyList[i].jumlahPenghuni1,
                                                    "penghasilanKK" to finalSurveyList[i].penghasilan1,
                                                    "luasRumah" to finalSurveyList[i].luasRumah1,
                                                    "struktur_fondasi" to finalSurveyList[i].pondasi1,
                                                    "struktur_sloof" to finalSurveyList[i].sloof1,
                                                    "struktur_kolom" to finalSurveyList[i].kolom1,
                                                    "struktur_ringBalok" to finalSurveyList[i].ringBalok1,
                                                    "struktur_kudaKuda" to finalSurveyList[i].kudaKuda1,
                                                    "nonstruktur_dinding" to finalSurveyList[i].dinding1,
                                                    "nonstruktur_lantai" to finalSurveyList[i].lantai1,
                                                    "nonstruktur_penutupAtap" to finalSurveyList[i].penutupAtap1,
                                                    "statusPenguasaanLahan" to finalSurveyList[i].statusPenguasaanLahan1,
                                                    "longitude" to finalSurveyList[i].longitude1,
                                                    "latitude" to finalSurveyList[i].latitude1,
                                                    "foto_depan" to ktp,
                                                    "foto_samping" to samping,
                                                    "foto_dalamRumah" to dalam,
                                                    "status" to "Sudah Diupload",
                                                    "nilai" to finalSurveyList[i].nilai1,
                                                    "date" to finalSurveyList[i].date1
                                                )

                                                FirebaseFirestore
                                                    .getInstance()
                                                    .collection("survey")
                                                    .document(finalSurveyList[i].serverUid1)
                                                    .set(data)
                                                    .addOnCompleteListener { response ->
                                                        if (response.isSuccessful) {

                                                            val db =
                                                                DBHelper(this@HomeActivity, null)

                                                            finalSurveyList.forEach { surveyData ->
                                                                db.editStatusSurvey(surveyData.id1)
                                                            }

//                                                            Log.e("ddasa",i.toString() + " " + finalSurveyList.size.toString())

                                                            if (i == finalSurveyList.size-1) {
//                                                                Log.e("ddasa","sada")
                                                                showSuccessDialog()
                                                                mProgressDialog.dismiss()
                                                            }

                                                        }
                                                    }

                                            }
                                        }
                                    }
                            }
                    }
                }
            }


        } else {
            Toast.makeText(this, "Pastikan anda menyalakan internet", Toast.LENGTH_SHORT).show()
        }
    }


    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Berhasil Sinkonisasi Data")
            .setMessage("Anda berhasil mengupload data survey ke server, selanjutnya admin dapat memeriksa data anda")
            .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
                val intent = Intent(this, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            .show()
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
    }


    @SuppressLint("Range")
    private fun getData() {
        surveyList.clear()
        val db = DBHelper(this, null)
        val cursor = db.getAllSurvey(uid)

        if(cursor != null) {
            if(cursor.moveToFirst()) {
                while(!cursor.isAfterLast) {
                    val id = cursor.getString(cursor.getColumnIndex(DBHelper.ID_COL))
                    val uid = cursor.getString(cursor.getColumnIndex(DBHelper.uid))
                    val nama = cursor.getString(cursor.getColumnIndex(DBHelper.nama))
                    val nik = cursor.getString(cursor.getColumnIndex(DBHelper.nik))
                    val nokk = cursor.getString(cursor.getColumnIndex(DBHelper.noKK))
                    val alamat = cursor.getString(cursor.getColumnIndex(DBHelper.alamat))
                    val desa = cursor.getString(cursor.getColumnIndex(DBHelper.desa))
                    val kecamatan = cursor.getString(cursor.getColumnIndex(DBHelper.kecamatan))
                    val jumlahkk = cursor.getString(cursor.getColumnIndex(DBHelper.jumlahKK))
                    val jumlahPenghuni =
                        cursor.getString(cursor.getColumnIndex(DBHelper.jumlahPenghuni))
                    val penghasilan =
                        cursor.getString(cursor.getColumnIndex(DBHelper.penghasilanKK))
                    val luas = cursor.getString(cursor.getColumnIndex(DBHelper.luasRumah))
                    val pondasi = cursor.getString(cursor.getColumnIndex(DBHelper.fondasi))
                    val sloof = cursor.getString(cursor.getColumnIndex(DBHelper.sloof))
                    val kolom = cursor.getString(cursor.getColumnIndex(DBHelper.kolom))
                    val ring = cursor.getString(cursor.getColumnIndex(DBHelper.ringBalok))
                    val kuda = cursor.getString(cursor.getColumnIndex(DBHelper.kudaKuda))
                    val dinding = cursor.getString(cursor.getColumnIndex(DBHelper.dinding))
                    val lantai = cursor.getString(cursor.getColumnIndex(DBHelper.lantai))
                    val penutup = cursor.getString(cursor.getColumnIndex(DBHelper.penutupAtap))
                    val statusPenguasaanLahan =
                        cursor.getString(cursor.getColumnIndex(DBHelper.statusPenguasaanLahan))
                    val longitude = cursor.getString(cursor.getColumnIndex(DBHelper.longitude))
                    val latitude = cursor.getString(cursor.getColumnIndex(DBHelper.latitude))
                    val ktp = cursor.getString(cursor.getColumnIndex(DBHelper.ktp))
                    val samping = cursor.getString(cursor.getColumnIndex(DBHelper.samping))
                    val dalam = cursor.getString(cursor.getColumnIndex(DBHelper.dalamRumah))
                    val status = cursor.getString(cursor.getColumnIndex(DBHelper.status))
                    val nilai = cursor.getString(cursor.getColumnIndex(DBHelper.nilai))
                    val date = cursor.getString(cursor.getColumnIndex(DBHelper.date))
                    val serverUid = cursor.getString(cursor.getColumnIndex(DBHelper.serverUid))

                    surveyList.add(
                        SurveyModel(
                            id1 = id.toInt(),
                            uid1 = uid,
                            nama1 = nama,
                            nik1 = nik,
                            noKK1 = nokk,
                            alamat1 = alamat,
                            desa1 = desa,
                            kecamatan1 = kecamatan,
                            jumlahKK1 = jumlahkk,
                            jumlahPenghuni1 = jumlahPenghuni,
                            penghasilan1 = penghasilan,
                            luasRumah1 = luas,
                            pondasi1 = pondasi,
                            sloof1 = sloof,
                            kolom1 = kolom,
                            ringBalok1 = ring,
                            kudaKuda1 = kuda,
                            dinding1 = dinding,
                            lantai1 = lantai,
                            penutupAtap1 = penutup,
                            statusPenguasaanLahan1 = statusPenguasaanLahan,
                            longitude1 = longitude,
                            latitude1 = latitude,
                            ktp1 = ktp,
                            samping1 = samping,
                            dalamRumah1 = dalam,
                            status1 = status,
                            nilai1 = nilai,
                            serverUid1 = serverUid,
                            date1 = date,
                        )
                    )
                    cursor.moveToNext()
                }


//                while (cursor.moveToNext()) {
//                    val id = cursor.getString(cursor.getColumnIndex(DBHelper.ID_COL))
//                    val uid = cursor.getString(cursor.getColumnIndex(DBHelper.uid))
//                    val nama = cursor.getString(cursor.getColumnIndex(DBHelper.nama))
//                    val nik = cursor.getString(cursor.getColumnIndex(DBHelper.nik))
//                    val nokk = cursor.getString(cursor.getColumnIndex(DBHelper.noKK))
//                    val alamat = cursor.getString(cursor.getColumnIndex(DBHelper.alamat))
//                    val desa = cursor.getString(cursor.getColumnIndex(DBHelper.desa))
//                    val kecamatan = cursor.getString(cursor.getColumnIndex(DBHelper.kecamatan))
//                    val jumlahkk = cursor.getString(cursor.getColumnIndex(DBHelper.jumlahKK))
//                    val jumlahPenghuni = cursor.getString(cursor.getColumnIndex(DBHelper.jumlahPenghuni))
//                    val penghasilan = cursor.getString(cursor.getColumnIndex(DBHelper.penghasilanKK))
//                    val luas = cursor.getString(cursor.getColumnIndex(DBHelper.luasRumah))
//                    val pondasi = cursor.getString(cursor.getColumnIndex(DBHelper.fondasi))
//                    val sloof = cursor.getString(cursor.getColumnIndex(DBHelper.sloof))
//                    val kolom = cursor.getString(cursor.getColumnIndex(DBHelper.kolom))
//                    val ring = cursor.getString(cursor.getColumnIndex(DBHelper.ringBalok))
//                    val kuda = cursor.getString(cursor.getColumnIndex(DBHelper.kudaKuda))
//                    val dinding = cursor.getString(cursor.getColumnIndex(DBHelper.dinding))
//                    val lantai = cursor.getString(cursor.getColumnIndex(DBHelper.lantai))
//                    val penutup = cursor.getString(cursor.getColumnIndex(DBHelper.penutupAtap))
//                    val statusPenguasaanLahan = cursor.getString(cursor.getColumnIndex(DBHelper.statusPenguasaanLahan))
//                    val longitude = cursor.getString(cursor.getColumnIndex(DBHelper.longitude))
//                    val latitude = cursor.getString(cursor.getColumnIndex(DBHelper.latitude))
//                    val ktp = cursor.getString(cursor.getColumnIndex(DBHelper.ktp))
//                    val samping = cursor.getString(cursor.getColumnIndex(DBHelper.samping))
//                    val dalam = cursor.getString(cursor.getColumnIndex(DBHelper.dalamRumah))
//                    val status = cursor.getString(cursor.getColumnIndex(DBHelper.status))
//                    val nilai = cursor.getString(cursor.getColumnIndex(DBHelper.nilai))
//                    val date = cursor.getString(cursor.getColumnIndex(DBHelper.date))
//                    val serverUid = cursor.getString(cursor.getColumnIndex(DBHelper.serverUid))
//
//                    surveyList.add(
//                        SurveyModel(
//                            id1 = id.toInt(),
//                            uid1 = uid,
//                            nama1 = nama,
//                            nik1 = nik,
//                            noKK1 = nokk,
//                            alamat1 = alamat,
//                            desa1 = desa,
//                            kecamatan1 = kecamatan,
//                            jumlahKK1 = jumlahkk,
//                            jumlahPenghuni1 = jumlahPenghuni,
//                            penghasilan1 = penghasilan,
//                            luasRumah1 = luas,
//                            pondasi1 = pondasi,
//                            sloof1 = sloof,
//                            kolom1 = kolom,
//                            ringBalok1 = ring,
//                            kudaKuda1 = kuda,
//                            dinding1 = dinding,
//                            lantai1 = lantai,
//                            penutupAtap1 = penutup,
//                            statusPenguasaanLahan1 = statusPenguasaanLahan,
//                            longitude1 = longitude,
//                            latitude1 = latitude,
//                            ktp1 = ktp,
//                            samping1 = samping,
//                            dalamRumah1 = dalam,
//                            status1 = status,
//                            nilai1 = nilai,
//                            serverUid1 = serverUid,
//                            date1 = date,
//                        )
//                    )
//                }


                adapter?.setData(surveyList)
                var belumDiupload = 0
                var sudahDiupload = 0
                surveyList.forEach {
                    if(it.status1 == "Belum Diupload") {
                        belumDiupload++
                    } else {
                        sudahDiupload++
                    }
                }
                binding.textView4.text = belumDiupload.toString()
                binding.textView3.text = sudahDiupload.toString()
                cursor.close()
            } else {
                if(surveyList.size == 0) {
                    binding.noData.visibility = View.VISIBLE
                } else {
                    binding.noData.visibility = View.GONE
                }
            }
        }
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        binding.rvSurvery.layoutManager = layoutManager
        adapter = SurveyAdapter()
        binding.rvSurvery.adapter = adapter
        adapter?.setData(surveyList)
    }

    private fun checkRole() {
        val prefs = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        uid = prefs.getString("uid", "").toString()
        role = prefs.getString("role", "user").toString()
        if(role == "admin") {
            binding.registerUserLayout.visibility = View.VISIBLE
            binding.addDataLayout.visibility = View.GONE
            binding.historyDataLayout.visibility = View.GONE
            binding.syncDataLayout.visibility = View.GONE
        } else if (role == "verifikator") {
            binding.addDataLayout.visibility = View.GONE
            binding.historyDataLayout.visibility = View.GONE
            binding.syncDataLayout.visibility = View.GONE
        }
        try {
            getData()
            initRecyclerView()
        } catch (e :Exception) {
            e.printStackTrace()
        }

    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setMessage("Apakah anda ingin logout dari akun ini ?")
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setPositiveButton("YA") { dialogInterface, _ ->
                dialogInterface.dismiss()
                val prefs = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
                prefs.edit().clear().apply()
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("TIDAK", null)
            .show()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}