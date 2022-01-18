package com.example.examer.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher

/**
 * An interface that defines the different types of dispatchers used
 * with a coroutine.
 * Use [StandardDispatchersProvider] to get an implementation of
 * [DispatcherProvider] that contains the default coroutine
 * dispatchers. By depending on an interface instead of the
 * dispatchers directly, it becomes possible to switch out all
 * dispatchers with test dispatchers when running tests.
 */
interface DispatcherProvider {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
}

/**
 * A concrete implementation of [DispatcherProvider] that contains the
 * default coroutine dispatchers.
 */
data class StandardDispatchersProvider(
    override val main: MainCoroutineDispatcher = Dispatchers.Main,
    override val io: CoroutineDispatcher = Dispatchers.IO,
    override val default: CoroutineDispatcher = Dispatchers.Default,
    override val unconfined: CoroutineDispatcher = Dispatchers.Unconfined
) : DispatcherProvider
