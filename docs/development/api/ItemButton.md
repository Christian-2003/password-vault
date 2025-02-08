<img src="../../img/icon.png" height="150" align="right"/>

# ItemButton
```java
public class ItemButton extends Item
```
Class resembles an [`Item`](Item.md) for a [`MoreDialog`](MoreDialog.md) that works like a button. A user can click on this button, which invokes the [`MoreDialogCallback`](MoreDialogCallback.md).

###### Table of Contents
1. [Public Constructors](#public-constructors)
2. [Public Methods](#public-methods)

<br/>

## Summary

###### Public Constructors
Constructor | Description
--- | ---
[`ItemButton(String, String, int)`](#itembutton) | Constructor creates a new item button.

###### Public Methods
Method | Description
--- | ---
[`getIcon()`](#geticon) | Method returns the icon of the button.

<br/>

***

## ItemButton
```java
public ItemButton(
    @NonNull String title,
    @Nullable String tag,
    @DrawableRes int icon
)
```
Constructor instantiates a new item button with the title, tag and icon specified.

###### Parameters
Parameter | Description
--- | ---
`title` | Title for the item. This title is displayed to the user.
`tag` | Tag for the item. This tag is used to identify the item once a callback is invoked.
`icon` | Drawable resource for the icon of the button.

<br/>

***

## getIcon
```java
@DrawableRes
public int getIcon()
```
Method returns the drawable resource of the icon of the item button

###### Returns
Drawable resource of the icon of the item button.

<br/>

***

2025-01-12  
&copy; Christian-2003
