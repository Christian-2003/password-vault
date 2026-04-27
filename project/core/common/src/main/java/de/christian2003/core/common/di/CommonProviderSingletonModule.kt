package de.christian2003.core.common.di

import android.content.ClipboardManager
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
internal class CommonProviderSingletonModule {

    @Provides
    @Singleton
    fun provideClipboardManager(
        @ApplicationContext context: Context
    ): ClipboardManager {
        return context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }


    @Provides
    fun provideOkHttpClient(
        @ApplicationContext context: Context
    ): OkHttpClient {
        val cacheSize: Long = 10L * 1024 * 1024 //10 MB
        val cacheDir = File(context.applicationContext.cacheDir, "http_cache")

        return OkHttpClient.Builder()
            .cache(Cache(cacheDir, cacheSize))
            .addNetworkInterceptor { chain ->
                val request: Request = chain.request()
                val response: Response = chain.proceed(request)
                val maxAge = 900 //900 seconds = 15 minutes

                response.newBuilder()
                    .header("Cache-Control", "public, max-age=$maxAge")
                    .build()
            }
            .build()
    }

}
