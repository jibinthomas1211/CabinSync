package com.example.cabinsync.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cabinsync.MeetingsDetails
import com.example.cabinsync.databinding.MeetingdetailsBinding
import com.example.cabinsync.dataclass.meetingdata

class meetingadapter(private val context: Context, private var meetinglist: List<meetingdata>): RecyclerView.Adapter<meetingadapter.meetingViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): meetingadapter.meetingViewHolder {
        val binding = MeetingdetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return meetingadapter.meetingViewHolder(binding, context)
    }



    override fun onBindViewHolder(holder: meetingadapter.meetingViewHolder, position: Int) {
        val meetings = meetinglist[position]
        holder.bind(meetings)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, MeetingsDetails::class.java)
            intent.putExtra("meetingId", meetings.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return meetinglist.size
    }

    fun updateData(newMeetings: List<meetingdata>) {
        meetinglist = newMeetings
        notifyDataSetChanged()
    }

    class meetingViewHolder(private val binding: MeetingdetailsBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(meeting: meetingdata) {
            binding.date.text=meeting.date
            binding.meepurpose.text=meeting.purpose
        }

    }
}