package com.specindia.ecommerce.di

import android.content.Context
import com.specindia.ecommerce.BuildConfig
import com.specindia.ecommerce.api.EcommerceApiService
import com.specindia.ecommerce.datastore.abstraction.DataStoreRepository
import com.specindia.ecommerce.datastore.implementation.DataStoreRepositoryImpl
import com.specindia.ecommerce.util.Constants.Companion.CONNECTION_TIME_OUT
import com.specindia.ecommerce.util.Constants.Companion.READ_TIME_OUT
import com.specindia.ecommerce.util.Constants.Companion.WRITE_TIME_OUT
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Module
    @InstallIn(SingletonComponent::class)
    object NetworkModule {
        @Singleton
        @Provides
        fun provideHttpClient(): OkHttpClient {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            when {
                BuildConfig.DEBUG -> {
                    return OkHttpClient
                        .Builder()
                        .addInterceptor(loggingInterceptor)
                        .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
                        .connectTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS)
                        .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
                        .build()
                }
                else -> {
                    return OkHttpClient
                        .Builder()
                        .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
                        .connectTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS)
                        .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
                        .build()
                }
            }
        }

        @Singleton
        @Provides
        fun provideConverterFactory(): GsonConverterFactory =
            GsonConverterFactory.create()

        @Singleton
        @Provides
        fun provideRetrofit(
            okHttpClient: OkHttpClient,
            gsonConverterFactory: GsonConverterFactory
        ): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(gsonConverterFactory)
                .build()
        }

        @Singleton
        @Provides
        fun provideECommService(retrofit: Retrofit): EcommerceApiService =
            retrofit.create(EcommerceApiService::class.java)

        @Singleton
        @Provides
        fun provideDataStoreRepository(
            @ApplicationContext app: Context
        ): DataStoreRepository = DataStoreRepositoryImpl(app)
    }
}