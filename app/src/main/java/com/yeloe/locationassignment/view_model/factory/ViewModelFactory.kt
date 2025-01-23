package com.yeloe.locationassignment.view_model.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yeloe.locationassignment.repository.VisitRepository
import com.yeloe.locationassignment.view_model.VisitViewModel

class ViewModelFactory(
    private val application: Application,

    ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return VisitViewModel(application) as T
    }
}

