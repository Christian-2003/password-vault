<img src="docs/img/icon.png" height="150" align="right">

# Changelog

## 3.5.4 (XXXX-XX-XX)
###### Release Highlights
Users can now save login data to Password Vault after login to an app.

###### Features
* Fixed a bug where autofill-authentication is always required, even if app login is deactivated or not configured.
* Fixed a bug where the autofill suggestion only displayed "Password for" if no username was provided.
* Refactored layout files that did not conform the naming conventions.
* Fixed color for fingerprint in autofill authentication presentation.
* Added save requests to the autofill service.
* Removed unused class and methods to improve maintainability.
* Loading (and sorting) packages in `PackagesActivity` no longer blocking main thread.
* Fixed a bug where the password security analysis showed an average security score of "-NaN / 5", when no passwords were analyzed.

<br/>

## 3.5.3 (2024-05-10)
###### Release Highlights
Added help pages to the app, changed home page and fixed some bugs.

###### Features
* Added feature to delete all autofill caches.
* Changes to `HomeFragment`.
* Moved tags and assigned apps from "general" container to "additional" container in `EntryActivity`.
* Changed some drawable icons for their respective versions with rounded corners.
* Added `LocalizedAssetManager` to load localized assets.
* Added help to settings.
* Added help page for configuring autofill service.
* Added help page for configuring the app login.
* Added help page for managing backups.
* Fixed a bug where the title within the app bar of the PackagesActivity was not correctly aligned.
* Improvements to content insets and image buttons of toolbars.
* Fixed a bug where cancelling app login configuration did not update the corresponding switch.

<br/>

## 3.5.2 (2024-04-26)
###### Release Highlights
General improvements and bugfixes. Completely changed HTML exports and introduced weak passwords to password security analysis.

###### Features
* Fixed a bug where all app data was permanently deleted when the app crashes.
* Changed navigation bar color in `MainActivity` to match the color of the NavigationBarView.
* Fixed a bug where rotating the app while the `DetailDialog` is open crashed the app and permanently deleted all app data.
* Fixed a bug where the color of end icons within the `DetailDialog` was not correct.
* Added option to change the number of the recently edited accounts on home page.
* Added feature indicating a new app release to the user.
* Complete overhaul of the HTML export file.
* Improvements to activity displaying duplicate passwords.
* Implemented integrity check when loading abbreviated entries: Abbreviated entries are not loaded if the corresponding extended entry does not exist.
* Added formatting of average password security score according to current app locale.
* Added weak passwords to password security analysis.
* Fixed color for navigation bar icons to match light / dark mode.
* Added checkmark icon to packages fragment to indicate which packages have been selected by the user.

<br/>

## 3.5.1 (2024-04-15)
###### Release Highlights
Improvements to the UI and UX as well as some bugfixes. Updated `com.google.android.material`-dependency from version _1.7.0_ to _1.9.0_, which is the last version that can compile against _SDK 32_. This upgrade has been done to introduce search bars to the app as well as for general improvements regarding material components.

###### Features
* Fixed a bug where an (incorrect) app icon is shown for an entry that should not have an icon at all.
* Added setting to manually change between dark / light mode.
* Generalized authentication through master password or biometrics to 'Authenticator'-class.
* Current date is automatically appended to the default file name when a new backup is created.
* Split all settings into different activities.
* Reworked `SettingsFragment`.
* Partial introduction of collapsable toolbars.
* Improvements to maintainability regarding string-resources.
* Added custom style for bottom navigation bar.
* Replaced buttons to save and delete custom password quality gates with image buttons.
* Replaced buttons to edit and delete entries with image buttons.
* Replaced button to save edited detail with image button.
* Removed button to cancel changes to an edited detail.
* Changed dialog background color and text appearance.
* Changed style of buttons.
* Fixed color for input hint of dropdown menu in `DetailDialog`.
* List of installed apps is now sorted alphabetically.
* Added search bar to search in installed apps.
* Fixed a bug in `EntryActivity` where the 'Details' headline was displayed when the entry does not have any details.
* Fixed a bug in `AddEntryActivity` where the color of the swipe icon in the recycler view did not match night mode selected in the settings.
* Fixed a bug where tags were not correctly restored with a backup.
* Small changes to app language.
* Added custom `SearchBarView` since I cannot sufficiently style the default `SearchView`.
* Fixed a bug in `AddEntryActivity` where a swiped detail was not shown after closing the corresponding dialog.
* Fixed a bug where changes within an entry were not immediately shown in recycler views of `MainActivity`. This includes editing and deleting entries.
* Fixed a bug where deleting all entries through settings did not update recycler view in `HomeFragment`.
* Fixed a bug where adding new entries did not immediately update recycler views in `MainActivity`.
* Entries with no available app icon will have the first letter of their name shown in place of the app icon instead.

<br/>

## 3.5.0 (2024-04-03)
###### Release Highlights
Major visual enhancements regarding boundaries of clickable contents. Furthermore, the app can be used as autofill service providing suggestions for
data (e.g. usernames, passwords or email addresses) to the user of other apps.

###### Features
* Fixed a bug within HTML exports, where `<h2>`-elements were not correctly closed.
* Manifest cleanup.
* Fixed app name from "PasswordVault" to "Password Vault".
* Users can select installed apps that shall be associated with an entry for the password manager.
* Moved padding from container to views.
* Added clipping child-views to container bounds.
* Selectable item backgrounds now fill entire parent-container.
* Enhanced boundaries for swipe action backgrounds in `AddEntryActivity`.
* Users can select details as password / username for the autofill service.
* Added security policy.
* Added issue-template.
* Fixed bug where the color of checkmarks for tags was not correct.
* Users can enable Password Vault as autofill service from settings.
* Users can enable / disable caching for autofill service.
* Fixed a bug where the most recently edited entries were not calculated correctly.
* Fixed a bug where restoring an encrypted backup created two empty accounts.
* App can be used as autofill service.
* Logos of associated entries are shown in list of entries.
* Reintroduced scrollbars since they were lost in the last update.
* Fixed scroll behaviours of details in `AddEntryActivity` and `EntryActivity`.
* Fixed scroll behaviours of entries in `HomeFragment`.
* Added optional authentication for autofill service.
* Moved MIT license from string resource into raw resource file.
* Moved templates for HTML export from string resource into raw resource files.
* Added special display for recently changed entries in `HomeFragment` and list of entries in `EntriesFragment` for when no entries are available to display.
* Implemented caches to accelerate access to autofill data that is often used.

<br/>

## 3.4.0 (2024-03-09)
###### Release Highlights
Details are now reorderable and the app can perform a password security analysis for all passwords at once. The analysis calculates the average security score, and
finds duplicate passwords.

###### Features
* Details can be reordered.
* Added swipe gestures to edit / delete details.
* Changed character used for obfuscation from '*' to '\u2022'.
* Fixed a bug where switches in settings did not correctly trigger app response when switched.
* Added five of the most recently edited entries to `HomeFragment`.
* Replaced `ListView` in `EntriesFragment` with `RecyclerView` to increase performance.
* Deleted _WISHLIST.md_, since this project uses GitHub Issues to track bugs and enhancements since 2023-12-29
* Added tutorials for app installation and quickstart.
* Improvements to _README.md_
* Added extensive password security analysis for all passwords to the app.
* Content of details can be copied to the clipboard through long press on the detail.

<br/>

## 3.3.0 (2024-02-22)
###### Release Highlights
Added custom quality gates for password security which can be changed in settings. Furthermore, entries can now be categorized using tags. Additionally, the storage of the accounts was completely reworked.

###### Features
* Added models for custom password quality gates.
* Users can now implement custom password security quality gates through RegEx.
* Removed deprecated password security analysis.
* Added tags for entries.
* Tags are included in backups.
* Details are correctly obfuscated when editing obfuscated details.
* Complete reworking of the storage management for entries and details.
* Stored details and entries are converted the first time the app is opened with data from before version 3.3.0.
* Toggling biometrics for login requires confirming identity through biometrics.
* Disabeling app login requires biometric authentication (if available) or confirmation through app password.
* Rotating `LoginActivity` after cancelling biometric login no longer shows biometric prompt.
* Added button to settings to delete all data. Deleting all data requires authentication (if available) or confirmation through dialog.
* UUID of entries is no longer shown in `EntryActivity`, since this is very useless for the user.
* `EntriesFragment` shows current sorting of entries in popup menu.

<br/>

## 3.2.1 (2024-02-02)
###### Release Highlights
Changes to app UI as well as some quality of life changes. Added PIN code as a type for details.

###### Features
* Moved buttons and headlines for fragments within main activity into app bar.
* Changed color of bottom navigation bar.
* Click on "OK"-button on keyboard within app login tries to login the user.
* Changing type to "Password" in `DetailDialog` changes detail name automatically to "Password" and makes it obfuscated, if those respective UI components were not changed by the user beforehand.
* Changing type to "E-Mail Address" in `DetailDialog` changes detail name automatically to "E-Mail Addres" if the name was not changed by the user beforehand.
* Reordered UI elements within `DetailDialog`.
* Added PIN code as a type for details.
* Content input within `DetailDialog` is now obfuscated when obfuscate-checkbox is checked.
* Added custom landscape layout for `MainActivity` in which the navigation bar is located at the leading side instead of the bottom.

<br/>

## 3.2.0 (2024-01-19)
###### Release Highlights
Backups can now be encrypted through a password. To increase data security, an optional login into the application with a password can be activated.

###### Features
* Removed setting to change UI mode, since this did not work.
* Cryptographic keys for AES can now be generated with a seed.
* Refactored location for all ViewModels.
* Added dialogs `CreateBackupDialog` and `RestoreBackupDialog` to create and restore backups.
* Changes to backup generation and backup restoration.
* Backups can now be encrypted using a password.
* Label of `DetailType` is now loaded from resources whenever it is accessed in order for the label to be dynamically changed when device's language changes while the app is running.
* Dialogs are no longer retained through the deprecated `DialogFragment.setRetainInstance(true)`-method. Instead, dialogs use a ViewModel to retain their state.
* Optional login to the application with a password.
* Password for optional login can be changed in settings.
* Reordered items in `SettingsFragment`.
* Refactoring of resource-strings to match initial naming pattern.
* Added buttons to all password inputs, to toggle password visibility.
* Added _class 3_ biometric login.

<br/>

## 3.1.0 (2023-12-31)
###### Release Highlights
Account data can now be exported into readable HTML-format. Furthermore, account data can be backed up and restored, although backups will not be encrypted. Furthermore, some changes to the GUI were made and German was added as language to the application.

###### Features
* Added `CreateXmlBackup`-class to backup the data to shared storage, which will not be wiped when the application is uninstalled.
* Added `RestoreXmlBackup`-class to restore a backup from shared storage.
* Added `BackupException`-class which can be thrown when some error with backups occur.
* Added `XmlException`-class which can be thrown when some error regarding the XML-structure of a backup occur.
* Added support for German.
* Changes in color scheme for app GUI.
* Added `Observer<T>`- and `Observable<T>`-interfaces for Observer-pattern.
* Added `EntryHandleObservable`-class to implement observable for the `EntryHandle`.
* Used Observer-pattern to fix a bug, where changes in entries were not updated in the `EntriesFragment`, which could result in a crash if the respective change was a deletion of an entry.
* Fixed a bug where a detail was not shown with the hidden details after it'S visibility was changed.
* Small QoL-changes
* Animated password-security-score.
* Additions on SettingsFragment.
* Added `ExportToHtml`-class to export account data to readable HTML format.
* Added `ExportException`-class which can be thrown when errors regarding an export were encountered.

<br/>

## 3.0.0 (2023-12-28)
###### Release Highlights
Major overhaul of the data handling results in backwards-compatibility break. Added some quality of life changes, which includes a new home fragment for the application.

###### Features
* Updated `EditText` in `AddEntryActivity` and `DetailDialogFragment` to match Material Design 3 standards.
* Details can now only be added when both name and content is set, otherwise, an error message is shown.
* Entry can now only be added when name is set, otherwise, an error message is shown.
* Configured `HomeFragment`.
* Added license info for the application.
* Fixed Singleton-pattern for `EntryFragment`.
* Added `Utils`-class for utility methods.
* Added Data-Transfer-Objects for data transfer between primary- and secondary storage.
* Entries and details are now stored in CSV format instead of JSON format
* Added classes to build and parse CSV.
* Added classes which will be used in the next updates to generate data backups.

<br/>

## 2.2.1 (2023-10-13)
###### Release Highlights
Major security overhaul: All entries are now being encrypted through an AES/GCM-Algorithm. The required key will be securely stored within the KeyStore.

###### Features
* The entries in the `ListView` in `EntriesFragment` now fill entire horizontal width of the container.
* Icons in bottom navigation bar are now outlined when not selected and filled when selected.
* Fixed a bug where the correct entry was not opened after being selected within the `ListView` where a filter was used through the search bar.
* Removed autocomplete from detail's content input to prevent sensitive user data (e.g. passwords) from being leaked to the autocorrect-software.
* Switched positions of "Save" and "Cancel" buttons in `AddEntryActivity` to match order of "Save" and "Cancel" buttons of popup dialog windows.
* Added slide in and slide out animations for search bar in `EntriesFragment`.
* All entries are encrypted through an AES-algorithm.

<br/>

## 2.2.0 (2023-10-10)
###### Release Highlights
Major design overhaul: The Material Design 3 has been partially incorporated into the applications design. Furthermore, a custom icon for the application is introduced. When it comes to functionalities, the list of entries in can now be searched for any substring.

###### Features
* List of `Entry`-instances in the `EntriesFragment` are now searchable. The algorithm searches a pattern (can be entered through a search bar) in the name and description of the entry.
* Created App-icon.
* Partial incorporation of Material Design 3.

<br/>

## 2.1.0 (2023-10-09)
###### Release Highlights
Entries can now be sorted according to some categories.

###### Features
* Changed name of `GenericSecurityException` to `SecurityException`.
* Removed implementation of `Serializable`-interface from class `Detail`, as nothing for serialization was implemented and there is no need to serialize instances.
* Entries within the `EntriesFragment` can be sorted according to the following categories: "Not sorted", "Name: Ascending", "Name: Descending", "Created: Ascending" and "Created: Descending".
* Implemented `GenericComparator<T>`-class, which implements `Comparator<T>`-interface to provide information on reverse sorting for subclasses.
* Implemented `LexicographicComparator` and `TimeComparator` to sort `Entry`-instances according to the categories listed above.

<br/>

## 2.0.0 (2023-10-04)
###### Release Highlights
Backwards-compatibility breaking changes to the handling of entries within the backend. All details about entries are now loaded from persistent memory when the application starts, to minimize the number of times files have to be accessed throughout the applications lifecycle.

###### Features
* Removed `AbbreviatedEntries` class and moved it's contents into `Entry`.
* Changed the `EntryHandle` class to no longer regard abbreviated entries.
* Changes throughout the application code to meet the above mentioned changes.
* Added client side encryption to `backup_rules` to encrypt user data when Android creates backups for app data.
* Added `AES` and `EncryptionException` classes as well as `Encryptable` interface to provide encryption algorithms for further data security. These classes are currently unused and will be implemented into the code later.
* Added `FileAccessor`, `FileReader`, `FileWriter` and `GenericSecurityException` classes to handle access to encrypted files. These classes are currently unused and will be implemented into the code later.
* Added exceptions to `Entry` classes for better security.

<br/>

## 1.0.1 (2023-09-23)
###### Release Highlighs
Added WISHLIST.md and VERSION.txt to project files. Bugfixes.

###### Features
* Added WISHLIST.md which contains everything that needs to be fixed and features that can be implemented.
* Added VERSION.txt which contains the version number of the current version.
* Changed entry now correctly displayed after change in EntryActivity.
* App no longer crashes while a dialog is open and the app is rotated.
* Fixed typos and links in README.md.
* Moved implementation for interface ListItemSelectionListener into EntriesFragment.

<br/>

## 1.0.0 (2023-09-23)
###### Release Highlights
The git repository was created on GitHub and all relevant project files were uploaded. The basic functions of the application work although there is much room for improvement.

###### Features
* Account details can be stored / added / deleted
* Sensitive content of a detail can be obfuscated
* An account entry can be stored / added / deleted
* The content of account details whose type is "password" will automatically be analyzed. An estimated password security score will be displayed
