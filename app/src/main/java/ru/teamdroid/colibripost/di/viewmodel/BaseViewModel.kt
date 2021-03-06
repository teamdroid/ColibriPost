package ru.teamdroid.colibripost.di.viewmodel

import androidx.lifecycle.ViewModel
import ru.teamdroid.colibripost.domain.type.Failure
import ru.teamdroid.colibripost.other.SingleLiveData

open class BaseViewModel : ViewModel() {

    var failureData: SingleLiveData<Failure> = SingleLiveData()

    var progressData: SingleLiveData<Boolean> = SingleLiveData()

    open fun handleFailure(failure: Failure) {
        this.failureData.value = failure
    }

    open protected fun updateRefreshing(progress: Boolean) {
        this.progressData.value = progress
    }

}