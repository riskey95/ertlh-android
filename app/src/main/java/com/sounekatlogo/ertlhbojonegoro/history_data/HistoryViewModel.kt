package com.sounekatlogo.ertlhbojonegoro.history_data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sounekatlogo.ertlhbojonegoro.survey.SurveyModel

class HistoryViewModel : ViewModel() {

    private val historyList = MutableLiveData<ArrayList<SurveyModel>>()
    private val listData = ArrayList<SurveyModel>()
    private val TAG = HistoryViewModel::class.java.simpleName


    fun setHistory() {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("survey")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = SurveyModel(
                            alamat1 = document.data["alamat"].toString(),
                            dalamRumah1 = document.data["foto_dalamRumah"].toString(),
                            date1 = document.data["date"].toString(),
                            desa1 = document.data["desa"].toString(),
                            dinding1 = document.data["nonstruktur_dinding"].toString(),
                            pondasi1 = document.data["struktur_fondasi"].toString(),
                            id1 = document.data["id"].toString().toInt(),
                            jumlahKK1 = document.data["jumlahKK"].toString(),
                            jumlahPenghuni1 = document.data["jumlahPenghuni"].toString(),
                            kecamatan1 = document.data["kecamatan"].toString(),
                            kolom1 = document.data["struktur_kolom"].toString(),
                            longitude1 = document.data["longitude"].toString(),
                            latitude1 = document.data["latitude"].toString(),
                            ktp1 = document.data["foto_ktp"].toString(),
                            kudaKuda1 = document.data["struktur_kudaKuda"].toString(),
                            lantai1 = document.data["nonstruktur_lantai"].toString(),
                            luasRumah1 = document.data["luasRumah"].toString(),
                            nama1 = document.data["nama"].toString(),
                            nik1 = document.data["nik"].toString(),
                            noKK1 = document.data["noKK"].toString(),
                            penghasilan1 = document.data["penghasilanKK"].toString(),
                            penutupAtap1 = document.data["nonstruktur_penutupAtap"].toString(),
                            ringBalok1 = document.data["struktur_ringBalok"].toString(),
                            samping1 = document.data["foto_samping"].toString(),
                            sloof1 = document.data["struktur_sloof"].toString(),
                            serverUid1 = document.data["serverUid"].toString(),
                            status1 = document.data["status"].toString(),
                            statusPenguasaanLahan1 = document.data["statusPenguasaanLahan"].toString(),
                            nilai1 = document.data["nilai"].toString(),
                            uid1 = document.data["uid"].toString()
                        )

                        listData.add(model)
                    }
                    historyList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }


    fun setHistoryByUid(uid: String) {
        listData.clear()

        try {
            FirebaseFirestore.getInstance().collection("survey")
                .whereEqualTo("uid", uid)
                .whereIn("status", listOf("Disetujui", "Ditolak"))
                .orderBy("id",Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = SurveyModel(
                            alamat1 = document.data["alamat"].toString(),
                            dalamRumah1 = document.data["foto_dalamRumah"].toString(),
                            date1 = document.data["date"].toString(),
                            desa1 = document.data["desa"].toString(),
                            dinding1 = document.data["nonstruktur_dinding"].toString(),
                            pondasi1 = document.data["struktur_fondasi"].toString(),
                            id1 = document.data["id"].toString().toInt(),
                            jumlahKK1 = document.data["jumlahKK"].toString(),
                            jumlahPenghuni1 = document.data["jumlahPenghuni"].toString(),
                            kecamatan1 = document.data["kecamatan"].toString(),
                            kolom1 = document.data["struktur_kolom"].toString(),
                            longitude1 = document.data["longitude"].toString(),
                            latitude1 = document.data["latitude"].toString(),
                            ktp1 = document.data["foto_ktp"].toString(),
                            kudaKuda1 = document.data["struktur_kudaKuda"].toString(),
                            lantai1 = document.data["nonstruktur_lantai"].toString(),
                            luasRumah1 = document.data["luasRumah"].toString(),
                            nama1 = document.data["nama"].toString(),
                            nik1 = document.data["nik"].toString(),
                            noKK1 = document.data["noKK"].toString(),
                            penghasilan1 = document.data["penghasilanKK"].toString(),
                            penutupAtap1 = document.data["nonstruktur_penutupAtap"].toString(),
                            ringBalok1 = document.data["struktur_ringBalok"].toString(),
                            samping1 = document.data["foto_samping"].toString(),
                            sloof1 = document.data["struktur_sloof"].toString(),
                            serverUid1 = document.data["serverUid"].toString(),
                            status1 = document.data["status"].toString(),
                            statusPenguasaanLahan1 = document.data["statusPenguasaanLahan"].toString(),
                            nilai1 = document.data["nilai"].toString(),
                            uid1 = document.data["uid"].toString()
                        )

                        listData.add(model)
                    }
                    historyList.postValue(listData)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }


    fun getHistory(): LiveData<ArrayList<SurveyModel>> {
        return historyList
    }
}