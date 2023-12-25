package com.example.foxichat.dto

data class Message(
    var imageUrl: String? = null,
    val author: String,
    val body: String,
    var isFromMe: Boolean = false,
    val visibleTo: MutableList<User> = mutableListOf()
) {
    constructor() : this(null, "User", "Some message")
}