package com.example.mvr.core.ktx

import com.example.mvr.core.net.adpter.IntTypeGsonTypeAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

val gsonInstance: Gson by lazy {
    buildGson()
}

private fun buildGson(): Gson {
    val gsonBuilder = GsonBuilder()
    //gsonBuilder.registerTypeAdapter(object : TypeToken<Map<String, @JvmSuppressWildcards Any>>() {}.type, GsonTypeAdapter())
    //gsonBuilder.registerTypeAdapter(object : TypeToken<List<@JvmSuppressWildcards Any>>() {}.type, GsonTypeAdapter())
    gsonBuilder.registerTypeAdapter(object : TypeToken<Number>() {}.type, IntTypeGsonTypeAdapter())
    // gsonBuilder.registerTypeAdapter(object : TypeToken<Int>() {}.type, IntTypeGsonTypeAdapter())
    //gsonBuilder.registerTypeAdapterFactory(ObjectTypeAdapter.FACTORY)
    return gsonBuilder.create()
}

fun <T> T.toJsonTxt(): String {
    return gsonInstance.toJson(this)
}

/**
 * 如果输入为 text isBlank 会返回 null 对象
 *
 * @receiver String
 * @return T
 */
inline fun <reified T> String.toObj(): T {
    val type = object : TypeToken<T>() {}.type
    return gsonInstance.fromJson(this, type)
    //val clazz = T::class.java
    //return gsonInstance.fromJson(this, clazz)
}

inline fun <reified T> String.toObjOrNull(): T? {
    return try {
        val type = object : TypeToken<T>() {}.type
        gsonInstance.fromJson(this, type)
    } catch (e: Exception) {
        null
    }
    //val clazz = T::class.java
    //return gsonInstance.fromJson(this, clazz)
}
