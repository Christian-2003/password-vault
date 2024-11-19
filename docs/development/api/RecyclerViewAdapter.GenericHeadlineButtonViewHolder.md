<img src="../../img/icon.png" height="150" align="right"/>

# RecyclerViewAdapter.GenericHeadlineButtonViewHolder
```java
public static class RecyclerViewAdapter.GenericHeadlineButtonViewHolder extends RecyclerView.ViewHolder
```
View holder for the generic headline within pages. This headline can be clicked and displays an image at the right side of the view.

###### Table of Contents
1. [Overview](#overview)
2. [Public Fields](#public-fields)
3. [Public Constructors](#public-constructors)

<br/>

## Overview
Create a new view holder of this type as follows:
```java
@Override
@NonNull
public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    switch (viewType) {
        case TYPE_GENERIC_HEADLINE_BUTTON:
            View itemView = layoutInflater.inflate(R.layout.item_generic_headline_button, parent, false)
            return new GenericHeadlineButtonViewHolder(itemView);
        // ...
    }
}
```

An example for an inflated and updated headline button might be as follows:
<div align="center">
    <img src="../../img/development/api/recyclerviewadapter_genericheadlinebuttonviewholder_1.png" height="128"/>
</div>

<br/>

## Summary
###### Public Fields
Field | Description
--- | ---
[`headlineTextView`](#headlinetextview) | Text view displaying the text of the headline.
[`buttonImageView`](#buttonimageview) | Image view displaying the button image at the right side of the view.
[`dividerView`](#dividerview) | View displaying a divider at the top of the headline.

###### Public Constructors
Constructor | Description
--- | ---
[`GenericHeadlineButtonViewHolder(View)`](#genericheadlineviewholder) | Constructor to create a new view holder from the passed view.

<br/>

***

## headlineTextView
```java
public final TextView headlineTextView;
```
Text view displaying the text of the headline.

<br/>

## buttonImageView
```java
public final ImageView buttonImageView;
```
Image view displaying the button image at the right end of the view. The drawable of the view is `null` by default. Therefore, setting the drawable through `buttonImageView.setDrawable()` is required when updating the view holder.

<br/>

## dividerView
```java
public final View dividerView;
```
View displaying a divider at the top of the headline.

<br/>

***

## GenericHeadlineViewHolder
```
public GenericHeadlineViewHolder(View itemView)
```
Constructor instantiates a new view holder from the inflated view that is passed as parameter.

###### Parameters
Parameter | Description
--- | ---
`itemView` | Inflated view from which to create the view holder.

<br/>

***
2024-08-25  
&copy; Christian-2003
