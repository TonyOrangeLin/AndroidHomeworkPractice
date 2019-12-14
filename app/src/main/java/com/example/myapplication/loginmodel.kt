package com.example.myapplication

data class logindata(
    val username: String,
    val objectId: String,
    val sessionToken: String
)

data class updatedata(
    val updatedAt: String
)