package com.poojasinghandroid.userbehaviourtrackingapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.poojasinghandroid.userbehaviourtrackingapp.data.UserBehavior
import com.poojasinghandroid.userbehaviourtrackingapp.data.local.AppDatabase
import kotlinx.coroutines.launch

class BehaviorViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).userBehaviorDao()

    private val _lastSessions = MutableLiveData<List<UserBehavior>>()
    val lastSessions: LiveData<List<UserBehavior>> = _lastSessions

    init {
        loadLastSessions()
    }

    fun saveBehavior(inputText: String) {
        viewModelScope.launch {
            dao.insert(UserBehavior(inputText = inputText))
            loadLastSessions()
        }
    }

    private fun loadLastSessions() {
        viewModelScope.launch {
            _lastSessions.postValue(dao.getLastSessions())
        }
    }
}