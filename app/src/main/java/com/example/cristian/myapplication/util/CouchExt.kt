package com.example.cristian.myapplication.util

import com.couchbase.lite.Expression
import java.util.*

// EQUAL TO

infix fun String.equalEx(value: String): Expression = Expression.property(this)
        .equalTo(Expression.string(value))

infix fun String.equalEx(value: Boolean): Expression = Expression.property(this)
        .equalTo(Expression.booleanValue(value))

infix fun String.equalEx(value: Int): Expression = Expression.property(this)
        .equalTo(Expression.intValue(value))

infix fun String.equalEx(value: Long): Expression = Expression.property(this)
        .equalTo(Expression.longValue(value))

infix fun String.equalEx(value: Date): Expression = Expression.property(this)
        .equalTo(Expression.date(value))

infix fun String.equalEx(value: Double): Expression = Expression.property(this)
        .equalTo(Expression.doubleValue(value))

infix fun String.equalEx(value: Float): Expression = Expression.property(this)
        .equalTo(Expression.floatValue(value))

// GREATER THAN

infix fun String.gt(value: Int): Expression = Expression.property(this)
        .greaterThan(Expression.intValue(value))

infix fun String.gt(value: Long): Expression = Expression.property(this)
        .greaterThan(Expression.longValue(value))

infix fun String.gt(value: Date): Expression = Expression.property(this)
        .greaterThan(Expression.date(value))

infix fun String.gt(value: Double): Expression = Expression.property(this)
        .greaterThan(Expression.doubleValue(value))

infix fun String.gt(value: Float): Expression = Expression.property(this)
        .greaterThan(Expression.floatValue(value))

// GREATER THAN EQUAL

infix fun String.gte(value: Int): Expression = Expression.property(this)
        .greaterThanOrEqualTo(Expression.intValue(value))

infix fun String.gte(value: Long): Expression = Expression.property(this)
        .greaterThanOrEqualTo(Expression.longValue(value))

infix fun String.gte(value: Date): Expression = Expression.property(this)
        .greaterThanOrEqualTo(Expression.date(value))

infix fun String.gte(value: Double): Expression = Expression.property(this)
        .greaterThanOrEqualTo(Expression.doubleValue(value))

infix fun String.gte(value: Float): Expression = Expression.property(this)
        .greaterThanOrEqualTo(Expression.floatValue(value))

// LESS THAN

infix fun String.lt(value: Int): Expression = Expression.property(this)
        .lessThan(Expression.intValue(value))

infix fun String.lt(value: Long): Expression = Expression.property(this)
        .lessThan(Expression.longValue(value))

infix fun String.lt(value: Date): Expression = Expression.property(this)
        .lessThan(Expression.date(value))

infix fun String.lt(value: Double): Expression = Expression.property(this)
        .lessThan(Expression.doubleValue(value))

infix fun String.lt(value: Float): Expression = Expression.property(this)
        .lessThan(Expression.floatValue(value))

// LESS THAN EQUAL

infix fun String.lte(value: Int): Expression = Expression.property(this)
        .lessThanOrEqualTo(Expression.intValue(value))

infix fun String.lte(value: Long): Expression = Expression.property(this)
        .lessThanOrEqualTo(Expression.longValue(value))

infix fun String.lte(value: Date): Expression = Expression.property(this)
        .lessThanOrEqualTo(Expression.date(value))

infix fun String.lte(value: Double): Expression = Expression.property(this)
        .lessThanOrEqualTo(Expression.doubleValue(value))

infix fun String.lte(value: Float): Expression = Expression.property(this)
        .lessThanOrEqualTo(Expression.floatValue(value))

// LIKE

infix fun String.likeEx(value: String): Expression = Expression.property(this)
        .like(Expression.string(value))


infix fun Expression.andEx(expression: Expression): Expression = this.and(expression)

infix fun Expression.orEx(expression: Expression): Expression = this.and(expression)

infix fun Expression.betweenEx(expressions:List<Expression>) =
        this.between(expressions[0], expressions[1])

// IS NULL
fun String.equalNull(): Expression = Expression.property(this)
        .isNullOrMissing

//IS NOT NULL
fun String.equalNotNull():Expression = Expression.property(this)
        .notNullOrMissing()
