package com.luowensheng.droid_models.models

import java.util.Optional

interface Model<T : Document> {

    fun getName(): String
    fun get(id: String): Optional<T>
    fun delete(id: Long): Boolean
    fun delete(ids: List<Long>): Boolean
    fun add(item: T): Boolean
    fun getAll(): Optional<List<T>>
    fun fromMap(data: Map<String, Any?>): T
    fun toMap(item: T): Map<String, Any?>
    fun<K: Model<T>> K.update(id: String, block: K.() -> Map<Column<T, *>, Any>): Boolean
    fun<K: Model<T>> K.query(block: K.() -> Query<T>): Optional<List<T>>

}