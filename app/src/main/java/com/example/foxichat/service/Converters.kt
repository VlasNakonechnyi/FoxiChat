package com.example.foxichat.service

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

/* TODO NOTE: There is no need to have a converter as there is no lists or other unsupported by
    SQLite data types in your DTO objects */
class Converters {
    @TypeConverter
    fun fromString(value: String?): List<String> {
        val gson = Gson()
        val listType: Type = object : TypeToken<List<String>?>() {}.type


        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<String?>?): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}