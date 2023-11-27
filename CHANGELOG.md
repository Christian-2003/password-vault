<img src="docs/img/icon.png" height="150" align="right">

# 2.2.2 (XXXX-XX-XX)
### Release Highlights
Added some quality of life changes, which includes a new home fragment for the application.

### Features
* Updated `EditText` in `AddEntryActivity` and `DetailDialogFragment` to match Material Design 3 standards.
* Details can now only be added when both name and content is set, otherwise, an error message is shown.
* Entry can now only be added when name is set, otherwise, an error message is shown.
* Configured `HomeFragment`.
* Added license info for the application.

<br>

# 2.2.1 (2023-10-13)
### Release Highlights
Major security overhaul: All entries are now being encrypted through an AES/GCM-Algorithm. The required key will be securely stored within the KeyStore.

### Features
* The entries in the `ListView` in `EntriesFragment` now fill entire horizontal width of the container.
* Icons in bottom navigation bar are now outlined when not selected and filled when selected.
* Fixed a bug where the correct entry was not opened after being selected within the `ListView` where a filter was used through the search bar.
* Removed autocomplete from detail's content input to prevent sensitive user data (e.g. passwords) from being leaked to the autocorrect-software.
* Switched positions of "Save" and "Cancel" buttons in `AddEntryActivity` to match order of "Save" and "Cancel" buttons of popup dialog windows.
* Added slide in and slide out animations for search bar in `EntriesFragment`.
* All entries are encrypted through an AES-algorithm.

<br>

# 2.2.0 (2023-10-10)
### Release Highlights
Major design overhaul: The Material Design 3 has been partially incorporated into the applications design. Furthermore, a custom icon for the application is introduced. When it comes to functionalities, the list of entries in can now be searched for any substring.

### Features
* List of `Entry`-instances in the `EntriesFragment` are now searchable. The algorithm searches a pattern (can be entered through a search bar) in the name and description of the entry.
* Created App-icon.
* Partial incorporation of Material Design 3.

<br>

# 2.1.0 (2023-10-09)
### Release Highlights
Entries can now be sorted according to some categories.

### Features
* Changed name of `GenericSecurityException` to `SecurityException`.
* Removed implementation of `Serializable`-interface from class `Detail`, as nothing for serialization was implemented and there is no need to serialize instances.
* Entries within the `EntriesFragment` can be sorted according to the following categories: "Not sorted", "Name: Ascending", "Name: Descending", "Created: Ascending" and "Created: Descending".
* Implemented `GenericComparator<T>`-class, which implements `Comparator<T>`-interface to provide information on reverse sorting for subclasses.
* Implemented `LexicographicComparator` and `TimeComparator` to sort `Entry`-instances according to the categories listed above.

<br>

# 2.0.0 (2023-10-04)
### Release Highlights
Backwards-compatibility breaking changes to the handling of entries within the backend. All details about entries are now loaded from persistent memory when the application starts, to minimize the number of times files have to be accessed throughout the applications lifecycle.

### Features
* Removed `AbbreviatedEntries` class and moved it's contents into `Entry`.
* Changed the `EntryHandle` class to no longer regard abbreviated entries.
* Changes throughout the application code to meet the above mentioned changes.
* Added client side encryption to `backup_rules` to encrypt user data when Android creates backups for app data.
* Added `AES` and `EncryptionException` classes as well as `Encryptable` interface to provide encryption algorithms for further data security. These classes are currently unused and will be implemented into the code later.
* Added `FileAccessor`, `FileReader`, `FileWriter` and `GenericSecurityException` classes to handle access to encrypted files. These classes are currently unused and will be implemented into the code later.
* Added exceptions to `Entry` classes for better security.

<br>

# 1.0.1 (2023-09-23)
### Release Highlighs
Added WISHLIST.md and VERSION.txt to project files. Bugfixes.

### Features
* Added WISHLIST.md which contains everything that needs to be fixed and features that can be implemented.
* Added VERSION.txt which contains the version number of the current version.
* Changed entry now correctly displayed after change in EntryActivity.
* App no longer crashes while a dialog is open and the app is rotated.
* Fixed typos and links in README.md.
* Moved implementation for interface ListItemSelectionListener into EntriesFragment.

<br>

# 1.0.0 (2023-09-23)
### Release Highlights
The git repository was created on GitHub and all relevant project files were uploaded. The basic functions of the application work although there is much room for improvement.

### Features
* Account details can be stored / added / deleted
* Sensitive content of a detail can be obfuscated
* An account entry can be stored / added / deleted
* The content of account details whose type is "password" will automatically be analyzed. An estimated password security score will be displayed
