<img src="../../img/icon.png" height="150" align="right"/>

# RecyclerViewSwipeCallback.SwipeAction
```java
public static class RecyclerViewSwipeCallback.SwipeAction
```
Class models a swipe action for a recycler view adapter. Swipe actions can be created manually. However, most swipe actions within the app are identical (e.g. left swipe to edit and right swipe to delete). These swipe actions can be generated through the `makeLeftSwipeAction()` and `makeRightSwipeAction` methods.

###### Table of Contents
1. [Overview](#overview)
2. [Public Constructors](#public-constructors)
3. [Public Methods](#public-methods)

<br/>

## Overview
The `SwipeAction` allows the configuration of swipe actions. Swipe actions that correspond to the global configuration can be configured as follows:
```java
public class MyActivity extends PasswordVaultActivity<MyViewModel> {

    public MyActivity() {
        super(MyViewModel.class, R.layout.activity_my);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RecyclerViewSwipeCallback.SwipeAction leftSwipeAction = RecyclerViewSwipeCallback.makeLeftSwipeAction(this::onEdit, this::onDelete);
        RecyclerViewSwipeCallback.SwipeAction rightSwipeAction = RecyclerViewSwipeCallback.makeRightSwipeAction(this::onEdit, this::onDelete);
        // ...
    }


    private void onEdit(int position) {
        // Your code to edit an item goes here.
    }

    private void onDelete(int position) {
        // Your code to delete an item goes here.
    }

}
```

In order to create your own swipe actions, the following code can be used:
```java
public RecyclerViewSwipeCallback.SwipeAction createCustomSwipeAction() {
    OnRecyclerViewActionCallback actionCallback = (position) -> {
        // Your code goes here.
    }
    return new RecyclerViewSwipeCallback.SwipeAction(R.drawable.my_icon, R.color.my_color, actionCallback);
}
```

<br/>

## Summary
###### Public Constructors
Constructor | Description
--- | ---
[`SwipeAction`](#swipeaction) | Constructor instantiates a new swipe action.

###### Public Methods
Method | Description
--- | ---
[`getDrawableRes`](#getdrawableres) | Returns the resource ID of the drawable for the swipe action.
[`getColorRes`](#getcolorres) | Returns the color resource ID for the swipe action.
[`getSwipeListener`](#getswipelistener) | Returns the [`OnRecyclerViewActionListener`](OnRecyclerViewActionListener.md) called when the swipe action is invoked.

<br/>

***

## SwipeAction
```java
public SwipeAction(@DrawableRes int drawableRes, @ColorRes int colorRes, @NonNull OnRecyclerViewActionListener swipeListener)
```
Constructor instantiates a new swipe action.

###### Parameters
Parameter | Description
--- | ---
`drawableRes` | ID of the drawable resource that is displayed below the swipeable item once the user swipes.
`colorRes` | ID of the color resource that is displayed below the swipeable item once the user swipes.
`swipeListener` | Action listener invoked once the user swipes the item.

<br/>

***

## getDrawableRes
```java
@DrawableRes
public int getDrawableRes()
```
Returns the ID of the drawable resource displayed below the swipeable item once the user swipes.

###### Returns
ID of the drawable resource of the swipe action.

<br/>

## getColorRes
```java
@ColorRes
public int getColorRes()
```
Returns the ID of the color resource displayed below the swipeable item once the user swipes.

###### Returns
ID of the color resource of the swipe action.

<br/>

## getDrawableRes
```java
@NonNull
public OnRecyclerViewActionListener getSwipeListener()
```
Returns the [`OnRecyclerViewActionListener`](OnRecyclerViewActionListener.md) called when the swipe action is invoked. This action is invoked when the swipeable item is swiped past half of the item's area.

###### Returns
Action listener to invoke once the user successfully swipes the item.

<br/>

***

2024-08-25  
&copy; Chrsistian-2003
