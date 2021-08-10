# Gradle Wrapper Version Updater

```bash
./build/install/bin/ghoul /home/elex/workspace 7.1.1
# or
./build/install/bin/ghoul /home/elex/workspace
# or
./build/install/bin/ghoul
```

It will find all the 'gradle-wrapper.properties' files recursively, under the given path. 
Then, replace the version string to the given version.

* if a path omitted, 'current working dir' will be the path.
* if a version omitted, it fetches recent version from the gradle website.

---
developed by Elex

https://www.elex-project.com
