package com.example.cabinsync.fragments

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cabinsync.R
import com.example.cabinsync.adapter.meetingadapter
import com.example.cabinsync.databinding.GuestSettingsBinding
import com.example.cabinsync.databinding.UserSchedulesFragBinding
import com.example.cabinsync.dataclass.meetingdata
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class user_schedules_frag : Fragment() {
    private lateinit var binding: UserSchedulesFragBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private val meetinglist = mutableListOf<meetingdata>()
    private lateinit var adapter: meetingadapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= UserSchedulesFragBinding.inflate(inflater,container,false)
        binding.addbtn.visibility=View.VISIBLE
        binding.imageView2.visibility=View.VISIBLE

        sharedPreferences = activity?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)!!
        val userid=sharedPreferences.getString("LoginId","")
        val role=sharedPreferences.getString("designation","")

        //if(role!="Authority" || role!="Teaching Staff"){
        //    binding.addbtn.visibility=View.GONE
        //binding.imageView2.visibility=View.GONE
       // }else{
         //   binding.addbtn.visibility=View.VISIBLE
          //  binding.imageView2.visibility=View.VISIBLE
       // }

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("User").child(userid!!).child("Meetings")

        adapter = meetingadapter(requireContext(), meetinglist)
        binding.userrecycle.layoutManager = LinearLayoutManager(requireContext())
        binding.userrecycle.adapter = adapter

        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
            filterMeetingsByDate(selectedDate)
        }

        // Initial load of all meetings from Firebase
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                meetinglist.clear()
                for (dataSnapshot in snapshot.children) {
                    val meeting = dataSnapshot.getValue(meetingdata::class.java)
                    meeting?.let { meetinglist.add(it) }
                }

                // Default filter to today's date
                val currentDate = java.text.SimpleDateFormat("dd/MM/yyyy").format(java.util.Date())
                filterMeetingsByDate(currentDate)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to retrieve data", Toast.LENGTH_SHORT).show()
            }
        })

        binding.imageView2.setOnClickListener {
            showCustomDialogBox()
        }

        return binding.root
    }

    private fun filterMeetingsByDate(selectedDate: String) {
        val filteredList = meetinglist.filter { it.date == selectedDate }
        adapter.updateData(filteredList)
    }


    private fun showCustomDialogBox() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.add_meeting)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val datebox: EditText = dialog.findViewById(R.id.editTextText6)
        val purposebox: EditText = dialog.findViewById(R.id.editTextText7)
        val noofbox: EditText = dialog.findViewById(R.id.editTextText8)
        val nooftbox: TextView = dialog.findViewById(R.id.textView53)
        val saveButton: Button = dialog.findViewById(R.id.savebutton)
        val cancelButton: Button = dialog.findViewById(R.id.cancelbutton2)

        noofbox.visibility = View.INVISIBLE
        nooftbox.visibility = View.INVISIBLE

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

            if (date.isEmpty() || purpose.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val id = databaseReference.push().key
            if (id != null) {
                val status = "Pending"
                val meeting = meetingdata(id, date, purpose, noof, status)
                databaseReference.child(id).setValue(meeting)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Meeting Scheduled", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to schedule meeting", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(requireContext(), "Failed to generate meeting ID", Toast.LENGTH_SHORT).show()
            }
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    companion object {

    }
}