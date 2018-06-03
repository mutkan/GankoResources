package com.example.cristian.myapplication.di.modules

import android.content.Context
import com.couchbase.lite.Database
import com.couchbase.lite.DatabaseConfiguration
import com.couchbase.lite.IndexBuilder
import com.couchbase.lite.ValueIndexItem
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import dagger.Module
import dagger.Provides
import java.io.File
import javax.inject.Named
import javax.inject.Singleton

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
    @Singleton
    fun provideFolderName(context: Context): File = context.filesDir


    @Provides
    fun provideMapper() : ObjectMapper {
      val mapper = ObjectMapper().registerKotlinModule()
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        return mapper
    }


}
