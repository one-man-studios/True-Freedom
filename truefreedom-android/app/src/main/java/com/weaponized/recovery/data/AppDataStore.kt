package com.weaponized.recovery.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.appDataStore: DataStore<Preferences> by preferencesDataStore(name = "weaponized_state")

/**
 * Persists the app's recovery state — start date, relapse count, urge count,
 * T-Maxx habit check-offs — to AndroidX DataStore.
 *
 * All data stays on the device. Nothing is uploaded anywhere.
 */
class AppDataStore(private val context: Context) {

    companion object {
        val START_DATE = stringPreferencesKey("start_date")
        val LAST_RELAPSE = stringPreferencesKey("last_relapse")
        val RELAPSE_COUNT = intPreferencesKey("relapse_count")
        val URGE_COUNT = intPreferencesKey("urge_count")
        val WARN_INDEX = intPreferencesKey("warn_index")
        val DMG_INDEX = intPreferencesKey("dmg_index")
        val WARN_AUTO = booleanPreferencesKey("warn_auto")
        val DMG_AUTO = booleanPreferencesKey("dmg_auto")
        val ACTIVE_VIEW = stringPreferencesKey("active_view")
    }

    val stateFlow: Flow<AppState> = context.appDataStore.data.map { p ->
        AppState(
            startDate = p[START_DATE],
            lastRelapseDate = p[LAST_RELAPSE],
            relapseCount = p[RELAPSE_COUNT] ?: 0,
            urgeCount = p[URGE_COUNT] ?: 0,
            warnIndex = p[WARN_INDEX] ?: 0,
            dmgIndex = p[DMG_INDEX] ?: 0,
            warnAuto = p[WARN_AUTO] ?: true,
            dmgAuto = p[DMG_AUTO] ?: true,
            activeView = p[ACTIVE_VIEW] ?: "dashboard"
        )
    }

    suspend fun update(transform: (AppState) -> AppState) {
        // Read-modify-write via individual keys
        context.appDataStore.edit { p ->
            val current = AppState(
                startDate = p[START_DATE],
                lastRelapseDate = p[LAST_RELAPSE],
                relapseCount = p[RELAPSE_COUNT] ?: 0,
                urgeCount = p[URGE_COUNT] ?: 0,
                warnIndex = p[WARN_INDEX] ?: 0,
                dmgIndex = p[DMG_INDEX] ?: 0,
                warnAuto = p[WARN_AUTO] ?: true,
                dmgAuto = p[DMG_AUTO] ?: true,
                activeView = p[ACTIVE_VIEW] ?: "dashboard"
            )
            val next = transform(current)
            p[START_DATE] = next.startDate ?: ""
            if (next.lastRelapseDate != null) p[LAST_RELAPSE] = next.lastRelapseDate
            else p.remove(LAST_RELAPSE)
            p[RELAPSE_COUNT] = next.relapseCount
            p[URGE_COUNT] = next.urgeCount
            p[WARN_INDEX] = next.warnIndex
            p[DMG_INDEX] = next.dmgIndex
            p[WARN_AUTO] = next.warnAuto
            p[DMG_AUTO] = next.dmgAuto
            p[ACTIVE_VIEW] = next.activeView
        }
    }

    suspend fun resetAll() {
        context.appDataStore.edit { it.clear() }
    }

    // ── T-Maxx daily habits — stored as a date-keyed set ──
    private fun tmaxxKey(): String {
        val d = java.util.Calendar.getInstance()
        return "tmaxx_${d.get(java.util.Calendar.YEAR)}_${d.get(java.util.Calendar.MONTH)}_${d.get(java.util.Calendar.DAY_OF_MONTH)}"
    }

    suspend fun toggleHabit(habitId: String): Boolean {
        var added = false
        context.appDataStore.edit { p ->
            val key = stringPreferencesKey(tmaxxKey())
            val raw = p[key] ?: ""
            val set = if (raw.isBlank()) mutableSetOf() else raw.split(",").toMutableSet()
            if (habitId in set) {
                set.remove(habitId)
                added = false
            } else {
                set.add(habitId)
                added = true
            }
            p[key] = set.joinToString(",")
        }
        return added
    }

    fun todayHabitIds(): Flow<Set<String>> = context.appDataStore.data.map { p ->
        val key = stringPreferencesKey(tmaxxKey())
        val raw = p[key] ?: ""
        if (raw.isBlank()) emptySet() else raw.split(",").toSet()
    }
}

data class AppState(
    val startDate: String? = null,
    val lastRelapseDate: String? = null,
    val relapseCount: Int = 0,
    val urgeCount: Int = 0,
    val warnIndex: Int = 0,
    val dmgIndex: Int = 0,
    val warnAuto: Boolean = true,
    val dmgAuto: Boolean = true,
    val activeView: String = "dashboard"
)
