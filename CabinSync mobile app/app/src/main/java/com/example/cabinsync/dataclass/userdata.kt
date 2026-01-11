package com.example.cabinsync.dataclass

data class userdata(
    val id:String?=null,
    val email:String?=null,
    var password:String?=null,
    val name:String?=null,
    val gender:String?=null,
    val phone:String?=null,
    val designation:String?=null,
    var department: String? = null,
    val meetings: Map<String, meetingdata> = emptyMap()
)
