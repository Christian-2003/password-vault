<img src="../img/icon.png" height="150" align="right"/>

# App Assets
This document contains information about the structure of the [app/src/main/assets](../../app/src/main/assets/)-directory. This directory contains all resources that would normally be stored within raw resource files (e.g. HTML templates) but need localization.

###### Table of Contents
1. [Directory Structure](#directory-structure)
2. [LocalizedAssetManager](#localizedassetmanager)

<br/>

## Directory Structure
The asset directory contains a subdirectory for each purpose of assets (in the context of the `LocalizedAssetManager`, these are called **folders**). 

Each of these folders contains a subdirectory for each **locale** that is shall be available. Please make sure that most assets are provided for all locales that are supported by the app.

The asset files itself are stored within the directories for the locales. Please make sure that the names of the asset files are identical for all locales.

###### Example
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
|          +---- style.css
|          +---- ...
+---- ...
```

###### Default Locale
The locale **en** is the default locale. If an asset is not available for a desired locale, the one from the **en**-locale is returned instead.

###### Special Resources
For each **folder**, you might need resources that are identical accross all locales like images or CSS styles. These types of resources can be stored within a **res**-directory as seen within the example above.

<br/>

## LocalizedAssetManager
The [`de.passwordvault.model.storage.LocalizedAssetManager`](../../app/src/main/java/de/passwordvault/model/storage/LocalizedAssetManager.java) is a Java-class that can access these types of localized assets.

You can get a localized asset for the file "backup.html" within the folder "help" for the current app locale as follows:

```Java
LocalizedAssetManager assetManager = new LocalizedAssetManager("help", Locale.getDefault());
String uri = assetManager.getFileUri("backup.html");
```

This returns a file-URI to the asset file that shall be loaded. As an example, this URI could be passed to a web view rendering the HTML document:

```Java
LocalizedAssetManager assetManager = new LocalizedAssetManager("help", Locale.getDefault());
String uri = assetManager.getFileUri("backup.html");

WebView webView = findViewById(R.id.web_view);
webView.loadUrl(uri);
```

Rendering a localized HTML document this way is recommended, since this allows the `WebView` to automatically load resource files that are required within the localized HTML asset.

<br/>

***
2024-05-10  
&copy; Christian-2003
