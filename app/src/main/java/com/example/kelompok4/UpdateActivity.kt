package com.example.kelompok4

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.kelompok4.databinding.ActivityUpdateBinding
import org.json.JSONException
import org.json.JSONObject

class UpdateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getTabungan()
        binding.btnSimpanUpdate.setOnClickListener {
            update()
        }

    }
    private fun getTabungan() {
        val tabunganId = intent.getStringExtra("idHistory")?.toInt()
        Log.d("ini id",tabunganId.toString())
        val url = urlAPI.endpoint.url
        AndroidNetworking.get("$url/api/tabungan/detail/$tabunganId")
            .addHeaders("Content-Type", "application/json")
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        val success = response.optBoolean("success")
                        if (success) {
                            Log.d("respons server",response?.getJSONObject("data").toString())
                            val dataObject = response.optJSONObject("data")
                            val jumlah = dataObject.getInt("jumlah").toString()
                            val jenis = dataObject.getString("jenis")
                            val tipe = dataObject.getString("tipe")
                            val keterangan = dataObject.getString("keterangan")
                            binding.jumlahPemasukanUpdate.setText(jumlah)
                            binding.keteranganPemasukanUpdate.setText(keterangan)
                            if (tipe == "pemasukan") {
                                binding.pemasukanUpdate.isChecked = true
                            } else {
                                binding.pengeluaranUpdate.isChecked = true
                            }
                            if (jenis=="kebutuhan"){
                                binding.kebutuhanUpdate.isChecked=true
                            }else if (jenis=="investasi"){
                                binding.investasiUpdate.isChecked=true
                            }else{
                                binding.keinginanUpdate.isChecked=true
                            }
                        } else {
                            Toast.makeText(this@UpdateActivity, "Kesalahan respons", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(this@UpdateActivity, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onError(error: ANError) {
                    Toast.makeText(this@UpdateActivity, error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }
    private fun update() {
        val selectedRadioButtonId = binding.radioGroupJenis?.checkedRadioButtonId
        val selectedRadioButton: RadioButton? = findViewById(selectedRadioButtonId ?: -1)
        val jenis = selectedRadioButton?.text.toString()
        val tabunganId = intent.getStringExtra("idHistory")?.toInt()
        val selectedRadioButtonTipe = binding.radioGroupUpdate?.checkedRadioButtonId
        val selectedRadioTipe: RadioButton? = findViewById(selectedRadioButtonTipe ?: -1)
        val tipe = selectedRadioTipe?.text.toString()

        val sharedPreference = getSharedPreferences("id", Context.MODE_PRIVATE)
        val id = sharedPreference?.getString("id", "")
        val jumlahString = binding.jumlahPemasukanUpdate.text.toString()
        val jumlahInt = jumlahString.toInt()
        val url = urlAPI.endpoint.url
        val jsonObject = JSONObject()
        jsonObject.put("jumlah", jumlahInt)
        jsonObject.put("jenis", tipe)
        jsonObject.put("tipe", jenis)
        jsonObject.put("keterangan", binding.keteranganPemasukanUpdate?.text.toString())
        jsonObject.put("id_user", id?.toInt())

        Log.d("DataToSend", jsonObject.toString())

        AndroidNetworking.put("$url/api/tabungan/update/$tabunganId")
            .addJSONObjectBody(jsonObject)
            .addHeaders("Content-Type", "application/json")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    Log.d("respons server",response?.getJSONObject("data").toString())
                    try {
                        val success = response?.optBoolean("success") ?: false
                        if (success) {
                            Toast.makeText(this@UpdateActivity, "Berhasil Mengubah Data", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@UpdateActivity, DashboardActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@UpdateActivity, "Terjadi kesalahan saat menambah data", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@UpdateActivity, "Respons gagal", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(anError: ANError?) {
                    Toast.makeText(this@UpdateActivity, "Respons $tipe $jenis ${anError?.errorCode}: ${anError?.errorDetail}", Toast.LENGTH_LONG).show()
                }
            })
    }

}