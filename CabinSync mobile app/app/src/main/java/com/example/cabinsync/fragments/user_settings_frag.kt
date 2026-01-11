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
import androidx.core.content.ContextCompat
import com.example.cabinsync.Changepassword
import com.example.cabinsync.R
import com.example.cabinsync.adapter.personaladapter
import com.example.cabinsync.databinding.UserHomeFragBinding
import com.example.cabinsync.databinding.UserSettingsFragBinding
import com.example.cabinsync.dataclass.authoritydata
import com.example.cabinsync.dataclass.meetingdata
import com.example.cabinsync.dataclass.userdata
import com.example.cabinsync.login_screen
import com.example.cabinsync.user_profile
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class user_settings_frag : Fragment() {
    private lateinit var binding:UserSettingsFragBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var authorityReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= UserSettingsFragBinding.inflate(inflater,container,false)

        firebaseDatabase=FirebaseDatabase.getInstance()
        databaseReference=firebaseDatabase.reference
        authorityReference = firebaseDatabase.reference.child("Authority")


        val sharedPrefs = activity?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPrefs?.getString("loginuser", "")
        val role = sharedPrefs?.getString("designation", "")
        if(role!="Authority"){
            binding.setavailbg.visibility=View.GONE
            binding.setavailtxt.visibility=View.GONE
        }else{
            email?.let { retrieveauth(it) }
        }

        email?.let { retrieve(it) }


        binding.button.setOnClickListener(){
            requireActivity().startActivity(Intent(requireContext(), user_profile::class.java))
        }

        binding.button2.setOnClickListener(){
            requireActivity().startActivity(Intent(requireContext(), Changepassword::class.java))
        }

        binding.button6.setOnClickListener(){
            val message:String? = "Are you sure you want to Logout"
            showCustomDialogBox(message)
        }

        binding.setavailtxt.setOnClickListener {
            val currentStatus = binding.setavailtxt.text.toString()
            val (message, status) = if (currentStatus == "Available") {
                "Update Status to Away" to "Away"
            } else {
                "Update Status to Available" to "Available"
            }
            val button = "Change"
            showCustomDialogBox1(message, status, button, email)
        }


        return binding.root
    }

    private fun showCustomDialogBox1(message: String, status: String, button: String, email: String?) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val passmessage: TextView = dialog.findViewById(R.id.dialogtext)
        val logoutButton: Button = dialog.findViewById(R.id.logoutbutton)
        val cancelButton: Button = dialog.findViewById(R.id.cancelbutton)

        passmessage.text = message
        logoutButton.text = button

        logoutButton.setOnClickListener {
            if (email.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Email is missing", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authorityReference.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (snapshot in dataSnapshot.children) {
                                val authData = snapshot.getValue(authoritydata::class.java)
                                val key = snapshot.key

                                if (authData != null && key != null) {
                                    authData.status = status
                                    authorityReference.child(key).setValue(authData)
                                        .addOnSuccessListener {
                                            Toast.makeText(requireContext(), "Status updated successfully", Toast.LENGTH_SHORT).show()
                                            retrieveauth(email)
                                            dialog.dismiss()
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                } else {
                                    Toast.makeText(requireContext(), "Error retrieving data", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(requireContext(), "Authority not found", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(requireContext(), "Database Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun retrieveauth(email: String) {
        databaseReference.child("Authority").orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (userSnapshot in dataSnapshot.children) {
                            val authdata = userSnapshot.getValue(authoritydata::class.java)
                            val status = authdata?.status
                            binding.setavailtxt.text = status

                            val color = if (status == "Away") {
                                ContextCompat.getColor(binding.root.context, R.color.red)
                            } else {
                                ContextCompat.getColor(binding.root.context, R.color.secondary)
                            }
                            binding.setavailbg.setCardBackgroundColor(color)
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

    private fun retrieve(email: String) {
        databaseReference.child("User").orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (userSnapshot in dataSnapshot.children) {
                            val userData = userSnapshot.getValue(userdata::class.java)
                            val name = userData?.name
                            val role = userData?.designation
                            binding.textView12.text = name
                            binding.textView13.text=role
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

    private fun showCustomDialogBox(message: String?) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val passmessage : TextView =dialog.findViewById(R.id.dialogtext)
        val logoutButton : Button = dialog.findViewById(R.id.logoutbutton)
        val cancelButton : Button = dialog.findViewById(R.id.cancelbutton)

        passmessage.text=message

        logoutButton.setOnClickListener {
            val sharedPrefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val editor = sharedPrefs.edit()
            editor.putBoolean("isSignedIn", false)
            editor.remove("loginuser")
            editor.apply()
            requireActivity().startActivity(Intent(requireContext(), login_screen::class.java))
            requireActivity().finish()
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    companion object {
    }
}