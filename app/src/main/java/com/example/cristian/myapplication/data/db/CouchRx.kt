package com.example.cristian.myapplication.data.db

import com.couchbase.lite.*
import com.couchbase.lite.Dictionary
import com.example.cristian.myapplication.data.preferences.UserSession
import com.example.cristian.myapplication.util.andEx
import com.example.cristian.myapplication.util.equalEx
import com.fasterxml.jackson.databind.ObjectMapper
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlin.reflect.KClass

@Singleton
class CouchRx @Inject constructor(private val db: Database
                                        , @Named("dbName") private val dbName: String
                                        , private val session: UserSession
                                        , private val mapper: ObjectMapper) {


    fun <T : CouchEntity> insert(doc: T): Single<String> = Single.create {
        val id = "${session.userId}.${UUID.randomUUID()}"
        val document = objectToDocument(doc, id)
        db.save(document)
        it.onSuccess(id)

    }

    fun <T : CouchEntity> insertGenId(doc: T): Single<String> = Single.create {
        val id = "${UUID.randomUUID()}"
        val document = objectToDocument(doc, id)
        db.save(document)
        it.onSuccess(id)
    }

    fun <T : CouchEntity> update(id: String, doc: T): Single<Unit> = Single.create {
        val document = objectToDocument(doc, id)
        db.save(document)
        it.onSuccess(Unit)
    }

    fun remove(id: String): Single<Unit> = Single.create {
        val doc = db.getDocument(id)
        db.delete(doc)
        it.onSuccess(Unit)
    }

    fun <T : CouchEntity> oneById(id: String, kClass: KClass<T>): Maybe<T> = Maybe.create {
        val doc = db.getDocument(id)
        if (doc != null) {
            val map = doc.toMap()
            val obj: T = mapToObject(doc.id, doc.sequence, map, kClass)
            it.onSuccess(obj)
        } else it.onComplete()
    }

    fun oneByQuery(query: Query): Maybe<Result> =
            Single.create<ResultSet> { it.onSuccess(query.execute()) }
                    .flatMapObservable { it.toObservable() }
                    .firstElement()


    fun <T : CouchEntity> oneByExp(expression: Expression, kClass: KClass<T>): Maybe<T> = Single.create<ResultSet> {
        val query = QueryBuilder
                .select(SelectResult.all(), SelectResult.expression(Meta.id), SelectResult.expression(Meta.sequence))
                .from(DataSource.database(db))
                .where(expression andEx ("type" equalEx kClass.simpleName.toString()))

        it.onSuccess(query.execute())
    }
            .flatMapObservable { it.toObservable() }
            .firstElement()
            .map {
                val id = it.getString("id")
                val sequence = it.getLong("sequence")
                val content = it.getDictionary(dbName)
                Triple(id, sequence, content)
            }
            .map { dictionaryToObject(it.first, it.second, it.third, kClass) }

    fun listByQuery(query: Query): Single<ResultSet> =
            Single.create<ResultSet> { it.onSuccess(query.execute()) }

    fun <T : Any> listByExp(expression: Expression, kClass: KClass<T>): Single<List<T>> = Single.create<ResultSet> {
        val query = QueryBuilder
                .select(SelectResult.all(), SelectResult.expression(Meta.id), SelectResult.expression(Meta.sequence))
                .from(DataSource.database(db))
                .where(expression andEx ("type" equalEx kClass.simpleName.toString()))

        it.onSuccess(query.execute())
    }
            .flatMapObservable { it.toObservable() }
            .map {
                val id = it.getString("id")
                val sequence = it.getLong("sequence")
                val content = it.getDictionary(dbName)
                Triple(id, sequence, content)
            }
            .map { dictionaryToObject(it.first, it.second, it.third, kClass) }
            .toList()

    private fun objectToDocument(obj: Any, id: String): MutableDocument {
        val map = mapper.convertValue(obj, Map::class.java) as MutableMap<String, Any>
        return MutableDocument(id, map)
    }

    private fun objectToMap(obj: Any): Map<String, Any> {
        return mapper.convertValue(obj, Map::class.java) as Map<String, Any>
    }


    private fun <T : Any> dictionaryToObject(id: String, sequence: Long, dictionary: Dictionary, kClass: KClass<T>): T {
        val map = dictionary.toMap()
        map["_id"] = id
        map["_sequence"] = sequence
        return mapper.convertValue(map, kClass.java)
    }


    private fun <T : Any> mapToObject(id: String, sequence: Long, map: MutableMap<String, Any>, kClass: KClass<T>): T {
        map["_id"] = id
        map["_sequence"] = sequence
        return mapper.convertValue(map, kClass.java)
    }


}