package com.example.cabinsync.dataclass

data class guestdata(
    val id:String?=null,
    val name:String?=null,
    var email:String?=null,
    val phone:String?=null,
    val gender:String?=null,
    var status:String?=null,
    val meetings: Map<String, meetingdata> = emptyMap()
)
