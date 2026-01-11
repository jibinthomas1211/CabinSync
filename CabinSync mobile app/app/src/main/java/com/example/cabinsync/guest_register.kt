package com.example.cabinsync

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cabinsync.databinding.GuestRegisterBinding
import com.example.cabinsync.dataclass.guestdata
import com.example.cabinsync.dataclass.meetingdata
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class guest_register : AppCompatActivity() {
    private val binding: GuestRegisterBinding by lazy{
        GuestRegisterBinding.inflate(layoutInflater)
    }
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        firebaseDatabase=FirebaseDatabase.getInstance()
        databaseReference=firebaseDatabase.reference

        val register=binding.registerbtn

        binding.date.addTextChangedListener(object : TextWatcher {
            private var currentLength = 0
            private var isDeleting = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                currentLength = s?.length ?: 0
                isDeleting = count > after
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val newLength = s?.length ?: 0
                if (!isDeleting && (newLength == 2 || newLength == 5) && newLength > currentLength) {
                    s?.append("/")
                }
            }
        })


        register.setOnClickListener(){
            val name = binding.guestname.text.toString().trim()
            val email = binding.guestemail.text.toString().trim()
            val phone = binding.guestphone.text.toString().trim()
            val gender = binding.editTextText4.text.toString().trim()
            val date = binding.date.text.toString().trim()
            val purpose = binding.purpose.text.toString().trim()
            val noof = binding.editTextText5.text.toString().trim()


            if (email.isEmpty() || date.isEmpty() || purpose.isEmpty()|| name.isEmpty()|| phone.isEmpty()|| gender.isEmpty()|| noof.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else if (!isValidEmail(email)) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            }else if (phone.length != 10 || !phone.isDigitsOnly()) {
                Toast.makeText(this, "Invalid phone number. Please enter exactly 10 digits.", Toast.LENGTH_SHORT).show()
            } else {
                signupUser(name, email, phone, date, purpose,gender,noof)
            }
        }
    }

    private fun signupUser(name: String, email: String, phone: String, date: String, purpose: String,gender: String, noof: String) {
        databaseReference.child("Guest").orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        val userId = databaseReference.child("Guest").push().key
                        if (userId != null) {
                            val status = "Activate"
                            val guest = guestdata(userId, name, email, phone,gender, status)
                            databaseReference.child("Guest").child(userId).setValue(guest)

                            // Save login state
                            val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                            with(sharedPrefs.edit()) {
                                putString("Guestid", userId)
                                putString("Guestmail", email)
                                putBoolean("GuestsignedIn", true)
                                apply()
                            }

                            // Save meeting details under Guest/userId/Meetings/meetingId
                            saveMeetingDetails(userId, date, purpose, noof)
                        }
                    } else {
                        Toast.makeText(this@guest_register, "Email already registered", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@guest_register, "Database Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun saveMeetingDetails(userId: String, date: String, purpose: String, noof: String) {
        val meetingId = databaseReference.child("Guest").child(userId).child("Meetings").push().key
        if (meetingId != null) {
            val status="Pending"
            val meeting = meetingdata(meetingId, date, purpose, noof,status)
            databaseReference.child("Guest").child(userId).child("Meetings").child(meetingId)
                .setValue(meeting)
                .addOnSuccessListener {
                    Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, guest_main::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error saving meeting: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}