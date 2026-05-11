package com.kevintcoughlin.smodr

import android.app.Application
import com.kevintcoughlin.smodr.network.NetworkRepository
import com.kevintcoughlin.smodr.network.RetrofitClient

/**
 * Manual dependency injection container for Smodr.
 *
 * Holds the single instances of shared components so that Activities,
 * Fragments, and Services can obtain them from `(application as SmodrApplication).container`
 * rather than constructing their own copies.
 *
 * The [app] context is retained for future context-dependent dependencies
 * (e.g. Room database, OkHttp disk cache directory).
 */
@Suppress("unused")
class AppContainer(private val app: Application) {
    val networkRepository: NetworkRepository = NetworkRepository(RetrofitClient.apiService)
}
