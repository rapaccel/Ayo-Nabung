package com.example.kelompok4

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.androidnetworking.interfaces.JSONObjectRequestListener
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.Calendar


class HomeFragment : Fragment() {
    private lateinit var nama : TextView
    private lateinit var total : TextView
    private lateinit var pemasukan :TextView
    private lateinit var pengeluaran : TextView
    private lateinit var tanggal : TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_home,container,false)
        val btn:ImageView=view.findViewById(R.id.tambah_data)
        nama = view.findViewById(R.id.homaNama)
        total=view.findViewById(R.id.total)
        pemasukan=view.findViewById(R.id.pemasukan)
        pengeluaran=view.findViewById(R.id.pengeluaran)
        tanggal=view.findViewById(R.id.date)
        btn.setOnClickListener {
            val intent= Intent(requireContext(),InputPemasukanActivity::class.java)
            startActivity(intent)
        }
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val monthNames = arrayOf(
            "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        )
        val bulan=monthNames[month]
        val currentDate = "$day $bulan $year"
        tanggal.text=currentDate
        getName()
        getTabungan()
        return view
    }
    private fun getName() {
        val sharedPreference = activity?.getSharedPreferences("id", Context.MODE_PRIVATE)
        val id = sharedPreference?.getString("id", "")
        val url = urlAPI.endpoint.url
        AndroidNetworking.get("$url/api/users/detailUser/$id")
            .addHeaders("Content-Type", "application/json")
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        val dataObject = response.getJSONObject("data")
                        val getName = dataObject.optString("name")
                        Log.d("Name", getName ?: "No name found")
                        nama.text = getName
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(error: ANError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun getTabungan() {
        val sharedPreference = activity?.getSharedPreferences("id", Context.MODE_PRIVATE)
        val id = sharedPreference?.getString("id", "")
        val url = urlAPI.endpoint.url
        AndroidNetworking.get("$url/api/tabungan/index/$id")
            .addHeaders("Content-Type", "application/json")
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        val message = response.optString("message")
                        if (message == "success") {
                            val dataArray = response.optJSONArray("data")
                            var totalPemasukan = 0
                            var totalPengeluaran = 0

                            for (i in 0 until dataArray.length()) {
                                val obj = dataArray.getJSONObject(i)
                                val tipe = obj.optString("tipe")
                                val jumlah = obj.optInt("jumlah", 0)

                                if (tipe.equals("pemasukan", ignoreCase = true)) {
                                    totalPemasukan += jumlah
                                } else if (tipe.equals("pengeluaran", ignoreCase = true)) {
                                    totalPengeluaran += jumlah
                                }
                            }
                            val saldo=totalPemasukan-totalPengeluaran
                            total.text = "Rp.$saldo"
                            pemasukan.text = "Rp.$totalPemasukan"
                            pengeluaran.text = "Rp.$totalPengeluaran"

                        } else {
                            Toast.makeText(context, "Kesalahan respons", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(error: ANError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }


}