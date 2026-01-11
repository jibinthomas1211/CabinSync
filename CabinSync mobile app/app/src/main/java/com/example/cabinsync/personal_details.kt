package com.example.cabinsync

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cabinsync.databinding.PersonalDetailsBinding
import com.example.cabinsync.databinding.UserProfileBinding
import com.example.cabinsync.dataclass.authoritydata
import com.example.cabinsync.dataclass.userdata
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class personal_details : AppCompatActivity() {
    private val binding: PersonalDetailsBinding by lazy{
        PersonalDetailsBinding.inflate(layoutInflater)
    }
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }


        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("Authority")

        val email= intent.getStringExtra("authemail")
        email?.let { retrieve(it) }

        binding.button12.setOnClickListener {
            if (binding.button12.text.toString() == "Notify Me") {
                val email = intent.getStringExtra("authemail")
                if (email != null) {
                    startNotificationListener(email)
                    Toast.makeText(this, "You will be notified when available", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun startNotificationListener(email: String) {
        databaseReference.orderByChild("email").equalTo(email)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        val status = child.child("status").getValue(String::class.java)
                        if (status.equals("Available", ignoreCase = true)) {
                            showAvailabilityNotification()
                            databaseReference.removeEventListener(this) // Remove listener after firing
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@personal_details, "Failed to monitor status", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showAvailabilityNotification() {
        val channelId = "availability_channel"
        val channelName = "Cabin Availability"

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(channelId, channelName, android.app.NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val name=binding.textView23.text

        val builder = androidx.core.app.NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logo_white) // Replace with a real icon from res/drawable
            .setContentTitle("Personnal Available")
            .setContentText("$name is now available in the Cabin!\nYou can visit accordingly!")
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(1, builder.build())
    }

    private fun retrieve(email: String) {
        databaseReference.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (userSnapshot in dataSnapshot.children) {
                            val authData = userSnapshot.getValue(authoritydata::class.java)
                            binding.textView21.text = authData?.designation
                            binding.textView23.text = authData?.name
                            binding.textView24.text = authData?.email
                            binding.textView35.text = authData?.location
                            val status=authData?.status
                            if (status.equals("Away")) {
                                binding.button12.text="Notify Me"
                            } else {
                                binding.button12.text="Available"
                            }
                        }
                    } else {
                        Toast.makeText(this@personal_details, "User not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@personal_details, "Database Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}