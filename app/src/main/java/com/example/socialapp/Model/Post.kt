package com.example.socialapp.Model

data class Post(
    val text : String = "" ,
    val createdBy : user = user() ,
    val createdAt : Long = 0L  ,
    val Likes : ArrayList<String> = ArrayList()
)
