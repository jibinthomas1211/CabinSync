package com.example.cabinsync.fragments

import android.os.Bundle
import android.renderscript.ScriptGroup.Binding
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cabinsync.R
import com.example.cabinsync.adapter.personaladapter
import com.example.cabinsync.databinding.UserHomeFragBinding
import com.example.cabinsync.dataclass.authoritydata
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class user_home_frag : Fragment() {
    private lateinit var binding:UserHomeFragBinding
    private val fullList = mutableListOf<authoritydata>()
    private var filteredList = mutableListOf<authoritydata>()
    private lateinit var databaseReference: DatabaseReference
    private lateinit var adapter: personaladapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=UserHomeFragBinding.inflate(inflater,container,false)
        databaseReference = FirebaseDatabase.getInstance().getReference("Authority")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter =personaladapter(requireContext(),mutableListOf())
        binding.recyclerview.layoutManager=LinearLayoutManager(requireContext())
        binding.recyclerview.adapter=adapter

        adapter = personaladapter(requireContext(), mutableListOf())
        binding.recyclerview.layoutManager=LinearLayoutManager(requireContext())
        binding.recyclerview.adapter=adapter
        Log.d("Working","Working")
        fetchDataFromFirebase()
        binding.searchBox.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                Log.d("Working","Working")
                val filtered = if (query.isEmpty()) {
                    fullList  // your full list from Firebase or wherever
                } else {
                    fullList.filter {
                        (it.name?.contains(query, ignoreCase = true) ?: false) ||
                                (it.designation?.contains(query, ignoreCase = true) ?: false)
                    }
                }

                adapter.updateList(filtered)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun afterTextChanged(s: Editable?) { }
        })
    }

    private fun fetchDataFromFirebase() {
        Log.d("Working","Working")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                fullList.clear()
                for (dataSnapshot in snapshot.children) {
                    val authority = dataSnapshot.getValue(authoritydata::class.java)
                    authority?.let { fullList.add(it) }
                }
                adapter.updateList(fullList) // Initially show all
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to retrieve data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {

    }
}