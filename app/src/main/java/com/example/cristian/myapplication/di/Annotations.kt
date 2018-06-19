package com.example.cristian.myapplication.di

import android.arch.lifecycle.ViewModel
import dagger.MapKey
import javax.inject.Scope
import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Scope
annotation class ActivityScope

@Retention(AnnotationRetention.RUNTIME)
@Scope
annotation class FragmentScope

@Retention(AnnotationRetention.RUNTIME)
@Scope
annotation class SubFragmentScope

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
internal annotation class ViewModelKey(val value: KClass<out ViewModel>)