import re

with open("app/src/main/java/com/example/ui/buyer/BuyerProfileTab.kt", "r") as f:
    content = f.read()

# I want to add address to the profile editing.
# First add state variable
pattern_state = r"var editPhone by remember \{ mutableStateOf\(currentUser\?\.phone \?: \"\"\)\s*\}"
match_state = re.search(pattern_state, content)
if match_state:
    content = content[:match_state.end()] + "\n    var editAddress by remember { mutableStateOf(currentUser?.address ?: \"\") }" + content[match_state.end():]

# Now update the UI
pattern_ui = r"OutlinedTextField\(\s*value = editPhone,.*?singleLine = true\s*\)"
match_ui = re.search(pattern_ui, content, re.DOTALL)
if match_ui:
    new_ui = match_ui.group(0) + """
                    OutlinedTextField(
                        value = editAddress,
                        onValueChange = { editAddress = it },
                        label = { Text("Saved Address") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )"""
    content = content[:match_ui.start()] + new_ui + content[match_ui.end():]

# Now update the click handler
pattern_click = r"viewModel\.updateUserProfile\(editName, editPhone\)"
content = content.replace(pattern_click, "viewModel.updateUserProfile(editName, editPhone, editAddress)")

with open("app/src/main/java/com/example/ui/buyer/BuyerProfileTab.kt", "w") as f:
    f.write(content)
