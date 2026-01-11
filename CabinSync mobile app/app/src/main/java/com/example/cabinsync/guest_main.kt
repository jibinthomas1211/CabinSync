package com.example.cabinsync

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.cabinsync.databinding.GuestMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class guest_main : AppCompatActivity() {
    private val binding: GuestMainBinding by lazy{
        GuestMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        var NavController: NavController =findNavController(R.id.fragmentContainerView1)
        var bottomNav: BottomNavigationView =findViewById<BottomNavigationView>(R.id.bottomNavigationView2)
        bottomNav.setupWithNavController(NavController)
    }
}