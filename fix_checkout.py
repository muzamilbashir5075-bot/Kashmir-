import re

with open("app/src/main/java/com/example/ui/buyer/BuyerCartTab.kt", "r") as f:
    content = f.read()

# I will find the double signature and fix it
pattern = r"fun CheckoutDialog\(\s*viewModel: MarketViewModel,\s*cartWithProducts: List<Pair<CartItemEntity, ProductEntity>>,\s*onDismiss: \(\) -> Unit,\s*onSuccess: \(\) -> Unit\s*cartWithProducts: List<Pair<CartItemEntity, ProductEntity>>,\s*viewModel: MarketViewModel,\s*onDismiss: \(\) -> Unit,\s*onSuccess: \(\) -> Unit\n\)"

new_signature = """fun CheckoutDialog(
    viewModel: MarketViewModel,
    cartWithProducts: List<Pair<CartItemEntity, ProductEntity>>,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
)"""

content = re.sub(pattern, new_signature, content, flags=re.MULTILINE)
with open("app/src/main/java/com/example/ui/buyer/BuyerCartTab.kt", "w") as f:
    f.write(content)
