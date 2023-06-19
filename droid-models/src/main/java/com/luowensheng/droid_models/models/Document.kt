package com.luowensheng.droid_models.models

import kotlin.properties.Delegates
import kotlin.reflect.KClass



fun <T: Any> KClass<T>.getFields(): List<Pair<String, Attribute>> {
    return this.java.declaredFields.map {
        it.isAccessible = true
        val name = it.name.split("$")[0]
        name to Attribute(it)
    }
}


abstract class Document {
    var id by Delegates.notNull<Long>()
    private var fields: Map<String, Attribute> = mutableMapOf()

    init {
        fields = this::class.getFields().filter {
            !it.second.isStatic
        }.associate {  it }
    }


    fun getMemberNames(): List<String> {
        val fieldList = mutableListOf<String>()
        for (field in fields.keys){
            fieldList.add(field)
        }
        return fieldList
    }

    @Throws(ClassCastException::class)
    fun <T: Any>getMember(name: String): T? {
        val attr = fields[name] ?: return null
        return attr.get(this)
    }

    @Throws(ClassCastException::class)
    fun <T: Document>setMember(name: String, value: T) {
        if (fields.containsKey(name)){
            val attr = fields[name] ?: return
            attr.set(this, value)
        }
    }

}