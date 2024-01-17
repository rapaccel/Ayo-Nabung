package com.example.kelompok4

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class HistoryFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataHistory: ArrayList<DataHistory>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_history,container,false)
        recyclerView = view.findViewById(R.id.recycler_pemasukan)
        dataHistory= arrayListOf<DataHistory>()
//        val harga= arrayOf(
//            "-8.000.000",
//            "-200.000",
//            "-800.000"
//        )
//        val keterangan= arrayOf(
//            "Investasi Usaha",
//            "Investasi keluarga",
//            "Investasi"
//        )
//        val tanggal= arrayOf(
//            "22 oktober 20223",
//            "20 september 2020",
//            "19 januari 1999"
//        )
//        val tipe= arrayOf(
//            "Investasi",
//            "Investasi"
//            ,"Investasi"
//        )
//        for (i in harga.indices) {
//            dataHistory.add(
//                DataHistory(
//                    harga[i],
//                    keterangan[i],
//                    tanggal[i],
//                    tipe[i]
//
//                )
//            )
//        }
        // Inflate the layout for this fragment
        getTabungan()
        return view
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
                            val res: JSONArray? = response?.getJSONArray("data")
                            for (i in 0 until res?.length()!!) {
                                val item = res.getJSONObject(i)
                                dataHistory.add(
                                    DataHistory(
                                        item.getInt("jumlah"),
                                        item.getString("keterangan"),
                                        item.getString("updated_at"),
                                        item.getString("tipe"),
                                        item.getInt("id")
                                    )
                                )
                                addData()
                            }

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
    private fun addData() {
        val linear = LinearLayoutManager(requireContext())
        linear.stackFromEnd = true
        linear.reverseLayout = true
        recyclerView.layoutManager = linear
        recyclerView.adapter = AdapterHistory(requireContext(), dataHistory)
    }

}