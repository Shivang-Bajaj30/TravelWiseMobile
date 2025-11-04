package com.example.travelwise.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FavoritesManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("favorites_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun addFavorite(destinationId: Int) {
        val favorites = getFavorites().toMutableSet()
        favorites.add(destinationId)
        saveFavorites(favorites)
    }

    fun removeFavorite(destinationId: Int) {
        val favorites = getFavorites().toMutableSet()
        favorites.remove(destinationId)
        saveFavorites(favorites)
    }

    fun isFavorite(destinationId: Int): Boolean {
        return getFavorites().contains(destinationId)
    }

    fun getFavorites(): Set<Int> {
        val favoritesJson = sharedPreferences.getString("favorites", "[]")
        val type = object : TypeToken<Set<Int>>() {}.type
        return gson.fromJson(favoritesJson, type) ?: emptySet()
    }

    private fun saveFavorites(favorites: Set<Int>) {
        val favoritesJson = gson.toJson(favorites)
        sharedPreferences.edit().putString("favorites", favoritesJson).apply()
    }
}