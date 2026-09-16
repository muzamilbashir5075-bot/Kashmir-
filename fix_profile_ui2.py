import re

with open("app/src/main/java/com/example/ui/buyer/BuyerProfileTab.kt", "r") as f:
    content = f.read()

# Fix the duplicate text
pattern = r"Text\(\s*text = currentUser\?\.phone \?: \"\",\s*style = MaterialTheme\.typography\.bodySmall,\s*color = MaterialTheme\.colorScheme\.onSurfaceVariant\s*\)Text\(\s*text = currentUser\?\.phone \?: \"\",\s*style = MaterialTheme\.typography\.bodySmall,\s*color = MaterialTheme\.colorScheme\.onSurfaceVariant\s*\)"

content = re.sub(pattern, """Text(
                                        text = currentUser?.phone ?: "",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )""", content)

with open("app/src/main/java/com/example/ui/buyer/BuyerProfileTab.kt", "w") as f:
    f.write(content)
