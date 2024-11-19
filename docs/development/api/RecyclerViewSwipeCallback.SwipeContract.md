<img src="../../img/icon.png" height="150" align="right"/>

# RecyclerViewSwipeCallback.SwipeContract
```java
public interface RecyclerViewSwipeCallback.SwipeContract
```
Interface that needs to be implemented by a recycler view adapter in order to support swipe gestures.

###### Table of Contents
1. [Overview](#overview)
2. [Public Methods](#public-methods)

<br/>

## Overview
The `SwipeContract` needs to be implemented by a recycler view adapter that wants to support swiping.

THe [`supportsSwipe(int)`](#supportsswipe)-method determines whether an item at the queried position supports swiping.

<br/>

## Summary
###### Public Methods
Method | Description
--- | ---
[`getContext`](#getcontext) | Method returns the context of the recycler view adapter. _Always implemented by [`RecyclerViewAdapter<V extends ViewModel>`](RecyclerViewAdapter.md)_.
[`notifyItemChanged(int)`](#notifyitemchanged) | Method notifies the adapter that an item changed. _Always implemented by `RecyclerView.Adapter`_.
[`supportsSwipe(int)`](#supportsswipe) | Method determines whether an item within the adapter at the specified position supports swiping.

<br/>

***

## getContext
```java
Contex getContext();
```
Method returns the context of the adapter.

###### Returns
Context of the adapter.

<br/>

## notifyItemChanged
```java
void notifyItemChanged(int position);
```
Method notifies the adapter that an item at the specified position changed.

###### Parameters
Parameter | Description
--- | ---
`position` | Position of the item that changed.

<br/>

## supportsSwipe
```java
boolean supportsSwipe(int position);
```
Method determines whether the passed position supports swiping.

###### Parameters
Parameter | Description
--- | ---
`position` | Position to query.

###### Returns
Whether the queried position supports swiping.

<br/>

***
2024-08-25  
&copy; Christian-2003
