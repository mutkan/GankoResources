package com.example.cristian.myapplication.data.db

import com.couchbase.lite.*
import com.example.cristian.myapplication.data.preferences.UserSession
import com.example.cristian.myapplication.util.andEx
import com.example.cristian.myapplication.util.equalEx
import com.fasterxml.jackson.databind.ObjectMapper
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlin.reflect.KClass


@Singleton
class CouchRx @Inject constructor(private val db: Database
                                  , @Named("dbName") private val dbName: String
                                  , private val folder: File
                                  , private val session: UserSession
                                  , private val mapper: ObjectMapper) {


    fun <T : Any> insert(doc: T): Single<String> = Single.create {
        val document = objectToDocument(doc)
        document.setArray("channels", MutableArray().addString(session.userId))
        db.save(document)
        it.onSuccess(document.id)
    }

    fun <T : Any> update(id: String, doc: T): Single<Unit> = Single.create {
        val document = objectToDocument(doc, id)
        db.save(document)
        it.onSuccess(Unit)
    }

    fun remove(id: String): Single<Unit> = Single.create {
        val doc = db.getDocument(id)
        db.delete(doc)
        it.onSuccess(Unit)
    }

    fun <T : Any> oneById(id: String, kClass: KClass<T>): Maybe<T> = Maybe.create {
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


    fun <T : Any> oneByExp(expression: Expression, kClass: KClass<T>): Maybe<T> = Single.create<ResultSet> {
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

    fun <T : Any> listByExp(expression: Expression, kClass: KClass<T>, limit: Int? = null, skip: Int? = null, orderBy:Array<Ordering> = emptyArray()): Single<List<T>> = Single.create<ResultSet> {
        val query = QueryBuilder
                .select(SelectResult.all(), SelectResult.expression(Meta.id), SelectResult.expression(Meta.sequence))
                .from(DataSource.database(db))
                .where(expression andEx ("type" equalEx kClass.simpleName.toString()))
                .orderBy(*orderBy)


        val result: ResultSet = if (limit != null && skip != null)
            query.limit(Expression.intValue(limit), Expression.intValue(skip)).execute()
        else if(limit!=null)
            query.limit(Expression.intValue(limit)).execute()
        else query.execute()

        it.onSuccess(result)
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


    fun <T : Any> listByExpNoType(expression: Expression, kClass: KClass<T>, limit: Int? = null, skip: Int? = null, orderBy:Array<Ordering> = emptyArray()): Single<List<T>> = Single.create<ResultSet> {
        val query = QueryBuilder
                .select(SelectResult.all(), SelectResult.expression(Meta.id), SelectResult.expression(Meta.sequence))
                .from(DataSource.database(db))
                .where(expression)
                .orderBy(*orderBy)


        val result: ResultSet = if (limit != null && skip != null)
            query.limit(Expression.intValue(limit), Expression.intValue(skip)).execute()
        else if(limit!=null)
            query.limit(Expression.intValue(limit)).execute()
        else query.execute()

        it.onSuccess(result)
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

    fun ListObsByQuery(query: Query): Observable<ResultSet> = Observable.create { emitter ->
        query.addChangeListener {
            if (it.error == null) emitter.onNext(it.results)
            else emitter.onError(it.error)
        }
    }

    fun <T : Any> listObsByExp(expression: Expression, kClass: KClass<T>): Observable<List<T>> = Observable.create<ResultSet> { emitter ->
        val query = QueryBuilder
                .select(SelectResult.all(), SelectResult.expression(Meta.id), SelectResult.expression(Meta.sequence))
                .from(DataSource.database(db))
                .where(expression andEx ("type" equalEx kClass.simpleName.toString()))
        query.addChangeListener {
            if (it.error == null) emitter.onNext(it.results)
            else emitter.onError(it.error)

        }
    }
            .flatMap { result ->
                result.toObservable()
                        .map {
                            val id = it.getString("id")
                            val sequence = it.getLong("sequence")
                            val content = it.getDictionary(dbName)
                            Triple(id, sequence, content)
                        }
                        .map { dictionaryToObject(it.first, it.second, it.third, kClass) }
                        .toList().toObservable()

            }

    fun getFile(id: String, name: String): Maybe<File> {

        val file = File(folder, name)
        return if (file.exists()) {
            Maybe.just(file)
        } else {
            Maybe.create<InputStream> {
                try {
                    val doc = db.getDocument(id)
                    val files:Dictionary? = doc.getDictionary("files")
                    val blob:Blob? = files?.getBlob(name)
                    val input = blob?.contentStream
                    if (input != null && blob != null) it.onSuccess(input)
                    else it.onComplete()
                } catch (e: Exception) {
                    it.onError(e)
                }
            }
                    .flatMap {
                        val outStream = FileOutputStream(file)
                        val buffer = ByteArray(8 * 1024)
                        var bytesRead: Int = it.read(buffer)
                        while (bytesRead != -1) {
                            outStream.write(buffer, 0, bytesRead)
                            bytesRead = it.read(buffer)
                        }
                        outStream.close()
                        it.close()
                        Maybe.just(file)
                    }

        }
    }

//    fun removeBlob(id:String, field:String) =

    fun putBlob(id: String, name: String, contentType: String, path: String): Single<Pair<String, String>> {
        val file = File(path)
        return putBlobAny(id, name, contentType, FileInputStream(file))
    }

    fun putBlob(id: String, name: String, contentType: String, file: File): Single<Pair<String, String>> =
            putBlobAny(id, name, contentType, FileInputStream(file))

    fun putBlob(id: String, name: String, contentType: String, input: InputStream): Single<Pair<String, String>> =
            putBlobAny(id, name, contentType, input)

    fun putBlob(id: String, name: String, contentType: String, bytes: ByteArray): Single<Pair<String, String>> =
            putBlobAny(id, name, contentType, bytes)

    fun putBlob(id: String, name: String, contentType: String, url: URL): Single<Pair<String, String>> =
            putBlobAny(id, name, contentType, url)

    private fun putBlobAny(id: String, name: String, contentType: String, data: Any): Single<Pair<String, String>> =
            Single.create {
                try {
                    val document = db.getDocument(id).toMutable()
                    val files:MutableDictionary = document.getDictionary("files") ?: MutableDictionary()

                    val blob = when (data) {
                        is InputStream -> Blob(contentType, data)
                        is ByteArray -> Blob(contentType, data)
                        is URL -> Blob(contentType, data)
                        else -> null
                    }

                    files.setBlob(name, blob)
                    document.setDictionary("files", files)
                    db.save(document)
                    (data as? InputStream)?.close()
                    it.onSuccess(id to name)
                } catch (e: Exception) {
                    it.onError(e)
                }
            }


    private fun objectToDocument(obj: Any, id: String? = null): MutableDocument {
        val map = mapper.convertValue(obj, Map::class.java) as MutableMap<String, Any>
        if (map.contains("_id")) map.remove("_id")
        if (map.contains("_sequence") && id == null) map.remove("_sequence")
        return if (id != null) MutableDocument(id, map) else MutableDocument(map)
    }

    private fun objectToMap(obj: Any): Map<String, Any> {
        val map = mapper.convertValue(obj, Map::class.java) as MutableMap<String, Any>
        map.remove("_id")
        return map
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

    fun <T : Any> insertDosisUno(doc: T): Single<String> = Single.create {
        val document = objectToDocument(doc)
        document.setArray("channels", MutableArray().addString(session.userId))
        document.setString("idDosisUno", document.id)
        db.save(document)
        it.onSuccess(document.id)
    }



}