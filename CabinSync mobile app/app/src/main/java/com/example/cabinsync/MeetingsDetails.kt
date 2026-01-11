package com.example.cabinsync

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cabinsync.databinding.GuestMainBinding
import com.example.cabinsync.databinding.MeetingdetailsBinding
import com.example.cabinsync.databinding.MeetingsDetailsBinding
import com.example.cabinsync.dataclass.guestdata
import com.example.cabinsync.dataclass.meetingdata
import com.example.cabinsync.dataclass.userdata
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MeetingsDetails : AppCompatActivity() {
    private val binding: MeetingsDetailsBinding by lazy{
        MeetingsDetailsBinding.inflate(layoutInflater)
    }

    private var lastStatus: String? = null

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                "meeting_status_channel",
                "Meeting Status Updates",
                android.app.NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies when meeting status changes"
            }

            val notificationManager = getSystemService(android.app.NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }


        val meetingid = intent.getStringExtra("meetingId")
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val guestid=sharedPreferences.getString("Guestid","")

        binding.button14.visibility =View.VISIBLE

        retrieveguest(guestid!!,meetingid!!)
        
        binding.button14.setOnClickListener(){
            deletemeeting(guestid!!,meetingid!!)
        }
    }

    private fun retrieveguest(guestid: String,meetingid:String) {
        databaseReference.child("Guest").orderByChild("id").equalTo(guestid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (userSnapshot in dataSnapshot.children) {
                            val guestData = userSnapshot.getValue(guestdata::class.java)
                            val name = guestData?.name
                            val email = guestData?.email
                            val contact = guestData?.phone

                            binding.textView26.text=name
                            binding.textView28.text=email
                            binding.textView30.text=contact

                            retrievemeetings(guestid,meetingid)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MeetingsDetails,"Database Error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun retrievemeetings(guestid: String, meetingid:String) {
        databaseReference.child("Guest").child(guestid).child("Meetings").orderByChild("id").equalTo(meetingid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (userSnapshot in dataSnapshot.children) {
                            val meetingData = userSnapshot.getValue(meetingdata::class.java)
                            val date = meetingData?.date
                            val purpose = meetingData?.purpose
                            val status = meetingData?.status

                            binding.textView32.text=date
                            binding.textView34.text=purpose
                            binding.textView48.text=status

                            if (lastStatus != null && lastStatus == "Pending" && status != "Pending") {
                                sendStatusNotification(status ?: "Updated")
                            }

                            lastStatus = status

                            if(status=="Deleted"){
                                binding.button14.visibility =View.GONE
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MeetingsDetails,"Database Error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun sendStatusNotification(status: String) {
        val notificationBuilder = androidx.core.app.NotificationCompat.Builder(this, "meeting_status_channel")
            .setSmallIcon(R.drawable.logo_white) // Replace with your notification icon
            .setContentTitle("Meeting Status Updated")
            .setContentText("Your Entry status is now updated: $status")
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = androidx.core.app.NotificationManagerCompat.from(this)

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
            == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(1002, notificationBuilder.build())
        }
    }


    private fun deletemeeting(guestid: String, meetingid: String) {
        val meetingRef = databaseReference.child("Guest").child(guestid).child("Meetings")

        meetingRef.orderByChild("id").equalTo(meetingid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (meetingSnapshot in dataSnapshot.children) {
                            val meetingData = meetingSnapshot.getValue(meetingdata::class.java)
                            meetingData?.let {
                                it.status = "Deleted"
                                meetingRef.child(meetingSnapshot.key!!).setValue(it).addOnSuccessListener {
                                    Toast.makeText(this@MeetingsDetails, "Deleted successfully", Toast.LENGTH_SHORT).show()
                                        onBackPressedDispatcher.onBackPressed()
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            this@MeetingsDetails,
                                            "Error updating meeting: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        }
                    } else {
                        Toast.makeText(this@MeetingsDetails, "Meeting not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MeetingsDetails, "Database Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

}