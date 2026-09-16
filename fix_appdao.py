import re

with open("app/src/main/java/com/example/data/AppDao.kt", "r") as f:
    lines = f.readlines()

new_lines = []
found = False
for line in lines:
    if "fun getAllPaymentsFlow" in line:
        if found:
            # Drop the @Query just before it as well
            if len(new_lines) > 0 and "@Query" in new_lines[-1]:
                new_lines.pop()
            continue
        found = True
    new_lines.append(line)

with open("app/src/main/java/com/example/data/AppDao.kt", "w") as f:
    f.writelines(new_lines)
