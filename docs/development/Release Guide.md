<img src="../img/icon.png" height="150" align="right"/>

# Release Guide
This document describes the process of releasing a new version of the Password Vault application. It is aimed at developers to better undestand the release-process and to prevent any important steps from being accidentally skipped. Therefore, this guide contains a list of all steps that need to be taken in order to release a new version of the software.

###### Table of Contents
1. [Before Releasing a New Version](#before-releasing-a-new-version)
2. [Preparing for a New Release](#preparing-for-a-new-release)
3. [Building a New Release](#building-a-new-release)
4. [Commit Changes to GitHub](#commit-changes-to-github)
5. [Publish a New Release](#publish-a-new-release)
6. [Complete Checklist](#complete-checklist)

<br/>

## Before Releasing a New Version
Before releasing a new version, make sure that the entire software works as intended. New functionalities must not prevent any functionalities that were introduced in previous versions from working properly. Make sure that all unit tests are passed before releasing.

###### Checklist
* Software works as intended
* Previous fetures work as intended

<br/>

## Preparing for a New Release
Before releasing, a few files used for documentation and build purposes need to be updated.

###### Version Numbers
In preparation for a new release, make sure that the version numbers both within the [VERSION.txt](../../VERSION.txt) and [build.gradle](../../app/build.gradle) files are correct. Determine the accurate version number as defined by the guidelines from [semver](https://semver.org/). Furthermore, make sure to increment the version code in build.gradle before every release. An example for version numbers within the mentioned files could be as follows:

_VERSION.txt_
```
3.2.1
```

_build.gradle_
```groovy
android {
    // ...
    defaultConfig {
        // ...
        versionCode 10
        versionName "3.2.1"
    }
}
```

Furthermore, make sure that the [CHANGELOG.md](../../CHANGELOG.md) is up to date. The changelog should contain all important changes to the new version, as well as a short description about the highlights for the release. Please make sure that the version number within the changelog is identical to the previously changed version number. In addition to that, make sure that the date is correct as well. The date should always be the date on which the new version is published. An example for a changelog could be as follows:

_CHANGELOG.md_
```markdown
# 3.2.1 (2024-01-31)
### Release Highlights
Small changes to application UI to be more compatible with Material Design 3 guidelines.

### Features
* Moved buttons and headlines for some fragments into app bar.
* Changed color of bottom navigation bar.
* ...

...
```

Continue by making sure that the documentation for all Java-classes are up to date. All classes that were changed since the last release need to be updated with the new version number. An example for an updated version number for a Java-class could be as follows:

_ExampleClass.java_
```java
/**
 * This class does something.
 * 
 * @author  Christian-2003
 * @version 3.2.1
 */
class ExampleClass {
    // ...
}
```

If you have done important changes to the class in a large quantity, please make sure to add your GitHub name to the class as well:

_ExampleClass.java_
```java
/**
 * This class does something.
 * 
 * @author  Christian-2003, MyNameIsMarvin
 * @version 3.2.1
 */
class ExampleClass {
    // ...
}
```

###### Security Policy
Please add the current version number to the table [Data Encryption](../../SECURITY.md#data-encryption) and add the respective information:

_SECURITY.md_
```markdown
...

Version | Data | Backup | User Password
--- | --- | --- | ---
3.2.1 | :white_check_mark: | :white_check_mark: | :white_check_mark:

...
```

If any security-related features are added within the release, add a column to indicate the feature.

###### Checklist
* Version number changed in _VERSION.txt_
* Version number changed in _build.gradle_
* Updated _CHANGELOG.md_
* Updated all Java classes
* Updated _SECURITY.md_

<br/>

## Building a New Release
The next step is building the new release within Android Studio. This can only be done by the head developer [Christian-2003](https://github.com/Christian-2003) since he contains the certificate to generate a signed APK file. In order to build a new release, follow the steps mentioned below.

Click **Build > Select Build Variant...** and change the Active Build Variant for module **:app** from debug to **release**.

Click **Build > Generate Signed Bundle / APK**. Select APK and click the Next-button.

Enter the keystore path, (e.g. `C:\Users\keystores\android.jks`), and the key alias (e.g. `release_key`). Enter the required passwords afterwards. Please remember that this step can only be done by the head developer since this requires access to the release certificate. Click the Next-button.

Select **release** for the build variant and click the Create-button.

The release will be generated to the following directory:
[app/release/](../../app/release/).

Please change the name of the generated APK file to "password-vault-vX.X.X.apk". An example for a correct name could be as follows:
```
password-vault-v3.2.1.apk
```

###### Checklist
* Generated signed APK file with the release certificate
* Changed name of APK file

<br/>

## Commit Changes to GitHub
Before publishing a new release, please commit all changes that were done to the repository (including the already mentioned changes within this guide) to the GitHub repository.

For this commit, please choose the following commit summary: `Release version <version number>`. An example for this message might be as follows:  
`Release version 3.2.1`

For the commit description, always choose the following:  
`Last changes before release!`

###### Checklist
* Committed all changes to GitHub

<br/>

## Publish a New Release
The next step is publishing the relase to GitHub, which can be done on [this](https://github.com/Christian-2003/password-vault/releases) page.

Click on **Draft a new release** and upload the previously created APK file.

Enter a name for the release. The name must always be the version number with the prefix "v". An example for a correct name could be as follows: `v3.2.1`.

Add a new tag to the release. The tag must be identical to the previously chosen name. An example of a correct tag could be as follows: `v3.2.1`.

Please describe the release with the most important information. Include the version number at the top in a sentence like "Update to version 3.2.1". Mention the most important release notes. This should be a summary of what was mentioned within **Release Highlights** of the _CHANGELOG.md_. An example could be as follows:
```markdown
###### Changes
Minor changes to application UI as well as some bugfixes.
```

Furthermore, if some very important happened during the release (like some backwards-incompatible changes), include those as important release notes:
```markdown
###### Important Release Notice
Due to backwards-incompatible changes...
```

Always include information for people who update from version 3.5.6 or lower:
```markdown
###### Update From Version 3.5.6 or Lower
If you update from version 3.5.6 or lower, please follow the [update guidelines](https://github.com/Christian-2003/password-vault/blob/master/docs/manual/Update%20to%20Newer%Versions.md) for updating to the newer versions.
```

A complete release description could be as follows:
```markdown
Update to version 3.2.1

###### Important Release Notice
Due to backwards-incompatible changes...

###### Changes
Minor changes to application UI as well as some bugfixes.

###### Update From Version 3.5.6 or Lower
If you update from version 3.5.6 or lower, please follow the [update guidelines](https://github.com/Christian-2003/password-vault/blob/master/docs/manual/Update%20to%20Newer%Versions.md) for updating to the newer versions.
```

After that, you may publish the release to GitHub.

###### Checklist
* Entered name
* Created new tag
* Created description

<br/>

## Complete Checklist
This is a complete checklist of all steps that need to be taken to release a new version:

1. [Before Releasing a New Version](#before-releasing-a-new-version)
    * Software works as intended
    * Previous fetures work as intended
2. [Preparing for a New Release](#preparing-for-a-new-release)
    * Version number changed in _VERSION.txt_
    * Version number changed in _build.gradle_
    * Updated _CHANGELOG.md_
    * Updated all Java classes
    * Updated _SECURITY.md_
3. [Building a New Release](#building-a-new-release)
    * Generated signed APK file with the release certificate
    * Changed name of APK file
4. [Commit Changes to GitHub](#commit-changes-to-github)
    * Committed all changes to GitHub
5. [Publish a New Release](#publish-a-new-release)
    * Entered name
    * Created new tag
    * Created description

<br/>

***
2024-06-06  
&copy; Christian-2003
