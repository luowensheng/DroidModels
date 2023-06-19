package com.luowensheng.droid_models.models

abstract class Column<T, K>(val name: String){
    abstract fun where(compare: Compare, value: K): Query<T>

    fun isEqualTo(value: K): Query<T> {
        return where(Compare.EQUAL, value)
    }
    fun isLessThan(value: K): Query<T> {
        return where(Compare.LESS_THAN, value)
    }
    fun isLessThanOrEqualTo(value: K): Query<T> {
        return where(Compare.LESS_THAN_OR_EQ, value)
    }
    fun isGreaterThan(value: K): Query<T> {
        return where(Compare.GREATER, value)
    }
    fun isGreaterOrEqualTo(value: K): Query<T> {
        return where(Compare.GREATER_OR_EQ, value)
    }
    fun isNotEqualTo(value: K): Query<T> {
        return where(Compare.NOT_EQUAL, value)
    }
    fun isIn(value: K): Query<T> {
        return where(Compare.IN, value)
    }
    fun isLike(value: K): Query<T> {
        return where(Compare.LIKE, value)
    }
    fun isBetween(value: K): Query<T> {
        return where(Compare.BETWEEN, value)
    }

}