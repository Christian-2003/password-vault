<img src="docs/img/icon.png" height="150" align="right">

# Update to Newer Versions
This page descruibes steps that need to be performed in order to update to certain newer versions of Password Vault.

###### Table of Contents
1. [Update from Version 3.5.6](#update-from-version-356)
2. [Update from Version 3.1.0](#update-from-version-310)

<br/>

## Update from Version 3.5.6
If want to update from version 3.5.6 or lower, please follow the update instructions below.

###### Update Instructions
1. Open Password Vault and go to **Settings > Data > Create Backup**.
2. Select a directory in which to store the backup.
3. Decide whether to encrypt the backup - if so, provide a password used for encryption.
4. Download the newest release of Password Vault [here](https://github.com/Christian-2003/password-vault/releases/latest).
5. Install the downloaded APK file.
6. Open the newly installed version of Password Vault.
7. Go to **Settings > Data > Restore Backup** and chosse the backup previously created.
8. If the backup is encrypted, provide the password used for encryption.
9. Click **Restore Backup**.
10. Unstall the previous version of Password Vault.

You have successfully updated Password Vault to version 3.6.0 or higher. Thank you for using this application.

###### Why Are These Steps Required?
We originally intended to publish the app on the Google Play Store. Therefore, we needed to perform some important changes to be suited for publishing on the Google Play Store, such as changing the package name.

However, eventually we decided against publishing on the Google Play Store due to security concerns - but we still kept some of the changes, requiring you to perform the steps mentioned above.

<br/>

## Update from Version 3.1.0
If want to update from version 3.1.0 or lower, please follow the update instructions below.

###### Update Instructions
1. Open Password Vault and click **Settings > Create backup**.
2. Create a backup of your data.
3. Uninstall your current version of Password Vault.
4. Download the newest Password Vault release [here](https://github.com/Christian-2003/password-vault/releases/latest).
5. Install the downloaded APK file.
6. Open the newly installed version of Password Vault.
7. Click **Settings > Data > Restore backup** and choose the previously created backup.

You have successfully updated Password Vault to version 3.2.0 or higher. Thank you for using this application.

###### Why Are These Steps Required?
In order to increase security, the release builds will be signed using a digital certificate starting with version 3.2.0. This mainly results in two situations:

1. Installing any signed APK that is not published on the Google Play Store (like Password Vault) results in your Android device warning you about potentially dangerous APK files. Since the certificate used to sign the APK was not handed to Google for verification, your Android device does not trust the application.  
If you want to continue using Password Vault, you need to ignore this warning and install the new version anyway.
2. If you update to version 3.2.0 or later from version 3.1.0 or lower, you once need to uninstall your currently installed version of Password Vault before you can install the new version. To do this (and keep your data, please follow the steps shown below).  
After you have done this, you can safely update to new versions once they are released without following the steps below.

<br/>

***
2024-07-20  
&copy; Christian-2003
