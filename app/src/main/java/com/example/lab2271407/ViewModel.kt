package com.example.lab2271407

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ViewModel(private val repository: Repository) : ViewModel() {
    val allItems: StateFlow<List<ItemEntity>> = repository.allItems.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun insert(name: String) = viewModelScope.launch {
        repository.insert(ItemEntity(name))
    }
    
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as android.app.Application)
                val database = AppDatabase.getDatabase(application)
                val repository = Repository(database.itemDao())
                ViewModel(repository)
            }
        }
    }
}
