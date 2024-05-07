<img src="../img/icon.png" height="150" align="right"/>

# App Assets
This document contains information about the structure of the [app/src/main/assets](../../app/src/main/assets/)-directory.

###### Table of Contents
1. [Directory Structure](#directory-structure)

<br/>

## Directory Structure
The asset directory contains a subdirectory for each purpose of assets. Within each of these subdirectory, there exists subdirectories for each locale. Localized asset files are stored within these locale-directories.

```
assets
+--- help
|    +---- de
|    |     +---- backup.html
|    |     +---- configure_autofill.html
|    |     +---- configure_login.html
|    +---- en
|    |     +---- backup.html
|    |     +---- configure_autofill.html
|    |     +---- configure_login.html
|    +---- res
|          +---- ...
+---- ...
```

Please make sure that the names of localized asset files are identical across all locales.

<br/>
***
2024-05-07 
&copy; Christian-2003
