package com.example.cabinsync

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cabinsync.databinding.UserHomeBinding
import com.example.cabinsync.databinding.UserProfileBinding
import com.example.cabinsync.dataclass.userdata
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class user_profile : AppCompatActivity() {
    private val binding: UserProfileBinding by lazy{
        UserProfileBinding.inflate(layoutInflater)
    }
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("User")

        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPrefs?.getString("loginuser", "")
        email?.let { retrieve(it) }
    }

    private fun retrieve(email: String) {
        databaseReference.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (userSnapshot in dataSnapshot.children) {
                            val userData = userSnapshot.getValue(userdata::class.java)
                            binding.textView36.text = userData?.designation
                            binding.textView41.text = userData?.id
                            binding.textView37.text = userData?.name
                            val gender=userData?.gender
                            binding.textView42.text=gender
                            binding.textView38.text = userData?.email
                            binding.textView39.text = userData?.phone
                            binding.textView40.text = userData?.department
                        }
                    } else {
                        Toast.makeText(this@user_profile, "User not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@user_profile, "Database Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}