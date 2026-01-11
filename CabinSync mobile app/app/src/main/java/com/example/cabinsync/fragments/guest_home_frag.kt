package com.example.cabinsync.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cabinsync.R
import com.example.cabinsync.adapter.meetingadapter
import com.example.cabinsync.databinding.GuestHomeFragBinding
import com.example.cabinsync.databinding.UserHomeFragBinding
import com.example.cabinsync.dataclass.meetingdata
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class guest_home_frag : Fragment() {
    private lateinit var binding: GuestHomeFragBinding
    private val meetinglist = mutableListOf<meetingdata>()
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var adapter: meetingadapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=GuestHomeFragBinding.inflate(inflater,container,false)

        val sharedPrefs = activity?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val id = sharedPrefs?.getString("Guestid", "")

        databaseReference = FirebaseDatabase.getInstance().getReference("Guest").child(id!!).child("Meetings")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = meetingadapter(requireContext(), meetinglist)
        binding.guestrecycle.layoutManager = LinearLayoutManager(requireContext())
        binding.guestrecycle.adapter = adapter

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                meetinglist.clear()
                val today = getTodayDate()

                for (dataSnapshot in snapshot.children) {
                    val meeting = dataSnapshot.getValue(meetingdata::class.java)
                    meeting?.let {
                        val isNotDeleted = it.status != "Deleted"
                        val isUpcoming = isDateTodayOrFuture(it.date, today)

                        if (isNotDeleted && isUpcoming) {
                            meetinglist.add(it)
                        }
                    }
                }

                adapter.notifyDataSetChanged()

                // Show message if no meetings
                binding.noMeetingsText.visibility = if (meetinglist.isEmpty()) View.VISIBLE else View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to retrieve data", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun getTodayDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun isDateTodayOrFuture(dateStr: String?, todayStr: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val meetingDate = sdf.parse(dateStr ?: "") ?: return false
            val todayDate = sdf.parse(todayStr) ?: return false
            !meetingDate.before(todayDate)
        } catch (e: Exception) {
            false
        }
    }


    companion object {
    }
}