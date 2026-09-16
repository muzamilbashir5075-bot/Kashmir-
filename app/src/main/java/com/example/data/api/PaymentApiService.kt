package com.example.data.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// These data classes represent the payloads for a real payment gateway integration.
data class PaymentOrderRequest(val amount: Double, val currency: String, val method: String)
data class PaymentOrderResponse(val gatewayOrderId: String, val status: String)

data class PaymentVerifyRequest(val gatewayOrderId: String, val transactionId: String, val signature: String)
data class PaymentVerifyResponse(val verified: Boolean, val status: String)

data class PayoutRequestDto(val sellerId: Long, val amount: Double, val method: String, val accountDetails: String)
data class PayoutResponseDto(val payoutId: String, val status: String)

data class RefundRequestDto(val transactionId: String, val amount: Double, val reason: String)
data class RefundResponseDto(val refundId: String, val status: String)

interface PaymentApiService {
    @POST("/api/v1/payments/create-order")
    suspend fun createPaymentOrder(@Body request: PaymentOrderRequest): PaymentOrderResponse

    @POST("/api/v1/payments/verify")
    suspend fun verifyPayment(@Body request: PaymentVerifyRequest): PaymentVerifyResponse

    @POST("/api/v1/payouts/request")
    suspend fun requestPayout(@Body request: PayoutRequestDto): PayoutResponseDto
    
    @POST("/api/v1/refunds/initiate")
    suspend fun initiateRefund(@Body request: RefundRequestDto): RefundResponseDto
}
