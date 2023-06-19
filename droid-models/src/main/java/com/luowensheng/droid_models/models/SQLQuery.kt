package com.luowensheng.droid_models.models

class SQLQuery<T>(baseQuery: String, private val isFilter: Boolean): Query<T> {

    private val queries = mutableListOf(baseQuery)
    private val toSelect = mutableListOf<String>()

    override fun isFilter(): Boolean {
        return isFilter
    }

    override fun limit(): Query<T> {
        queries.add("LIMIT 1")
        return this
    }

    override fun orderBy(orderBy: OrderBy): Query<T> {
        queries.add("ORDER BY $orderBy")
        return this
    }

    override fun orderBy(orderBy: Column<T, *>): Query<T> {
        queries.add("ORDER BY ${orderBy.name}")
        return this
    }

    override fun getFirst(): Query<T> {
        return limit()
    }

    override fun toList(): Query<T> {
        return this
    }

    override fun getFieldsToSelect(): List<String>? {
        if (toSelect.size == 0){
            return null
        }
        return toSelect
    }

    override fun and(query: Query<T>): Query<T> {
        var qs = "$query"
        if (qs.startsWith("WHERE")){
            qs = qs.substring(6)
        }
        queries.add("AND ($qs)")
        return this
    }

    override fun toString(): String {
        return queries.joinToString(" ")
    }

    override fun or(query: Query<T>): Query<T> {
        queries.add("OR $query")
        return this
    }

    override fun select(vararg column: Column<T, *>): Query<T> {
        column.forEach { toSelect.add(it.name) }
        return  this
    }
}