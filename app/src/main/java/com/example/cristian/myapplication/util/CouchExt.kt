package com.example.cristian.myapplication.util

import com.couchbase.lite.ArrayFunction
import com.couchbase.lite.Expression
import com.couchbase.lite.Function
import com.couchbase.lite.Ordering
import java.util.*

// EQUAL TO

infix fun String.equalEx(value: Any): Expression = Expression.property(this)
        .equalTo(makeExpressionValue(value))

// GREATER THAN

infix fun String.gt(value: Any): Expression = Expression.property(this)
        .greaterThan(makeExpressionValue(value))

// GREATER THAN EQUAL

infix fun String.gte(value: Any): Expression = Expression.property(this)
        .greaterThanOrEqualTo(makeExpressionValue(value))

// LESS THAN

infix fun String.lt(value: Any): Expression = Expression.property(this)
        .lessThan(makeExpressionValue(value))

// LESS THAN EQUAL

infix fun String.lte(value: Any): Expression = Expression.property(this)
        .lessThanOrEqualTo(makeExpressionValue(value))

// LIKE
infix fun String.likeEx(value: String): Expression = Function.lower(Expression.property(this))
        .like(Expression.string(value.toLowerCase()))

// LOGICAL
infix fun Expression.andEx(expression: Expression): Expression = this.and(expression)

infix fun Expression.orEx(expression: Expression): Expression = this.or(expression)

infix fun Expression.betweenEx(expressions: List<Expression>) =
        this.between(expressions[0], expressions[1])

// REGEX

infix fun String.regexEx(value: String): Expression = Expression.property(this)
        .regex(Expression.string(value))

// Array
infix fun String.inEx(values: List<Any>): Expression =
        Expression.property(this).`in`(*makeExpressionArray(values).toTypedArray())

infix fun String.containsEx(value: Any): Expression =
        ArrayFunction.contains(Expression.property(this), makeExpressionValue(value))


// Make Expression
private fun makeExpressionValue(value: Any): Expression = when (value) {
    is String -> Expression.string(value)
    is Boolean -> Expression.booleanValue(value)
    is Int -> Expression.intValue(value)
    is Long -> Expression.longValue(value)
    is Date -> Expression.date(value)
    is Double -> Expression.doubleValue(value)
    is Float -> Expression.floatValue(value)
    else -> Expression.value(value)
}

private fun makeExpressionArray(values: List<Any>): List<Expression> {
    val exps: MutableList<Expression> = mutableListOf()
    values.forEach { exps.add(makeExpressionValue(it)) }
    return exps
}

fun String.betweenDates(from:Date, to:Date):Expression = Expression.property(this).between(Expression.date(from), Expression.date(to))

infix fun String.orderEx(ordering: Int): Ordering = when(ordering){
    DESCENDING -> Ordering.property(this).descending()
    else -> Ordering.property(this).ascending()
}
const val DESCENDING = 0
const val ASCENDING = 1