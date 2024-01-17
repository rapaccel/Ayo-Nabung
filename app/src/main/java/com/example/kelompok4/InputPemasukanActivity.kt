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
import com.example.kelompok4.databinding.ActivityInputPemasukanBinding
import org.json.JSONObject

class InputPemasukanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInputPemasukanBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityInputPemasukanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnSimpan.setOnClickListener {


            if (binding.jumlahPemasukan.text.isEmpty() || binding.keteranganPemasukan?.text.toString().isEmpty()) {
                Toast.makeText(this@InputPemasukanActivity, "Silahkan isi semua field", Toast.LENGTH_SHORT).show()
            } else {
                store()
            }
        }


    }
    private fun store() {
        val selectedRadioButtonId = binding.radioGroupJenis?.checkedRadioButtonId
        val selectedRadioButton: RadioButton? = findViewById(selectedRadioButtonId ?: -1)
        val jenis = selectedRadioButton?.text.toString()

        val selectedRadioButtonTipe = binding.radioGroup?.checkedRadioButtonId
        val selectedRadioTipe: RadioButton? = findViewById(selectedRadioButtonTipe ?: -1)
        val tipe = selectedRadioTipe?.text.toString()

        val sharedPreference = getSharedPreferences("id", Context.MODE_PRIVATE)
        val id = sharedPreference?.getString("id", "")
        val jumlahString = binding.jumlahPemasukan.text.toString()
        val jumlahInt = jumlahString.toInt()
        val url = urlAPI.endpoint.url
        val jsonObject = JSONObject()
        jsonObject.put("jumlah", jumlahInt)
        jsonObject.put("jenis", tipe)
        jsonObject.put("tipe", jenis)
        jsonObject.put("keterangan", binding.keteranganPemasukan?.text.toString())
        jsonObject.put("id_user", id?.toInt())

        Log.d("DataToSend", jsonObject.toString())

        AndroidNetworking.post("$url/api/tabungan/store")
            .addJSONObjectBody(jsonObject)
            .addHeaders("Content-Type", "application/json")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    try {
                        val success = response?.optBoolean("success") ?: false
                        if (success) {
                            Toast.makeText(this@InputPemasukanActivity, "Berhasil Menambahkan Data", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@InputPemasukanActivity, DashboardActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@InputPemasukanActivity, "Terjadi kesalahan saat menambah data", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@InputPemasukanActivity, "Respons gagal", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(anError: ANError?) {
                    Toast.makeText(this@InputPemasukanActivity, "Respons $tipe $jenis ${anError?.errorCode}: ${anError?.errorDetail}", Toast.LENGTH_LONG).show()
                }
            })
    }

}