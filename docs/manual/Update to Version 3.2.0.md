<img src="../img/icon.png" height="150" align="right"/>

# Update from Version 3.1.0
This page describes how to update from version 3.1.0 or lower to the newer versions of Password Vault, since this requires special steps that you need to take in order to keep your data.

<br>

## Update Instructions
Starting with version 3.2.0, the release builds will be signed using a digital certificate. This mainly results in two situations:

1. Installing any signed APK that is not published on the Google Play Store (like PasswordVault) results in your Android device warning you about potentially dangerous APK files. Since the certificate used to sign the APK was not handed to Google for verification, your Android device does not trust the application.  
If you want to continue using PasswordVault, you need to ignore this warning and install the new version anyway.
2. If you update to version 3.2.0 or later from version 3.1.0 or lower, you once need to uninstall your currently installed version of PasswordVault before you can install the new version. To do this (and keep your data, please follow the steps shown below).  
After you have done this, you can safely update to new versions once they are released without following the steps below.

###### Update from version 3.1.0 or lower
Due to the changes to the signed release APK, you need to execute the following steps if you want to update from version 3.1.0 or lower to any other version:

1. Open PasswordVault and click **Settings > Create backup**.
2. Create a backup of your data.
3. Uninstall your current version of PasswordVault.
4. Download the newest PasswordVault relese [here](https://github.com/Christian-2003/password-vault/releases/latest).
5. Install the downloaded APK file.
6. Open the newly installed version of PasswordVault.
7. Click **Settings > Restore backup** and choose the previously created backup.

You have successfully updated PasswordVault to version 3.2.0 or higher. Thank you for using this application.

<br>

***
2024-03-15  
&copy; Christian-2003
