<img src="../img/icon.png" height="150" align="right">

# Update to Version 3.2.0
This page explains how to update to the newest version of PasswordVault, version 3.2.0 . You can [download it here](https://github.com/Christian-2003/password-vault/releases/tag/v3.2.0) or update inside PasswordVault by clicking **Settings > Software Update**.

If you update from version 3.1.0 or lower to any other version, please follow the [update instructions](#update-from-version-310-or-lower).

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

## New Features and Improvements
The following new features and improvements are included in version 3.2.0 and onwards:

###### Optional Login
To further increase the safety of your data within PasswordVault, you can now require login to the application. Activate the login by clicking **Settings > Require login** and enter a password which will be used for authentication whenever you want to open the app.

You can easily reset your password by clicking **Settings > Change password**.

If your device supports biometric authentication (class 3), you can add biometric login to the application. Simply click **Settings > Use biometrics**. Please note that this will only work if biometrics are already configured on your device. If you have done this, whenever you want to open the app, you can login using your preferred biometric authentication method.

If class 3 biometrics are not available on your device, the respecitve options will not be shown within the application.


###### Optionally Encrypted Backup
In order to ensure maximum security when you backup your data, backups can be encrypted. To do so, click **Settings > Create backup** and enter a password afterwards. If you do not enter a password, the backup will be unencrypted.

Once you want to restore an encrypted backup, click **Settings > Restore backup**. The application prompts you to enter the password which was used for encryption. If you entered the correct password, the encrypted backup will be restored.


###### Changes to UI
To meet the Material Design 3 standards, changes were made to multiple UI elements.

<br>

***
2024-02-23  
&copy; Christian-2003
