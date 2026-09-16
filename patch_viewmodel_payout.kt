    fun getPayoutRequestsFlow(): Flow<List<PayoutRequestEntity>> {
        val sellerId = _currentUser.value?.id ?: return kotlinx.coroutines.flow.flowOf(emptyList())
        return dao.getPayoutRequestsFlow(sellerId)
    }

    fun requestPayout(amount: Double, method: String, accountDetails: String, onComplete: (Boolean, String) -> Unit) {
        val sellerId = _currentUser.value?.id ?: return
        viewModelScope.launch {
            try {
                // Network call to backend
                val response = com.example.data.api.RetrofitClient.paymentApi.requestPayout(
                    com.example.data.api.PayoutRequestDto(sellerId, amount, method, accountDetails)
                )
                // If it succeeds (it shouldn't):
                dao.insertPayoutRequest(
                    PayoutRequestEntity(
                        sellerId = sellerId,
                        amount = amount,
                        status = "Processing",
                        payoutMethod = method,
                        accountDetails = accountDetails,
                        transactionId = response.payoutId
                    )
                )
                onComplete(true, "Payout requested successfully!")
            } catch (e: Exception) {
                // Record the failed attempt as well so it doesn't just disappear, or fail outright
                dao.insertPayoutRequest(
                    PayoutRequestEntity(
                        sellerId = sellerId,
                        amount = amount,
                        status = "Failed",
                        payoutMethod = method,
                        accountDetails = accountDetails,
                        transactionId = ""
                    )
                )
                onComplete(false, "Payout gateway unreachable. Request failed.")
            }
        }
    }
