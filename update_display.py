import re

with open("app/src/main/java/com/example/ui/buyer/BuyerProfileTab.kt", "r") as f:
    content = f.read()

pattern = r"Text\(\s*text = currentUser\?\.phone \?: \"\",\s*style = MaterialTheme\.typography\.bodySmall,\s*color = MaterialTheme\.colorScheme\.onSurfaceVariant\s*\)"
match = re.search(pattern, content)
if match:
    new_text = match.group(0) + """
                                    if (!currentUser?.address.isNullOrBlank()) {
                                        Text(
                                            text = currentUser?.address ?: "",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }"""
    content = content[:match.end()] + new_text + content[match.end():]

with open("app/src/main/java/com/example/ui/buyer/BuyerProfileTab.kt", "w") as f:
    f.write(content)
