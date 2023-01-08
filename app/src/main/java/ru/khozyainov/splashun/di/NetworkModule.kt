package ru.khozyainov.splashun.di

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.khozyainov.splashun.data.network.adapters.ItemPhotoListAdapterJSON
import ru.khozyainov.splashun.data.network.adapters.PhotoDetailAdapterJSON
import ru.khozyainov.splashun.data.network.adapters.SearchItemPhotoAdapterJSON
import ru.khozyainov.splashun.data.network.api.PhotoAPI
import ru.khozyainov.splashun.data.network.interceptor.AuthorizationInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://api.unsplash.com/"

    @Provides
    @Singleton
    fun providesOkHttpClient(
        authorizationInterceptor: AuthorizationInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        //Подключение интерцептора для лога запросов TAG: okhttp.OkHttpClient
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .addNetworkInterceptor(authorizationInterceptor)
        .followRedirects(true)
        .build()

    @Provides
    @Singleton
    fun providesRetrofit(okhttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(
            MoshiConverterFactory.create(
                Moshi.Builder()
                    .add(ItemPhotoListAdapterJSON())
                    .add(SearchItemPhotoAdapterJSON(ItemPhotoListAdapterJSON()))
                    .add(PhotoDetailAdapterJSON())
                    .build()
            )
        )
        .client(okhttpClient)
        .build()

    @Provides
    @Singleton
    fun providesNetworkSubredditApi(retrofit: Retrofit): PhotoAPI =
        retrofit.create(PhotoAPI::class.java)
}