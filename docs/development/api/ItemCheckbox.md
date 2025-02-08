<img src="../../img/icon.png" height="150" align="right"/>

# ItemCheckbox
```java
public class ItemCheckbox extends Item
```
Class resembles an [`Item`](Item.md) for a [`MoreDialog`](MoreDialog.md) that works like a checkbox. A user can click on this checkbox, which invokes the [`MoreDialogCallback`](MoreDialogCallback.md).

###### Table of Contents
1. [Public Constructors](#public-constructors)
2. [Public Methods](#public-methods)

<br/>

## Summary

###### Public Constructors
Constructor | Description
--- | ---
[`ItemCheckbox(String, String, boolean)`](#itemcheckbox) | Constructor creates a new item checkbox.

###### Public Methods
Method | Description
--- | ---
[`isChecked()`](#ischecked) | Method returns whether the item checkbox is checked.
[`setChecked(boolean)`](#setchecked) | Method changes whether the item checkbox is checked.

<br/>

***

## ItemCheckbox
```java
public ItemCheckbox(
    @NotNull String title,
    @Nullable String tag,
    boolean checked)
```
Constructor instantiates a new item checkbox with the title, tag and checked-state specified.

###### Parameters
Parameter | Description
--- | ---
`title` | Title for the item. This title is displayed to the user.
`tag` | Tag for the item. This tag is used to identify the item once a callback is invoked.
`checked` | Whether the checkbox button is checked.

<br/>

***

## isChecked
```java
public boolean isChecked()
```
Method returns whether the checkbox button is checked.

###### Returns
Whether the checkbox button is checked.

## setChecked
```java
public void setChecked(boolean checked)
```
Method changes whether the checkbox button is checked.

###### Parameters
Parameter | Description
--- | ---
`checked` | Whether the checkbox item is checked.

<br/>

***

2025-01-12  
&copy; Christian-2003
