import re

with open("app/src/main/java/com/example/ui/buyer/BuyerProfileTab.kt", "r") as f:
    content = f.read()

# Let's fix the syntax error in BuyerProfileTab.kt lines 161-168
# Let's extract around line 150-170
lines = content.split('\n')
for i, line in enumerate(lines[140:175]):
    pass # print(f"{i+141}: {line}")

# The issue is likely a missing composable wrapper or formatting issue with the injected code
pattern = r"if \(\!currentUser\?\.address\.isNullOrBlank\(\)\) \{\s*Text\(\s*text = currentUser\?\.address \?: \"\",\s*style = MaterialTheme\.typography\.bodySmall,\s*color = MaterialTheme\.colorScheme\.onSurfaceVariant,\s*maxLines = 2,\s*overflow = TextOverflow\.Ellipsis\s*\)\s*\}"

content = re.sub(pattern, """
                                    if (!currentUser?.address.isNullOrBlank()) {
                                        androidx.compose.material3.Text(
                                            text = currentUser?.address ?: "",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }""", content)

with open("app/src/main/java/com/example/ui/buyer/BuyerProfileTab.kt", "w") as f:
    f.write(content)
