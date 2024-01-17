package com.example.kelompok4

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.ProviderStatus.STATUS
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.kelompok4.databinding.ActivityMainBinding
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var pass : EditText
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pass=findViewById(R.id.hint2)
        binding.btnLogin.setOnClickListener{
            if (binding.hint1.text.toString().isEmpty() || pass.text.isEmpty()){
                Toast.makeText(this@MainActivity,"Silahkan isi semua field",Toast.LENGTH_SHORT).show()
            }
            else {
                login()
            }
        }
        binding.daftarsekarang.setOnClickListener {
            val intentt=Intent(this@MainActivity,RegistrasiActivity::class.java)
            startActivity(intentt)
        }
    }
    private fun login() {
        val url = urlAPI.endpoint.url
        val jsonObject = JSONObject()

        try {
            jsonObject.put("email", binding.hint1.text.toString())
            jsonObject.put("password", pass.text.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        AndroidNetworking.post("$url/api/users/login")
            .addJSONObjectBody(jsonObject)
            .addHeaders("Content-Type", "application/json")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    try {
                        Log.d("respon", response.toString())
                        val success = response?.optBoolean("success") ?: false
                        if (success) {
                            Log.d("respon", response.toString())
                            Toast.makeText(
                                this@MainActivity,
                                "Login successful",
                                Toast.LENGTH_LONG
                            ).show()
                            val getId=response?.getString("id")
                            val intent = Intent(this@MainActivity, DashboardActivity::class.java)
                            startActivity(intent)
                            finish()
                            val sharedPreference =  getSharedPreferences("id", Context.MODE_PRIVATE)
                            val editorId = sharedPreference.edit()
                            editorId.putString("id", getId)
                            editorId.commit()
                        } else {
                            Log.d("respon", response.toString())
                            Toast.makeText(
                                this@MainActivity,
                                "email atau password salah",
                                Toast.LENGTH_LONG
                            ).show()

                        }

                    } catch (e: JSONException) {
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(anError: ANError?) {
                    Toast.makeText(
                        this@MainActivity,
                        "Terjadi kesalahan saat melakukan login",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

}