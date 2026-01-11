package com.example.cabinsync.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.cabinsync.R
import com.example.cabinsync.databinding.PersonalBinding
import com.example.cabinsync.dataclass.authoritydata
import com.example.cabinsync.personal_details

class personaladapter(private val context: Context,private val authorityList: MutableList<authoritydata>): RecyclerView.Adapter<personaladapter.personalViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): personalViewHolder {
        val binding=PersonalBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return personalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: personalViewHolder, position: Int) {
        val authority=authorityList[position]
        holder.bind(authority)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, personal_details::class.java)
            intent.putExtra("authemail", authority.email)
            context.startActivity(intent)
        }
    }
    override fun getItemCount(): Int {
       return authorityList.size
    }

    fun updateList(newList: List<authoritydata>) {
        authorityList.clear()
        authorityList.addAll(newList)
        notifyDataSetChanged()
    }

    class personalViewHolder(private val binding: PersonalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(authority: authoritydata) {
            binding.names.text = authority.name
            binding.desig.text = authority.designation
            binding.availtxt.text = authority.status
            val color = if (authority.status == "Away") {
                ContextCompat.getColor(binding.root.context, R.color.red)
            } else {
                ContextCompat.getColor(binding.root.context, R.color.secondary)
            }
            binding.availbg.setCardBackgroundColor(color)
        }
    }
}