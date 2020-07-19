package com.example.risogelato.data.local

import android.content.Context
import androidx.core.content.edit
import java.lang.IllegalArgumentException

private const val SHARED_PREFERENCES_NAME = "RisoGelatoSPF"

class SharedPreferencesManager(context: Context) {

    private val spf = context.getSharedPreferences(SHARED_PREFERENCES_NAME, 0)

    fun put(key: String, value: Any?) {
        spf.edit {
            when (value) {
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
                is Boolean -> putBoolean(key, value)
                is String? -> putString(key, value)
                is Set<*> -> putStringSet(key, value.map { it.toString() }.toSet())
                null -> throw IllegalArgumentException("Illegal nullable type for key \"$key\"")
                else -> {
                    val valueType = value.javaClass.canonicalName
                    throw IllegalArgumentException("Illegal value type $valueType for key \"$key\"")
                }
            }
        }
    }

    fun getInt(key: String, default: Int) = spf.getInt(key, default)

    fun getLong(key: String, default: Long) = spf.getLong(key, default)

    fun getBoolean(key: String, default: Boolean) = spf.getBoolean(key, default)

    fun getString(key: String, default: String? = null): String? = spf.getString(key, default)

    fun getStringSet(key: String, default: Set<String> = hashSetOf()): MutableSet<String>
            = spf.getStringSet(key, default) ?: hashSetOf()

    fun contains(key: String) = spf.contains(key)

}
