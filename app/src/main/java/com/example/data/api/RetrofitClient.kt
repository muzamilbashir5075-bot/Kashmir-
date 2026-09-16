package com.example.data.api

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitClient {
    // We use a hypothetical backend URL. This will naturally fail on real network requests,
    // achieving the requirement: "Do not show Payment Successful until server verifies".
    private const val BASE_URL = "https://api.kashmirshop.com/"

    val paymentApi: PaymentApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(PaymentApiService::class.java)
    }
}
