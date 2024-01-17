package com.example.kelompok4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.kelompok4.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.replaceFragment(HomeFragment())
        this.replaceFragment(HomeFragment())
        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.fragment_home ->
                    this.replaceFragment(HomeFragment())
                R.id.fragment_history ->
                    this.replaceFragment(HistoryFragment())
                R.id.fragment_settings ->
                    this.replaceFragment(SettingsFragment())

                else ->{}
            }
            true
        }
    }
    private fun replaceFragment(fragment: Fragment) {
        val change = supportFragmentManager
        val transaction = change.beginTransaction()
        transaction.replace(R.id.fragment, fragment)
        transaction.attach(fragment)
        transaction.commitNow()
    }
}