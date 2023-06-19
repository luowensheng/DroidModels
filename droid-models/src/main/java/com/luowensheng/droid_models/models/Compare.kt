package com.luowensheng.droid_models.models

enum class OrderBy {
    ASCENDING,
    DESCENDING
}

enum class Compare {
    EQUAL { override fun toString(): String { return "=" } },
    LESS_THAN { override fun toString(): String { return "<" } },
    LESS_THAN_OR_EQ { override fun toString(): String { return "<=" } },
    GREATER { override fun toString(): String { return ">" } },
    GREATER_OR_EQ { override fun toString(): String { return ">=" } },
    NOT_EQUAL { override fun toString(): String { return "!=" } },
    IN { override fun toString(): String { return "IN" } },
    LIKE { override fun toString(): String { return "LIKE" } },
    BETWEEN { override fun toString(): String { return "BETWEEN" } },
}