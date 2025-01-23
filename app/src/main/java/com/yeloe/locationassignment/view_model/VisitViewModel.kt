package com.yeloe.locationassignment.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Update
import com.yeloe.locationassignment.database.VisitDatabase
import com.yeloe.locationassignment.model.Visit
import com.yeloe.locationassignment.repository.VisitRepository
import kotlinx.coroutines.launch

class VisitViewModel(application: Application) :
    AndroidViewModel(application) {

    private val visitRepository: VisitRepository

    // read users
    val readAllVisit: LiveData<List<Visit>>

    init {
        val visitDatabase = VisitDatabase.getInstance(
            application
        ).visitDao()
        this.visitRepository = VisitRepository(visitDatabase)
        readAllVisit = this.visitRepository.getAllVisits
    }

    fun insertVisit(visit: Visit) {
        viewModelScope.launch {
            visitRepository.insertVisit(visit)
        }
    }

    fun getLastVisit(): Visit? {
        return visitRepository.getLastVisit()
    }

    fun updateVisit(visit: Visit) {
        viewModelScope.launch {
            visitRepository.updateVisit(visit)
        }
    }

}