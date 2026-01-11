package com.example.cabinsync.fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.cabinsync.Changepassword
import com.example.cabinsync.ManageMeeting
import com.example.cabinsync.R
import com.example.cabinsync.databinding.GuestSettingsBinding
import com.example.cabinsync.databinding.UserSettingsFragBinding
import com.example.cabinsync.dataclass.guestdata
import com.example.cabinsync.dataclass.meetingdata
import com.example.cabinsync.dataclass.userdata
import com.example.cabinsync.login_screen
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class guest_settings_frag : Fragment() {
    private lateinit var binding: GuestSettingsBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= GuestSettingsBinding.inflate(inflater,container,false)

        firebaseDatabase=FirebaseDatabase.getInstance()
        databaseReference=firebaseDatabase.reference.child("Guest")

        val sharedPrefs = activity?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPrefs?.getString("Guestmail", "")
        email?.let { retrieve(it) }

        binding.button7.setOnClickListener(){
            requireActivity().startActivity(Intent(requireContext(), ManageMeeting::class.java))
        }

        binding.button11.setOnClickListener(){
            val message:String? = "Are you sure you want to Delete account"
            val buttontext:String?="Delete"
            showCustomDialogBox(message,buttontext,email)
        }

        return binding.root
    }

    private fun retrieve(email: String) {
        databaseReference.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (userSnapshot in dataSnapshot.children) {
                            val guest = userSnapshot.getValue(guestdata::class.java)
                            val name = guest?.name
                            binding.textView16.text = name
                        }
                    } else {
                        Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Database Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showCustomDialogBox(message: String?, btntxt: String?, email: String?) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val passmessage : TextView =dialog.findViewById(R.id.dialogtext)
        val logoutButton : Button = dialog.findViewById(R.id.logoutbutton)
        val cancelButton : Button = dialog.findViewById(R.id.cancelbutton)

        passmessage.text=message
        logoutButton.text=btntxt

        logoutButton.setOnClickListener {
            deleteaccount(email)
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun deleteaccount(email: String?) {
        if (email.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Email is missing", Toast.LENGTH_SHORT).show()
            return
        }

        databaseReference.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (meetingSnapshot in dataSnapshot.children) {
                            val guest = meetingSnapshot.getValue(guestdata::class.java)
                            val key = meetingSnapshot.key

                            if (guest != null && key != null) {
                                guest.status = "Deleted"
                                // Prevent appending "_deleted" multiple times
                                guest.email = if (!email.endsWith("_deleted")) {
                                    "${email}_deleted"
                                } else {
                                    email
                                }

                                val updates = mapOf<String, Any>(
                                    "status" to "Deleted",
                                    "email" to if (!email.endsWith("_deleted")) "${email}_deleted" else email
                                )
                                databaseReference.child(key).updateChildren(updates)
                                    .addOnSuccessListener {
                                        Toast.makeText(requireContext(), "Account deleted successfully", Toast.LENGTH_SHORT).show()

                                        // Clear SharedPreferences
                                        val sharedPrefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                                        with(sharedPrefs.edit()) {
                                            putBoolean("GuestsignedIn", false)
                                            remove("Guestmail")
                                            remove("Guestid")
                                            apply()
                                        }

                                        // Redirect to login
                                        val intent = Intent(requireContext(), login_screen::class.java)
                                        requireActivity().startActivity(intent)
                                        requireActivity().finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(requireContext(), "Failed to delete: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                Toast.makeText(requireContext(), "Error retrieving guest data", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "Guest not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }


    companion object {
    }
}