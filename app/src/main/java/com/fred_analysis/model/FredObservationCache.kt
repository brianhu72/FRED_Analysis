package com.example.fred_analysis.model

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FredObservationCache @Inject constructor() {
    private val entries = object : LinkedHashMap<CacheKey, List<Observation>>(MAX_ENTRIES, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<CacheKey, List<Observation>>): Boolean = size > MAX_ENTRIES
    }

    @Synchronized
    fun get(key: CacheKey): List<Observation>? = entries[key]

    @Synchronized
    fun put(key: CacheKey, observations: List<Observation>) {
        entries[key] = observations
    }

    data class CacheKey(val seriesId: String, val startDate: String, val endDate: String)

    private companion object { const val MAX_ENTRIES = 12 }
}
