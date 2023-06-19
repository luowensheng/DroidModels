package com.luowensheng.droid_models.models



interface Query<T> {
    fun isFilter(): Boolean
    fun limit(): Query<T>
    fun orderBy(orderBy: OrderBy = OrderBy.DESCENDING): Query<T>
    fun orderBy(orderBy: Column<T, *>): Query<T>
    fun getFirst(): Query<T>
    fun toList(): Query<T>
    fun and(query: Query<T>): Query<T>
    fun or(query: Query<T>): Query<T>
    fun select(vararg column: Column<T, *>): Query<T>
    fun getFieldsToSelect(): List<String>?
}


