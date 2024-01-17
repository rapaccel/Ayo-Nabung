package com.example.kelompok4

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.kelompok4.databinding.ActivityRegistrasiBinding
import org.json.JSONObject

class RegistrasiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrasiBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRegistrasiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnLojin.setOnClickListener {
            val intent=Intent(this@RegistrasiActivity,MainActivity::class.java)
            startActivity(intent)
        }
        binding.btnDaftar.setOnClickListener{
            if (binding.hintNama.text.toString().isEmpty() || binding.hintPass.text.toString().isEmpty() || binding.email.text.toString().isEmpty()){
                Toast.makeText(this@RegistrasiActivity,"Silahkan isi semua field",Toast.LENGTH_SHORT).show()
            }
            else {
                register()
            }
        }
    }
    private fun register() {
        val url = urlAPI.endpoint.url
        val jsonObject = JSONObject()
        jsonObject.put("name", binding.hintNama.text.toString())
        jsonObject.put("email", binding.email.text.toString())
        jsonObject.put("password", binding.hintPass.text.toString())

        AndroidNetworking.post("$url/api/users/register")
            .addJSONObjectBody(jsonObject)
            .addHeaders("Content-Type", "application/json")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    try {
                        val success = response?.optBoolean("success") ?: false
                        if (success) {
                            Toast.makeText(this@RegistrasiActivity,"Registrasi Berhasil",Toast.LENGTH_SHORT).show()
                            val intent=Intent(this@RegistrasiActivity,MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@RegistrasiActivity,"Terjadi kesalahan saat melakukan registras",Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@RegistrasiActivity,"Respons gagal",Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(anError: ANError?) {
                    Toast.makeText(this@RegistrasiActivity,anError.toString(),Toast.LENGTH_SHORT).show()
                }
            })
    }
}