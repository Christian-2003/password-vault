<img src="../../img/icon.png" height="150" align="right"/>

# Item
```java
public abstract class Item implements Serializable
```
Class resembles the superclass for all context actions that can be invoked from a [`MoreDialog`](MoreDialog.md). Invoking such a context action will trigger the [`MoreDialogCallback`](MoreDialogCallback.md) implemented by the dialog host.

###### Table of Contents
1. [Public Fields](#public-fields)
2. [Public Constructors](#public-constructors)
3. [Public Methods](#public-methods)

<br/>

## Summary

###### Public Fields
Field | Description
--- | ---
[`TYPE_BUTTON`](#type_button) | Type indicating that a subclass of `Item` is a button.
[`TYPE_CHECKBOX`](#type_checkbox) | Type indicating that a subclass of `Item` is a checkbox.
[`TYPE_DIVIDER`](#type_divider) | Type indicating that a subclass of `Item` is a divider.

###### Public Constructors
Constructor | Description
--- | ---
[`Item(String, String, int)`](#item) | Constructor creates a new item.

###### Public Methods
Method | Description
--- | ---
[`getTitle()`](#gettitle) | Method returns the title of the context action.
[`getTag()`](#gettag) | Method returns the tag of the context action.
[`getType()`](#gettype) | Method returns the type of the context action.

<br/>

***

## TYPE_BUTTON
```java
public static final int TYPE_BUTTON = 2;
```
Type indicates that an `Item` is an [`ItemButton`](ItemButton.md). This is used by the internally by the MoreDialog to prevent costly `instanceof`-calls.

## TYPE_CHECKBOX
```java
public static final int TYPE_CHECKBOX = 4;
```
Type indicates that an `Item` is an [`ItemCheckbox`](ItemCheckbox.md). This is used by the internally by the MoreDialog to prevent costly `instanceof`-calls.

## TYPE_DIVIDER
```java
public static final int TYPE_DIVIDER = 8;
```
Type indicates that an `Item` is an [`ItemDivider`](ItemDivider.md). This is used by the internally by the MoreDialog to prevent costly `instanceof`-calls.

<br/>

***

## Item
```java
public Item(
    @NonNull String title,
    @Nullable String tag,
    int type
)
```
Constructor instantiates a new item with the title, tag and type specified.

###### Parameters
Parameter | Description
--- | ---
`title` | Title for the item. This title is displayed to the user.
`tag` | Tag for the item. This tag is used to identify the item once a callback is invoked.
`type` | Type for the item used to prevent costly `instanceof`-calls.

<br/>

***

## getTitle
```java
@NonNull
public String getTitle()
```
Method returns the title of the item. This title is displayed to the user.

###### Returns
Title of the item.

## getTag
```java
@Nullable
public String getTag()
```
Method returns the tag of the item. This tag is used to identify a specific item once it is invoked.

###### Returns
Tag of the item.

## getType
```java
public int getType()
```
Method returns the type of the item. This type is used to prevent costly `instanceof`-calls.

###### Returns
Type of the item.

<br/>

***

2025-01-12  
&copy; Christian-2003
