package com.example.droidmodels.models

import android.util.Log
import java.lang.reflect.Field
import kotlin.reflect.KClass

data class Attribute(private val field: Field){

    val isStatic = !field.name.contains("$")
    init {  if (!isStatic)  println("${field.name}$ ^^^^^ ${field.name.contains("$")}")  }

    private val type: KClass<*> = when(field.type.name){
        "byte" -> Byte::class
        "[B", -> ByteArray::class
        "double" -> Double::class
        "int" -> Int::class
        "long" -> Long::class
        "boolean" -> Boolean::class
        "short" -> Short::class
        "float" -> Float::class
        "java.lang.String"-> String::class
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
