package com.yeloe.locationassignment.repository

import androidx.room.Update
import com.yeloe.locationassignment.doa.VisitDao
import com.yeloe.locationassignment.model.Visit

class VisitRepository(private val visitDao: VisitDao) {

    suspend fun insertVisit(visit: Visit) {
        visitDao.insertVisit(visit)
    }

    val getAllVisits = visitDao.getAllVisits()

    fun getLastVisit(): Visit? {
        return visitDao.getLastVisit()
    }

    suspend fun updateVisit(visit: Visit) {
        visitDao.updateVisit(visit)
    }

}