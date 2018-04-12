package com.example.cristian.myapplication.di.modules

import android.content.Context
import com.couchbase.lite.Database
import com.couchbase.lite.DatabaseConfiguration
import com.couchbase.lite.IndexBuilder
import com.couchbase.lite.ValueIndexItem
import com.fasterxml.jackson.databind.ObjectMapper
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class CouchModule{

    @Provides
    @Named("dbName")
    fun provideDataBaseName():String = "ganko-database"

    @Provides
    fun provideDataBase(context:Context, @Named("dbName") name:String):Database{
        val config = DatabaseConfiguration(context)
        val db = Database(name, config)
        db.createIndex("TypeIndex", IndexBuilder.valueIndex(ValueIndexItem.property("type")))
        return db
    }

    @Provides
    fun provideMapper() = ObjectMapper()


}
