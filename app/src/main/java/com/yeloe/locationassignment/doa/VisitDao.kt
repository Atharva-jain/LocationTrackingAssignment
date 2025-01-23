package com.yeloe.locationassignment.doa

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.yeloe.locationassignment.model.Visit

@Dao
interface VisitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisit(visit: Visit)

    @Query("SELECT * FROM visit_table ORDER BY date DESC")
    fun getAllVisits(): LiveData<List<Visit>>

    @Query("SELECT * FROM visit_table ORDER BY id DESC LIMIT 1")
    fun getLastVisit(): Visit?

    @Update
    suspend fun updateVisit(visit: Visit)
}