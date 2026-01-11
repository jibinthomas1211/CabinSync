package com.example.cabinsync

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cabinsync.adapter.meetingadapter
import com.example.cabinsync.databinding.ManageMeetingBinding
import com.example.cabinsync.databinding.MeetingsDetailsBinding
import com.example.cabinsync.dataclass.guestdata
import com.example.cabinsync.dataclass.meetingdata
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ManageMeeting : AppCompatActivity() {
    private val binding: ManageMeetingBinding by lazy{
        ManageMeetingBinding.inflate(layoutInflater)
    }

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private val meetinglist = mutableListOf<meetingdata>()
    private lateinit var adapter: meetingadapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val meetingid = intent.getStringExtra("meetingId")
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val guestid=sharedPreferences.getString("Guestid","")

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("Guest").child(guestid!!).child("Meetings")

        adapter = meetingadapter(this, meetinglist)
        binding.managerecycle.layoutManager = LinearLayoutManager(this)
        binding.managerecycle.adapter = adapter

        databaseReference
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    meetinglist.clear()
                    for (dataSnapshot in snapshot.children) {
                        val pet = dataSnapshot.getValue(meetingdata::class.java)
                        pet?.let {
                            meetinglist.add(it)
                        }
                    }

                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ManageMeeting, "Failed to retrieve data", Toast.LENGTH_SHORT).show()
                }
            })

        binding.imageView25.setOnClickListener(){
            showCustomDialogBox()
        }
    }

    private fun showCustomDialogBox() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.add_meeting)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val datebox: EditText = dialog.findViewById(R.id.editTextText6)
        val purposebox: EditText = dialog.findViewById(R.id.editTextText7)
        val noofbox: EditText = dialog.findViewById(R.id.editTextText8)
        val saveButton: Button = dialog.findViewById(R.id.savebutton)
        val cancelButton: Button = dialog.findViewById(R.id.cancelbutton2)

        datebox.addTextChangedListener(object : TextWatcher {
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

        saveButton.setOnClickListener {
            val date = datebox.text.toString().trim()
            val purpose = purposebox.text.toString().trim()
            val noof = noofbox.text.toString().trim()

            if (date.isEmpty() || purpose.isEmpty() || noof.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val id = databaseReference.push().key
            if (id != null) {
                val status = "Pending"
                val meeting = meetingdata(id, date, purpose, noof, status)
                databaseReference.child(id).setValue(meeting)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Meeting Scheduled", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to schedule meeting", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Failed to generate meeting ID", Toast.LENGTH_SHORT).show()
            }
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}