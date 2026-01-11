package com.example.cabinsync

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cabinsync.databinding.ChangepasswordBinding
import com.example.cabinsync.databinding.PersonalDetailsBinding
import com.example.cabinsync.dataclass.userdata
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Changepassword : AppCompatActivity() {
    private val binding: ChangepasswordBinding by lazy{
        ChangepasswordBinding.inflate(layoutInflater)
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

        binding.button13.setOnClickListener(){
            email?.let { it1 -> resetpassword(it1)}
        }
    }

    private fun resetpassword(email:String) {
        Log.d("Email",email)
        databaseReference.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (userSnapshot in dataSnapshot.children) {
                            val userData = userSnapshot.getValue(userdata::class.java)
                            val pass = userData?.password

                            val old = binding.editTextText.text.toString()
                            val new = binding.editTextText2.text.toString()
                            val con = binding.editTextText3.text.toString()

                            if (pass != old) {
                                Toast.makeText(this@Changepassword, "Current Password does not match", Toast.LENGTH_SHORT).show()
                            } else if (new != con) {
                                Toast.makeText(this@Changepassword, "New Password and Current Password does not match", Toast.LENGTH_LONG).show()
                            } else if (new.isEmpty() || con.isEmpty()) {
                                Toast.makeText(this@Changepassword, "Fill in all fields", Toast.LENGTH_SHORT).show()
                            } else {
                                userData?.let {
                                    it.password = new // Update password
                                    databaseReference.child(userSnapshot.key!!).setValue(it)
                                    Toast.makeText(this@Changepassword, "Password updated successfully", Toast.LENGTH_SHORT).show()
                                    onBackPressed()
                                    finish()
                                }
                            }
                        }

                    } else {

                        Toast.makeText(this@Changepassword, "User not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@Changepassword, "Database Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}