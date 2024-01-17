package com.example.kelompok4

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import org.json.JSONException
import org.json.JSONObject


class SettingsFragment : Fragment() {
    private lateinit var nama : TextView
    private lateinit var email : TextView
    private lateinit var btnLogout : CardView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_settings,container,false)
        nama=view.findViewById(R.id.namaSetting)
        email=view.findViewById(R.id.emailSetting)
        btnLogout=view.findViewById(R.id.btnLogout)
        btnLogout.setOnClickListener {
            val preferences: SharedPreferences = requireActivity().getSharedPreferences("id", Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.clear()
            editor.apply()
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }
        getProfile()
        return view
    }

    private fun getProfile() {
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
                        val getEmail = dataObject.optString("email")
                        Log.d("Name", getName ?: "No name found")
                        nama.text = getName
                        email.text=getEmail
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