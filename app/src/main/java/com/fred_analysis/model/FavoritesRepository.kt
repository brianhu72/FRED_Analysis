package com.example.fred_analysis.model

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepository @Inject constructor(@ApplicationContext context: Context) {
    private val preferences = context.getSharedPreferences("fred_preferences", Context.MODE_PRIVATE)
    private val _favorites = MutableStateFlow(readFavorites())
    val favorites = _favorites.asStateFlow()

    fun toggle(seriesId: String) {
        val updated = _favorites.value.toMutableSet().apply {
            if (!add(seriesId)) remove(seriesId)
        }
        _favorites.value = updated
        preferences.edit().putStringSet(FAVORITES_KEY, updated).apply()
    }

    private fun readFavorites(): Set<String> = preferences.getStringSet(FAVORITES_KEY, emptySet()).orEmpty()

    private companion object { const val FAVORITES_KEY = "favorite_series" }
}
