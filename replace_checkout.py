import re

with open("app/src/main/java/com/example/ui/buyer/BuyerCartTab.kt", "r") as f:
    content = f.read()

# Find the start of CheckoutDialog
pattern = r"@Composable\nfun CheckoutDialog\(.*?\n\)\s*\{"
match = re.search(pattern, content, re.DOTALL)
if match:
    start_index = match.start()
    
    with open("patch_checkout.kt", "r") as patch_file:
        patch_content = patch_file.read()
        
    # Replace from start_index to the end of the file with patch_content
    # Wait, in patch_checkout.kt I named it `CheckoutModal`. Let me rename it to `CheckoutDialog` and adjust parameters.
    patch_content = patch_content.replace("fun CheckoutModal(", "fun CheckoutDialog(\n    viewModel: MarketViewModel,\n    cartWithProducts: List<Pair<CartItemEntity, ProductEntity>>,\n    onDismiss: () -> Unit,\n    onSuccess: () -> Unit\n")

    new_content = content[:start_index] + patch_content
    with open("app/src/main/java/com/example/ui/buyer/BuyerCartTab.kt", "w") as f:
        f.write(new_content)
    print("Replaced successfully")
else:
    print("Could not find CheckoutDialog")
