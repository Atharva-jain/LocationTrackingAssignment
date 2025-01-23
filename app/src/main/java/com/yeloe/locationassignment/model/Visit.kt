package com.yeloe.locationassignment.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "visit_table")
data class Visit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val locationName: String,
    val date: String,
    val entryTime: String,
    val exitTime: String,
    val duration: String
)