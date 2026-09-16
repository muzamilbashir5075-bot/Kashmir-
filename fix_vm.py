import re

with open("app/src/main/java/com/example/ui/MarketViewModel.kt", "r") as f:
    lines = f.readlines()

with open("patch_viewmodel_payout.kt", "r") as f:
    payout_code = f.read()

update_order_code = """
    fun updateOrderStatus(orderId: Long, newStatus: String) {
        viewModelScope.launch {
            val order = dao.getOrderById(orderId)
            if (order != null) {
                var payStatus = order.paymentStatus
                if (newStatus == "Delivered" && order.paymentMethod == "COD") {
                    payStatus = "COD Collected"
                }
                dao.updateOrder(order.copy(orderStatus = newStatus, paymentStatus = payStatus))
            }
        }
    }
"""

new_lines = []
skip = False
for i, line in enumerate(lines):
    if "fun updateOrderStatus(orderId: Long, newStatus: String) {" in line:
        skip = True
        new_lines.append(payout_code)
        new_lines.append(update_order_code)
        continue
    
    if skip and "fun cancelOrder(orderId: Long) {" in line:
        skip = False
        new_lines.append(line)
        continue
        
    if not skip:
        new_lines.append(line)

with open("app/src/main/java/com/example/ui/MarketViewModel.kt", "w") as f:
    f.writelines(new_lines)
