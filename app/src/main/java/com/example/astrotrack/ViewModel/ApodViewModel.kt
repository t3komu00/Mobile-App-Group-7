package com.example.astrotrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.astrotrack.model.ApodItem
import com.example.astrotrack.network.RetrofitInstance
import com.example.astrotrack.repository.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ApodViewModel : ViewModel() {

    private val favoritesRepo = FavoritesRepository()

    private val _apodList = MutableStateFlow<List<ApodItem>>(emptyList())
    val apodList: StateFlow<List<ApodItem>> = _apodList

    private val _favoriteList = MutableStateFlow<List<ApodItem>>(emptyList())
    val favoriteList: StateFlow<List<ApodItem>> = _favoriteList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _selectedItem = MutableStateFlow<ApodItem?>(null)
    val selectedItem: StateFlow<ApodItem?> = _selectedItem

    var savedStartDate: String = ""
    var savedEndDate: String = ""

    init {
        refreshFavorites()
    }

    fun fetchApod(start: String, end: String) {
        savedStartDate = start
        savedEndDate = end
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = RetrofitInstance.api.getApodData(start, end)
                _apodList.value = response
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun isFavorited(item: ApodItem): Boolean {
        return _favoriteList.value.any { it.date == item.date }
    }

    fun toggleFavorite(item: ApodItem, shouldFavorite: Boolean, onComplete: (String) -> Unit) {
        if (shouldFavorite) {
            favoritesRepo.addToFavorites(item,
                onSuccess = {
                    refreshFavorites()
                    onComplete("Saved to favorites")
                },
                onFailure = {
                    onComplete("Failed to save favorite")
                }
            )
        } else {
            favoritesRepo.removeFromFavorites(item,
                onSuccess = {
                    refreshFavorites()
                    onComplete("Removed from favorites")
                },
                onFailure = {
                    onComplete("Failed to remove favorite")
                }
            )
        }
    }

    fun refreshFavorites() {
        favoritesRepo.getFavorites {
            _favoriteList.value = it
        }
    }

    fun setSelectedItem(item: ApodItem) {
        _selectedItem.value = item
    }

    fun setError(message: String) {
        _errorMessage.value = message
    }
}
