package com.example.foxichat.service

import android.util.Log
import androidx.room.Room
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class Converters {
    @TypeConverter
    fun fromString(value: String?): List<String> {
        val gson = Gson()
        val listType: Type = object : TypeToken<List<String>?>() {}.type

        println(value)

        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<String?>?): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}