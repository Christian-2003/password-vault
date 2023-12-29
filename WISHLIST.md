<img src="docs/img/icon.png" height="150" align="right">

> **Attention:** This document has been discarded in favor of GitHub [Issues](https://github.com/Christian-2003/password-vault/issues).

# Known Bugs that need to be fixed
This is a list of known bugs should be fixed.
* When Entry is changed / deleted, changed / deleted entry is not shown in EntriesFragment
* I don't quite know when exactly everything is saved to the filesystem. This should be fixed
* If detail's visibility is changed to "invisible", it is not shown with "Hidden Details" immediately after change. It is only shown after restarting `EntryActivity` with the same entry.

<br>

***
# Features to Add
This is a list of features that would be nice if added to the application.
* Analysis for all passwords on home-screen
* Automatically generated passwords
* Templates for details and entries
* Multi-Language-Support
* Change language in settings
* Change between light / dark mode in settings
* Add password manager to application
* Biometric login for further security
* Additional encryption for sensitive data
* Export data
* Google drive backup
* Synchronization between devices
* Changeable order of details within entry
* Make URL-Detail be opened on click
* Make click on E-Mail-Detail open email app to write to this email. This is useless (as the email should be owned by the user), but cool
* Custom quality gates for password security score
* Show UUID / Created / Changed on Details
* Make Appbar in MainActivity collapsable (as it is in most apps from Samsung)
* Swipe in MainActivity to scroll between fragments
* License and Used Software in Settings
* Link to Github and Bug reports in settings
* Make appearance and disappearance of search bar in `EntriesFragment` animated
* Show current sorting in `EntriesFragment`

<br>

***
# Use of Deprecated Functionalities that Should be Replaced
This is a list of API functionalities that were marked as _deprecated_ by the developer, that were used anyway.
* Class `DetailDialogFragment` uses `setRetainInstance()`
* Class `ConfirmDeleteDialogFragment` uses `setRetainInstance()`
* Class `EntryActivity` uses `startActivityForResult()`
* Class `EntriesFragment` uses `startActivityForResult()`

<br>

***
# Features implemented / fixed
This is a list of everything that was listed anywhere above beforehand. Items can be moved here if they were fixed / implemented / ...
* Sort entries by name or other meaningful categories
* Search within entries
* Custom button style (Style not custom, but from Material Design 3)
