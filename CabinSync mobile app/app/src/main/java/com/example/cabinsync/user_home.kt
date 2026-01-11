package com.example.cabinsync

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.cabinsync.databinding.UserHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class user_home : AppCompatActivity() {
    private val binding: UserHomeBinding by lazy{
        UserHomeBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        var NavController:NavController=findNavController(R.id.fragmentContainerView)
        var bottomNav:BottomNavigationView=findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setupWithNavController(NavController)
    }
}