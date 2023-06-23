package com.luowensheng.droid_models.models

import android.util.Log
import java.lang.reflect.Field
import kotlin.reflect.KClass

data class Attribute(private val field: Field){

    val isStatic = !field.name.contains("$")

    private val type: KClass<*> = when(field.type.name){
        Byte::class.java.name -> Byte::class
        ByteArray::class.java.name -> ByteArray::class
        Double::class.java.name -> Double::class
        Int::class.java.name -> Int::class
        Long::class.java.name -> Long::class
        Boolean::class.java.name -> Boolean::class
        Short::class.java.name -> Short::class
        Float::class.java.name -> Float::class
        String::class.java.name -> String::class
        else -> Any::class
    }

    fun < U:Any>get(obj: Any): U {
        return field.get(obj) as U
    }

    fun getType(): KClass<*> {
        return type
    }

    fun <T: Any>set(obj: T, newValue: T) {
        field.set(obj, newValue)
    }
}
