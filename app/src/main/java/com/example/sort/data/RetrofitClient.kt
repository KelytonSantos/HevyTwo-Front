package com.example.sort.data

import android.content.Context
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.1.13:8080"

    fun getInstance(context: Context): ApiService {
        val sessionManager = SessionManager(context)

        val authInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            
            // Usando as propriedades .url e .encodedPath (acesso via val, sem parênteses)
            if (originalRequest.url.encodedPath.contains("/auth/login")) {
                return@Interceptor chain.proceed(originalRequest)
            }

            val token = runBlocking {
                sessionManager.jwtToken.first()
            }
            
            val requestBuilder = originalRequest.newBuilder()
            if (token != null) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
            chain.proceed(requestBuilder.build())
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
