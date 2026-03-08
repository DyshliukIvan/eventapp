package com.dyshiuk.eventapp.network

data class EventDto(
    val id: Long,
    val title: String,
    val location: String,
    val date: String,
    val description: String
)