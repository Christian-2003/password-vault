# Known Bugs that need to be fixed
* When Entry is changed / deleted, changed / deleted entry is not shown in EntriesFragment
* I don't quite know when exactly everything is saved to the filesystem. This should be fixed
* Items in ListView in EntriesFragment do not occupy full width of container view

<br>

***
# Features to Add
* Analysis for all passwords on home-screen
* Automatically generated passwords
* Templates for details and entries
* Multi-Language-Support
* Change language in settings
* Change between ligh / dark mode in settings
* Add password manager to application
* Biometric login for further security
* Additional encryption for sensitive data
* Export data
* Google drive backup
* Synchronization between devices
* Sort entries by name or other meaningful categories
* Search within entries
* Changeable order of details within entry
* Make URL-Detail be opened on click
* Make click on E-Mail-Detail open email app to write to this email. This is useless (as the email should be owned by the user), but cool
* Custom quality gates for password security score
* Show UUID / Created / Changed on Details
* Custom button style
* Make Appbar in MainActivity collapsable (as it is in most apps from Samsung)
* Swipe in MainActivity to scroll between fragments
* License and Used Software in Settings
* Link to Github and Bug reports in settings

<br>

***
# Use of Deprecated Functionalities that Should be Replaced
* Class `DetailDialogFragment` uses `setRetainInstance()`
* Class `ConfirmDeleteDialogFragment` uses `setRetainInstance()`
* Clas `EntryActivity` uses `startActivityForResult()`
* Clas `EntriesFragment` uses `startActivityForResult()`
