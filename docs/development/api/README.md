<img src="../../img/icon.png" height="150" align="right"/>

# Password Vault API
This document contains a reference for multiple important framework components of the Password Vault app.

###### Table of Contents
1. [RecyclerView Framework](#recyclerview-framework)
2. [BottomSheetDialog Framework](#bottomsheetdialog-framework)
3. [MoreDialog Framework](#moredialog-framework)

<br/>

***

## RecyclerView Framework
The RecyclerView framework provides classes that can be used to implement recycler views that handle scrolling, data sourcing and swiping.

###### Classes
Class | Description
--- | ---
[OnRecyclerViewActionListener](OnRecyclerViewActionListener.md) | Interface can be implemented by a host of a recycler view. This allows for callbacks from a recycler view adapter to be handled by the host (e.g. an Activity or Fragment).
[RecyclerViewAdapter](RecyclerViewAdapter.md) | Class resembles the superclass for all recycler view adapters that want to utilize this framework. The class handles basic tasks like view model management and item swiping. Furthermore, the class contains some generic view holders.
[RecyclerViewSwipeCallback](RecyclerViewSwipeCallback.md) | Class handles the item swiping.
[RecyclerViewSwipeCallback.SwipeAction](RecyclerViewSwipeCallback.SwipeAction.md) | Class is used to define a swipe action for items within a recycler view.
[RecyclerViewSwipeCallback.SwipeContract](RecyclerViewSwipeCallback.SwipeContract.md) | Class is implemented by a recycler view adapter to handle item swiping.

<br/>

***

## BottomSheetDialog Framework
The BottomSheetDialog framework provides utilities that are required to create bottom sheet dialogs within the Password Vault app.

###### Classes
Class | Description
--- | ---
[PasswordVaultBottomSheetDialog](PasswordVaultBottomSheetDialog.md) | Class resembles the superclass for all bottom sheet dialogs and handles basic tasks like host management.
[PasswordVaultBottomSheetDialog.Callback](PasswordVaultBottomSheetDialog.Callback.md) | Class resembles a callback for a bottom sheet dialog that must be implemented by the host of a bottom sheet dialog.

<br/>

***

## MoreDialog Framework
The MoreDialog framework provides a dialog that displays a list of context actions that can be invoked. This framework is based on the [BottomSheetDialog Framework](#bottomsheetdialog-framework).

###### Classes
Class | Description
--- | ---
[Item](Item.md) | Class resembles the generic superclass for all context actions within a MoreDialog.
[ItemButton](ItemButton.md) | Class resembles a context action for a MoreDialog that works like a button.
[ItemCheckbox](ItemCheckbox.md) | Class resembles a context action for a MoreDialog that works like a checkbox.
[ItemDivider](ItemDivider.md) | Class implements a horizontal divider that visually divides multiple other context actions.
[MoreDialog](MoreDialog.md) | Class implements the bottom sheet dialog that shows a list of context actions to the user.
[MoreDialogCallback](MoreDialogCallback.md) | Interface can be implemented by the host of a MoreDialog which allows for callbacks to be made once a context action is invoked.

<br/>

***

2025-01-12  
&copy; Christian-2003
