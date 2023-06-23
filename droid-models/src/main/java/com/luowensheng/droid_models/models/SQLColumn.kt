package com.luowensheng.droid_models.models

import kotlin.reflect.KMutableProperty1
import kotlin.reflect.jvm.jvmErasure


open class SQLColumn<T, K: Any>(
    name: String,
    property: KMutableProperty1<T, K>,
    val primaryKey: Boolean = false,
    val unique: Boolean = false,

    ): Column<T, K>(name) {

    constructor(
        property: KMutableProperty1<T, K>,
        primaryKey: Boolean = false,
        unique: Boolean = false,
    ) : this(property.name, property, primaryKey, unique)

    private val kClass = property.getter.returnType.jvmErasure.java
    val fieldName = property.name
    fun getSQLType(): String {
        return when(kClass){
            Byte::class.java -> "INTEGER"
            ByteArray::class.java  -> "BLOB"
            Double::class.java  -> "REAL"
            Int::class.java  -> "INTEGER"
            Long::class.java  -> "INTEGER"
            Boolean::class.java  -> "NUMERIC"
            Short::class.java  -> "INTEGER"
            Float::class.java  -> "REAL"
            String::class.java  -> "TEXT"
            else -> "TEXT"
        }
    }

    override fun where(compare: Compare, value: K): Query<T> {
        if (value is String){
            return SQLQuery("WHERE $name $compare' $value'", true)
        }
        return SQLQuery("WHERE $name $compare $value", true)
    }
}

class SQLPrimaryColumn<T, K: Any>(name: String, kClass: KMutableProperty1<T, K>): SQLColumn<T, K>(name, kClass, primaryKey = true){

}